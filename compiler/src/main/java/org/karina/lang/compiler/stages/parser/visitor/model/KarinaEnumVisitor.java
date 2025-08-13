package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaEnumVisitor implements IntoContext {

    private final RegionContext context;
    private final KarinaUnitVisitor visitor;

    public KarinaEnumVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.visitor = base;
        this.context = regionContext;
    }

    public KClassModel visit(
            KClassModel owningClass,
            ObjectPath owningPath,
            ImmutableList<KAnnotation> annotations,
            KarinaParser.EnumContext ctx,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        var currentClassPointer = ClassPointer.of(region, path);
        final var mods = (ctx.PUB() != null ? Modifier.PUBLIC : 0) | Modifier.INTERFACE | Modifier.ABSTRACT;

        KType.ClassType superClass = KType.ROOT;

        var interfaces = ImmutableList.<KType.ClassType>builder();

        var methods = ImmutableList.<KMethodModel>builder();
        for (var functionContext : ctx.function()) {
            methods.add(this.visitor.methodVisitor.visit(currentClassPointer, ImmutableList.of(), functionContext, false));
        }

        for (var implCtx : ctx.implementation()) {
            //pointer have to be validated in the import stage
            var structType = this.visitor.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(region, structType.name().value());
            var clsType = classPointer.implement(structType.generics());
            interfaces.add(clsType);

            for (var functionContext : implCtx.function()) {
                methods.add(this.visitor.methodVisitor.visit(
                        currentClassPointer, ImmutableList.of(),
                        functionContext, true
                ));
            }
        }



        var fields = ImmutableList.<KFieldModel>builder();

        var constNames = new ArrayList<String>();
        var constValues = new ArrayList<KExpr>();
        for (var constContext : ctx.const_()) {
            var visitExpression = this.visitor.exprVisitor.visitExpression(constContext.expression());
            var constModel = this.visitor.visitConst(constContext, visitExpression, currentClassPointer);
            constNames.add(constModel.name());
            constValues.add(visitExpression);
            fields.add(constModel);
        }
        if (!constNames.isEmpty()) {
            var clinit = KarinaUnitVisitor.createStaticConstructor(
                    this,
                    region,
                    currentClassPointer,
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

        var host = owningClass.pointer();
        if (owningClass.nestHost() != null) {
            host = owningClass.nestHost();
        }

        var innerClassesToFill = new ArrayList<KClassModel>();
        var permittedSubClassesToFill = new ArrayList<ClassPointer>();
        var nestMembersToFill = new ArrayList<ClassPointer>();
        var classModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
                host,
                interfaces.build(),
                innerClassesToFill,
                fields.build(),
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
            var enumMember = innerEnumClass(classModel, currentClassPointer, path, enumMemberContext, generics, modelBuilder, ctx.PUB() != null);
            innerClassesToFill.add(enumMember);
            permittedSubClassesToFill.add(enumMember.pointer());
        }
        modelBuilder.addClass(this, classModel);

        return classModel;
    }

    private KClassModel innerEnumClass(
            KClassModel enumClass,
            ClassPointer enumInterfacePointer,
            ObjectPath owningPath,
            KarinaParser.EnumMemberContext ctx,
            List<Generic> genericsOuter,
            ModelBuilder modelBuilder,
            boolean asPublic
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());

        var path = owningPath.append(name);
        var currentClass = ClassPointer.of(region, path);
        KType.ClassType superClass = KType.ROOT;
        var mods = (asPublic ? Modifier.PUBLIC : 0) | Modifier.FINAL;


        var innerClasses = ImmutableList.<KClassModel>of();
        var fields = ImmutableList.<KFieldModel>builder();

        var parameters = ctx.parameterList();
        if (parameters != null) {
            for (var parameterContext : parameters.parameter()) {
                var enumField = enumField(parameterContext, currentClass);
                fields.add(enumField);
            }
        }


        var fieldModels = fields.build();
        var constructor = KarinaStructVisitor.createDefaultConstructor(region, currentClass, fieldModels, Modifier.PUBLIC, superClass);
        var methods = ImmutableList.<KMethodModel>builder();
        methods.add(constructor);

        for (var fieldModel : fieldModels) {
            methods.add(createGetterMethod(fieldModel));
        }
        methods.add(KarinaStructVisitor.createToStringMethod(
                region,
                enumClass.name() + "::" + name,
                currentClass,
                fieldModels
        ));

        var generics = ImmutableList.copyOf(genericsOuter.stream().map(ref -> {
            var generic = new Generic(region, ref.name());
            generic.updateBounds(KType.ROOT, List.of());
            return generic;
        }).toList());
        var imports = ImmutableList.<KImport>of();
        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var mappedGenerics = generics.stream().map(ref -> (KType) new KType.GenericLink(ref)).toList();
        var enumInterfaceClassType =enumInterfacePointer.implement(mappedGenerics);
        var interfaces = ImmutableList.of(enumInterfaceClassType);

        var host = enumClass.pointer();
        if (enumClass.nestHost() != null) {
            host = enumClass.nestHost();
        }

        var annotations = ImmutableList.<KAnnotation>of();
        var nestMembers =  new ArrayList<ClassPointer>();
        var enumClassInner = new KClassModel(
                name,
                path,
                mods,
                superClass,
                enumClass,
                host,
                interfaces,
                innerClasses,
                fieldModels,
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                nestMembers,
                annotations,
                this.context.source(),
                region,
                null
        );
        modelBuilder.addClass(this, enumClassInner);

        return enumClassInner;

    }

    // TODO generate function for common fields
    private KMethodModel createGetterMethod(FieldModel fieldModel) {
        var region = fieldModel.region();
        var expr = new KExpr.GetMember(
                region,
                new KExpr.Self(region, null),
                RegionOf.region(region, fieldModel.name()),
                false,
                null
        );

        return new KMethodModel(
                fieldModel.name(),
                Modifier.PUBLIC | Modifier.FINAL | Opcodes.ACC_SYNTHETIC,
                new Signature(ImmutableList.of(), fieldModel.type()),
                ImmutableList.of(),
                ImmutableList.of(),
                expr,
                ImmutableList.of(), region,
                fieldModel.classPointer(),
                List.of()
        );
    }

    private KFieldModel enumField(KarinaParser.ParameterContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var type = this.visitor.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PRIVATE | Modifier.FINAL;
        return new KFieldModel(name, type, mods, region, owningClass, null);


    }

    @Override
    public Context intoContext() {
        return this.visitor.context();
    }
}
