package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KAnnotation;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaStructVisitor {
    private final RegionContext context;
    private final KarinaUnitVisitor base;

    public KarinaStructVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.base = base;
        this.context = regionContext;
    }

    public KClassModel visit(
            @Nullable KClassModel owningClass,
            ObjectPath owningPath,
            ImmutableList<KAnnotation> annotations,
            KarinaParser.StructContext ctx,
            ModelBuilder modelBuilder
    ) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        //assume to be valid, as you can only get this, if the class is valid
        var currentClass = ClassPointer.of(region, path);
        var mods = Modifier.PUBLIC;

        var superClass = KType.ROOT;

        var superAnnotation = processAnnotations(annotations);
        if (superAnnotation != null) {
            if (superAnnotation.superType() instanceof KType.UnprocessedType superType) {
                var superPath = superType.name().value();
                var superClassPointer = ClassPointer.of(region, superPath);
                superClass = new KType.ClassType(superClassPointer, superType.generics());
            } else if (superAnnotation.superType() instanceof KType.ClassType superType) {
                superClass = superType;
            } else {
                Log.temp(superAnnotation.region(), "Invalid super fieldType");
                throw new Log.KarinaException();
            }
        }


        var interfaces = ImmutableList.<KType.ClassType>builder();
        var methods = ImmutableList.<KMethodModel>builder();


        for (var implCtx : ctx.implementation()) {
            //pointer have to be validated in the import stage
            var structType = this.base.typeVisitor.visitStructType(implCtx.structType());
            var classPointer = ClassPointer.of(region, structType.name().value());
            var clsType = new KType.ClassType(classPointer, structType.generics());
            interfaces.add(clsType);

            for (var functionContext : implCtx.function()) {
                methods.add(this.base.methodVisitor.visit(
                        currentClass, ImmutableList.of(),
                        functionContext
                ));
            }
        }

        boolean containsConstructor = false;
        for (var functionContext : ctx.function()) {
            var methodModel = this.base.methodVisitor.visit(
                    currentClass,
                    ImmutableList.of(),
                    functionContext
            );
            methods.add(methodModel);

            if (methodModel.name().equals("<init>")) {
                containsConstructor = true;
            }
        }

        var fields = ImmutableList.<KFieldModel>builder();

        for (var fieldContext : ctx.field()) {
            fields.add(this.visitField(fieldContext, currentClass));
        }


        if (!containsConstructor) {
            var constructor = createDefaultConstructor(region, currentClass, fields.build(), Modifier.PUBLIC, superClass);
            methods.add(constructor);
        }

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>of();



        var newModel = new KClassModel(
                name,
                path,
                mods,
                superClass,
                owningClass,
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
        modelBuilder.addClass(newModel);

        return newModel;
    }

    private KFieldModel visitField(KarinaParser.FieldContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC;
        return new KFieldModel(name, type, mods, region, owningClass);

    }

    public static KMethodModel createDefaultConstructor(
            Region region,
            ClassPointer classPointer,
            List<KFieldModel> fields,
            int mods,
            KType superClass
    ) {
        var name = "<init>";

        var paramTypes = ImmutableList.copyOf(fields.stream().map(KFieldModel::type).toList());
        var paramNames = ImmutableList.copyOf(fields.stream().map(KFieldModel::name).toList());
        var signature = new Signature(paramTypes, KType.NONE);
        var generics = ImmutableList.<Generic>of();

        //TODO fill super call with parameters,
        var expressions = new ArrayList<KExpr>();

        var superLiteral = new KExpr.SpecialCall(
                region,
                new InvocationType.SpecialInvoke(
                        "<init>",
                        superClass
                )
        );
        var arguments = List.<KExpr>of();
        var superCall = new KExpr.Call(region, superLiteral, List.of(), arguments, null);
        expressions.add(superCall);



        for (var field : fields) {
            var self = new KExpr.Self(region, null);
            var fieldName = RegionOf.region(region, field.name());
            var rhs = new KExpr.Literal(region, field.name(), null);
            var lhs = new KExpr.GetMember(region, self, fieldName, false, null);
            var assign = new KExpr.Assignment(region, lhs, rhs, null);
            expressions.add(assign);
        }




        var expression = new KExpr.Block(region, expressions, null, false);

        return new KMethodModel(
                name,
                mods,
                signature,
                paramNames,
                generics,
                expression,
                ImmutableList.of(),
                region,
                classPointer,
                List.of()
        );
    }

    private @Nullable SuperAnnotation processAnnotations(ImmutableList<KAnnotation> annotations) {
        KAnnotation superAnnotation = null;
        for (var annotation : annotations) {
            if (annotation.name().equals("Super")) {
                if (superAnnotation != null) {
                    Log.temp(annotation.region(), "Duplicate 'Super' annotation");
                    throw new Log.KarinaException();
                }
                superAnnotation = annotation;
            } else {
                Log.warn(annotation.region(), "Unexpected annotation: " + annotation.name());
            }
        }
        if (superAnnotation != null) {
            return SuperAnnotation.fromAnnotation(superAnnotation.value());
        } else {
            return null;
        }

    }


}
