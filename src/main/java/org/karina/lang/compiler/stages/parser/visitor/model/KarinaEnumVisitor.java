package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.parser.TextContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.lang.reflect.Modifier;
import java.util.List;

public class KarinaEnumVisitor {

    private final TextContext context;
    private final KarinaUnitVisitor base;

    public KarinaEnumVisitor(KarinaUnitVisitor base, TextContext textContext) {
        this.base = base;
        this.context = textContext;
    }

    public ClassPointer visit(@Nullable ClassPointer owningClass, ObjectPath owningPath, KarinaParser.EnumContext ctx, JKModelBuilder builder) {

        var name = ctx.ID().getText();
        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(path);
        var mods = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT | Modifier.STATIC;

        ClassPointer superClass = null;

        var interfaces = ImmutableList.<ClassPointer>of();

        var methods = ImmutableList.<KMethodModel>builder();
        for (var functionContext : ctx.function()) {
            methods.add(this.base.methodVisitor.visit(currentClass, functionContext));
        }

        var fields = ImmutableList.<KFieldModel>of();

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KTree.KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>builder();
        var innerClasses = ImmutableList.<ClassPointer>builder();

        for (var enumMemberContext : ctx.enumMember()) {
            var enumMember = innerEnumClass(currentClass, path, enumMemberContext, builder, generics);
            innerClasses.add(enumMember);
            permittedSubClasses.add(enumMember);
        }

        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                interfaces,
                innerClasses.build(),
                fields,
                methods.build(),
                generics,
                imports,
                permittedSubClasses.build(),
                this.context.source(),
                this.context.toRegion(ctx)
        );

        builder.addClass(classModel);

        return classModel.pointer();
    }

    private ClassPointer innerEnumClass(ClassPointer interfacePointer, ObjectPath owningPath, KarinaParser.EnumMemberContext ctx, JKModelBuilder builder, List<Generic> genericsOuter) {
        var region = this.context.toRegion(ctx);
        var name = ctx.ID().getText();

        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(path);
        var superClass = ClassPointer.ROOT;
        var mods = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;

        var interfaces = ImmutableList.of(interfacePointer);
        var innerClasses = ImmutableList.<ClassPointer>of();
        var fields = ImmutableList.<KFieldModel>builder();

        var parameters = ctx.parameterList();
        if (parameters != null) {
            for (var parameterContext : parameters.parameter()) {
                var enumField = enumField(parameterContext, currentClass);
                fields.add(enumField);
            }
        }

        var constructor = KarinaStructVisitor.createConstructor(region, currentClass, fields.build(), Modifier.PUBLIC);
        var methods = ImmutableList.of(constructor);

        //TODO add generics from super class
        var generics = ImmutableList.copyOf(genericsOuter.stream().map(ref -> new Generic(region, ref.name())).toList());
        var imports = ImmutableList.<KTree.KImport>of();
        var permittedSubClasses = ImmutableList.<ClassPointer>of();


        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                interfacePointer,
                interfaces,
                innerClasses,
                fields.build(),
                methods,
                generics,
                imports,
                permittedSubClasses,
                this.context.source(),
                region
        );

        builder.addClass(classModel);

        return classModel.pointer();
    }

    private KFieldModel enumField(KarinaParser.ParameterContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = ctx.ID().getText();
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC | Modifier.FINAL;
        return new KFieldModel(name, type, mods, region, owningClass);


    }
}
