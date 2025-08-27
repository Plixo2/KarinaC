package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaInterfaceVisitor implements IntoContext {

    private final RegionContext context;
    private final KarinaUnitVisitor visitor;

    public KarinaInterfaceVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.visitor = base;
        this.context = regionContext;
    }

    public KClassModel visit(
            KClassModel owningClass,
            ObjectPath owningPath,
            ImmutableList<KAnnotation> annotations,
            KarinaParser.InterfaceContext ctx,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(region, path);
        // interfaces cannot be private
        final var mods = (ctx.PUB() != null ? Modifier.PUBLIC : 0) | Modifier.INTERFACE | Modifier.ABSTRACT;

        KType.ClassType superClass = KType.ROOT;

        var interfaces = ImmutableList.<KType.ClassType>builder();

        for (var implCtx : ctx.interfaceExtension()) {
            var structType = this.visitor.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(region, structType.name().value());
            var clsType = classPointer.implement(structType.generics());
            interfaces.add(clsType);
        }

        var methods = ImmutableList.<KMethodModel>builder();
        for (var functionContext : ctx.function()) {
            var method = this.visitor.methodVisitor.visit(
                    currentClass, ImmutableList.of(),
                    functionContext, false
            );
            methods.add(method);

            if (method.name().equals("<init>")) {
                Log.error(this, new AttribError.UnqualifiedSelf(region, method.region()));
                throw new Log.KarinaException();
            }
        }

        var fields = ImmutableList.<KFieldModel>builder();

        var constNames = new ArrayList<String>();
        var constValues = new ArrayList<KExpr>();
        for (var constContext : ctx.const_()) {
            var visitExpression = this.visitor.exprVisitor.visitExpression(constContext.expression());
            var constModel = this.visitor.visitConst(constContext, visitExpression, currentClass);
            constNames.add(constModel.name());
            constValues.add(visitExpression);
            fields.add(constModel);
        }
        if (!constNames.isEmpty()) {
            var clinit = KarinaUnitVisitor.createStaticConstructor(
                    this,
                    region,
                    currentClass,
                    constNames,
                    constValues
            );
            methods.add(clinit);
        }

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.visitor.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var host = owningClass.pointer();
        if (owningClass.nestHost() != null) {
            host = owningClass.nestHost();
        }

        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                host,
                interfaces.build(),
                List.of(),
                fields.build(),
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                new ArrayList<>(),
                annotations,
                this.context.source(),
                region,
                null
        );
        modelBuilder.addClass(this, classModel);

        return classModel;
    }

    @Override
    public Context intoContext() {
        return this.visitor.context();
    }
}
