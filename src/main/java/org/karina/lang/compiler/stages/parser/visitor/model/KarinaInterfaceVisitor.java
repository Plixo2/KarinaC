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

import java.lang.reflect.Modifier;

public class KarinaInterfaceVisitor {

    private final TextContext context;
    private final KarinaUnitVisitor base;

    public KarinaInterfaceVisitor(KarinaUnitVisitor base, TextContext textContext) {
        this.base = base;
        this.context = textContext;
    }

    public ClassPointer visit(@Nullable ClassPointer owningClass, ObjectPath owningPath, KarinaParser.InterfaceContext ctx, JKModelBuilder builder) {
        var name = ctx.ID().getText();
        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(path);
        var mods = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT | Modifier.STATIC;

        ClassPointer superClass = null;
        var innerClasses = ImmutableList.<ClassPointer>of();

        var interfaces = ImmutableList.<ClassPointer>builder();

        for (var implCtx : ctx.interfaceExtension()) {
            //TODO has to be validated
            var structType = this.base.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(structType.name().value());
            interfaces.add(classPointer);
        }

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

        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                interfaces.build(),
                innerClasses,
                fields,
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                this.context.source(),
                this.context.toRegion(ctx)
        );

        builder.addClass(classModel);

        return classModel.pointer();
    }

}
