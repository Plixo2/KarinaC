package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KAnnotation;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaEnumVisitor {

    private final RegionContext context;
    private final KarinaUnitVisitor base;

    public KarinaEnumVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.base = base;
        this.context = regionContext;
    }

    public KClassModel visit(
            @Nullable KClassModel owningClass,
            ObjectPath owningPath,
            ImmutableList<KAnnotation> annotations,
            KarinaParser.EnumContext ctx,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        var currentClassPointer = ClassPointer.of(region, path);
        var mods = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT;

        KType.ClassType superClass = KType.ROOT;

        var interfaces = ImmutableList.<KType.ClassType>of();

        var methods = ImmutableList.<KMethodModel>builder();
        for (var functionContext : ctx.function()) {
            methods.add(this.base.methodVisitor.visit(currentClassPointer, ImmutableList.of(), functionContext));
        }
        //TODO add constructor method for easy initialization

        var fields = ImmutableList.<KFieldModel>of();

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KImport>of();



        var innerClassesToFill = new ArrayList<KClassModel>();
        var permittedSubClassesToFill = new ArrayList<ClassPointer>();
        var nestMembersToFill = new ArrayList<ClassPointer>();
        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                interfaces,
                innerClassesToFill,
                fields,
                methods.build(),
                generics,
                imports,
                permittedSubClassesToFill,
                nestMembersToFill,
                annotations,
                this.context.source(),
                region,
                null
        );

        for (var enumMemberContext : ctx.enumMember()) {
            var enumMember = innerEnumClass(classModel, currentClassPointer, path, enumMemberContext, generics, modelBuilder);
            innerClassesToFill.add(enumMember);
            permittedSubClassesToFill.add(enumMember.pointer());
            nestMembersToFill.add(enumMember.pointer());
        }
        modelBuilder.addClass(classModel);


        return classModel;
    }

    private KClassModel innerEnumClass(
            KClassModel enumClass,
            ClassPointer enumInterfacePointer,
            ObjectPath owningPath,
            KarinaParser.EnumMemberContext ctx,
            List<Generic> genericsOuter,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());

        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(region, path);
        KType.ClassType superClass = KType.ROOT;
        var mods = Modifier.PUBLIC | Modifier.FINAL;


        var innerClasses = ImmutableList.<KClassModel>of();
        var fields = ImmutableList.<KFieldModel>builder();

        var parameters = ctx.parameterList();
        if (parameters != null) {
            for (var parameterContext : parameters.parameter()) {
                var enumField = enumField(parameterContext, currentClass);
                fields.add(enumField);
            }
        }

        var constructor = KarinaStructVisitor.createDefaultConstructor(region, currentClass, fields.build(), Modifier.PUBLIC, superClass);
        var methods = ImmutableList.of(constructor);


        var generics = ImmutableList.copyOf(genericsOuter.stream().map(ref -> {
            var generic = new Generic(region, ref.name());
            generic.updateBounds(KType.ROOT, List.of());
            return generic;
        }).toList());
        var imports = ImmutableList.<KImport>of();
        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var mappedGenerics = generics.stream().map(ref -> (KType) new KType.GenericLink(ref)).toList();
        var enumInterfaceClassType = new KType.ClassType(enumInterfacePointer, mappedGenerics);
        var interfaces = ImmutableList.of(enumInterfaceClassType);

        var annotations = ImmutableList.<KAnnotation>of();
        var nestMembers =  new ArrayList<ClassPointer>();
        var enumClassInner = new KClassModel(
                name,
                path,
                mods,
                superClass,
                enumClass,
                interfaces,
                innerClasses,
                fields.build(),
                methods,
                generics,
                imports,
                permittedSubClasses,
                nestMembers,
                annotations,
                this.context.source(),
                region,
                null
        );
        modelBuilder.addClass(enumClassInner);

        return enumClassInner;

    }

    private KFieldModel enumField(KarinaParser.ParameterContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC | Modifier.FINAL;
        return new KFieldModel(name, type, mods, region, owningClass);


    }
}
