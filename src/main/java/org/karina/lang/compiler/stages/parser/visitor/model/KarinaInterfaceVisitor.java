package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KAnnotation;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaInterfaceVisitor {

    private final RegionContext context;
    private final KarinaUnitVisitor base;

    public KarinaInterfaceVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.base = base;
        this.context = regionContext;
    }

    public KClassModel visit(
            @Nullable KClassModel owningClass,
            ObjectPath owningPath,
            ImmutableList<KAnnotation> annotations,
            KarinaParser.InterfaceContext ctx,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(region, path);
        var mods = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT;

        KType.ClassType superClass = KType.ROOT;

        var interfaces = ImmutableList.<KType.ClassType>builder();

        for (var implCtx : ctx.interfaceExtension()) {
            //TODO has to be validated
            var structType = this.base.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(region, structType.name().value());
            var clsType = new KType.ClassType(classPointer, structType.generics());
            interfaces.add(clsType);
        }

        var methods = ImmutableList.<KMethodModel>builder();
        for (var functionContext : ctx.function()) {
            var method = this.base.methodVisitor.visit(
                    currentClass, ImmutableList.of(),
                    functionContext
            );
            methods.add(method);

            if (method.name().equals("<init>")) {
                Log.attribError(new AttribError.UnqualifiedSelf(region, method.region()));
                throw new Log.KarinaException();
            }
        }

        var fields = ImmutableList.<KFieldModel>of();

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                interfaces.build(),
                List.of(),
                fields,
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
        modelBuilder.addClass(classModel);

        return classModel;
    }

}
