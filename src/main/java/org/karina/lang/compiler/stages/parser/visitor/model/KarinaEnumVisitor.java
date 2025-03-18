package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.PhaseDebug;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
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

public class KarinaEnumVisitor {

    private final RegionContext context;
    private final KarinaUnitVisitor base;

    public KarinaEnumVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.base = base;
        this.context = regionContext;
    }

    public KClassModel visit(@Nullable KClassModel owningClass, ObjectPath owningPath,  ImmutableList<KAnnotation> annotations, KarinaParser.EnumContext ctx) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        var currentClassPointer = ClassPointer.of(region, path);
        var mods = Modifier.PUBLIC | Modifier.INTERFACE | Modifier.ABSTRACT | Modifier.STATIC;

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
                PhaseDebug.LOADED,
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
            var enumMember = innerEnumClass(classModel, currentClassPointer, path, enumMemberContext, generics);
            innerClassesToFill.add(enumMember);
            permittedSubClassesToFill.add(enumMember.pointer());
            nestMembersToFill.add(enumMember.pointer());
        }


        return classModel;
    }

    private KClassModel innerEnumClass(KClassModel enumClass, ClassPointer enumInterfacePointer, ObjectPath owningPath, KarinaParser.EnumMemberContext ctx, List<Generic> genericsOuter) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());

        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(region, path);
        KType.ClassType superClass = KType.ROOT;
        var mods = Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;


        var innerClasses = ImmutableList.<KClassModel>of();
        var fields = ImmutableList.<KFieldModel>builder();

        var parameters = ctx.parameterList();
        if (parameters != null) {
            for (var parameterContext : parameters.parameter()) {
                var enumField = enumField(parameterContext, currentClass);
                fields.add(enumField);
            }
        }

        var constructor = KarinaStructVisitor.createConstructor(region, currentClass, fields.build(), Modifier.PUBLIC, null);
        var methods = ImmutableList.of(constructor);


        var generics = ImmutableList.copyOf(genericsOuter.stream().map(ref -> new Generic(region, ref.name())).toList());
        var imports = ImmutableList.<KImport>of();
        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var mappedGenerics = generics.stream().map(ref -> (KType) new KType.GenericLink(ref)).toList();
        var enumInterfaceClassType = new KType.ClassType(enumInterfacePointer, mappedGenerics);
        var interfaces = ImmutableList.of(enumInterfaceClassType);

        var annotations = ImmutableList.<KAnnotation>of();
        var nestMembers =  new ArrayList<ClassPointer>();
        return new KClassModel(
                PhaseDebug.LOADED,
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

    }

    private KFieldModel enumField(KarinaParser.ParameterContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC | Modifier.FINAL;
        return new KFieldModel(name, type, mods, region, owningClass);


    }
}
