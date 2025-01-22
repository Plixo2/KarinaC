package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.TextContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaStructVisitor {
    private final TextContext context;
    private final KarinaUnitVisitor base;

    public KarinaStructVisitor(KarinaUnitVisitor base, TextContext textContext) {
        this.base = base;
        this.context = textContext;
    }

    public ClassPointer visit(@Nullable ClassPointer owningClass, ObjectPath owningPath, KarinaParser.StructContext ctx, JKModelBuilder builder) {
        var region = this.context.toRegion(ctx);
        var name = ctx.ID().getText();
        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(path);
        var mods = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

        var superClass = ClassPointer.ROOT;
        var innerClasses = ImmutableList.<ClassPointer>of();

        var interfaces = ImmutableList.<ClassPointer>builder();
        var methods = ImmutableList.<KMethodModel>builder();


        for (var implCtx : ctx.implementation()) {
            //TODO has to be validated
            var structType = this.base.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(structType.name().value());
            interfaces.add(classPointer);

            for (var functionContext : implCtx.function()) {
                methods.add(this.base.methodVisitor.visit(currentClass, functionContext));
            }
        }

        for (var functionContext : ctx.function()) {
            methods.add(this.base.methodVisitor.visit(currentClass, functionContext));
        }

        var fields = ImmutableList.<KFieldModel>builder();

        for (var fieldContext : ctx.field()) {
            fields.add(this.visitField(fieldContext, currentClass));
        }

        var constructor = createConstructor(region, currentClass, fields.build(), Modifier.PUBLIC);
        methods.add(constructor);

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KTree.KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>of();


        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                interfaces.build(),
                innerClasses,
                fields.build(),
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                this.context.source(), region
        );

        builder.addClass(classModel);

        return classModel.pointer();
    }

    private KFieldModel visitField(KarinaParser.FieldContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = ctx.ID().getText();
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC;
        return new KFieldModel(name, type, mods, region, owningClass);

    }

    public static KMethodModel createConstructor(Region region, ClassPointer classPointer, List<KFieldModel> fields, int mods) {
        var name = "<init>";

        var paramTypes = ImmutableList.copyOf(fields.stream().map(KFieldModel::type).toList());
        var paramNames = ImmutableList.copyOf(fields.stream().map(KFieldModel::name).toList());
        var signature = new Signature(paramTypes, new KType.PrimitiveType(KType.KPrimitive.VOID));
        var generics = ImmutableList.<Generic>of();

        //TODO super call
        var expressions = new ArrayList<KExpr>();
        var superLiteral = new KExpr.Super(region);
        var superCall = new KExpr.Call(region, superLiteral, List.of(), List.of(), null);
        expressions.add(superCall);

        for (var field : fields) {
            var self = new KExpr.Self(region, null);
            var fieldName = RegionOf.region(region, field.name());
            var lhs = new KExpr.GetMember(region, self, fieldName, null);
            var rhs = new KExpr.Literal(region, field.name(), null);
            var assign = new KExpr.Assignment(region, lhs, rhs, null);
            expressions.add(assign);
        }
        var expression = new KExpr.Block(region, expressions, null);

        return new KMethodModel(
                name,
                mods,
                signature,
                paramNames,
                generics,
                expression,
                region,
                classPointer
        );
    }
}
