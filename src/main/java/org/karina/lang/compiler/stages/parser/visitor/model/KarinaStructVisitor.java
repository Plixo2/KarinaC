package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.PhaseDebug;
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

    public KClassModel visit(@Nullable KClassModel owningClass, ObjectPath owningPath, ImmutableList<KAnnotation> annotations, KarinaParser.StructContext ctx) {
        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var path = owningPath.append(name);
        //assume to be valid, as you can only get this, if the class is valid
        var currentClass = ClassPointer.of(region, path);
        var mods = Modifier.PUBLIC | Modifier.STATIC;

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

        for (var functionContext : ctx.function()) {
            methods.add(this.base.methodVisitor.visit(currentClass, ImmutableList.of(), functionContext));
        }

        var fields = ImmutableList.<KFieldModel>builder();

        for (var fieldContext : ctx.field()) {
            fields.add(this.visitField(fieldContext, currentClass));
        }



        var constructor = createConstructor(region, currentClass, fields.build(), Modifier.PUBLIC, superAnnotation);
        methods.add(constructor);

        var generics = ImmutableList.<Generic>of();
        if (ctx.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(ctx.genericHintDefinition()));
        }

        var imports = ImmutableList.<KImport>of();

        var permittedSubClasses = ImmutableList.<ClassPointer>of();



        return new KClassModel(
                PhaseDebug.LOADED,
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
    }

    private KFieldModel visitField(KarinaParser.FieldContext ctx, ClassPointer owningClass) {

        var region = this.context.toRegion(ctx);
        var name = this.context.escapeID(ctx.id());
        var type = this.base.typeVisitor.visitType(ctx.type());
        var mods = Modifier.PUBLIC;
        return new KFieldModel(name, type, mods, region, owningClass);

    }

    public static KMethodModel createConstructor(
            Region region,
            ClassPointer classPointer,
            List<KFieldModel> fields,
            int mods,
            @Nullable SuperAnnotation superAnnotation
    ) {
        var name = "<init>";

        var callRegion = region;
        if (superAnnotation != null) {
            region = superAnnotation.region();
            callRegion = superAnnotation.expr().region();
        }

        var paramTypes = ImmutableList.copyOf(fields.stream().map(KFieldModel::type).toList());
        var paramNames = ImmutableList.copyOf(fields.stream().map(KFieldModel::name).toList());
        var signature = new Signature(paramTypes, KType.VOID);
        var generics = ImmutableList.<Generic>of();

        //TODO fill super call with parameters,
        var expressions = new ArrayList<KExpr>();

        KType superType = KType.ROOT;

        if (superAnnotation != null) {
            superType = superAnnotation.superType();
        }

        var superLiteral = new KExpr.SpecialCall(
                callRegion,
                new InvocationType.SpecialInvoke(
                        "<init>",
                        superType
                )
        );
        var arguments = List.<KExpr>of();

        if (superAnnotation != null) {
            var expr = superAnnotation.expr();
//            if (expr instanceof KExpr.Block block && !block.expressions().isEmpty()) {
//                expr = block.expressions().getLast();
//            }

            if (!(expr instanceof KExpr.Call call)) {
                Log.temp(expr.region(), "Expected a call expression");
                throw new Log.KarinaException();
            }
            if (!(call.left() instanceof KExpr.SpecialCall(var _, InvocationType.Unknown()))) {
                Log.temp(expr.region(), "Expected a call to super");
                throw new Log.KarinaException();
            }
            arguments = call.arguments();
        }

        for (var field : fields) {
            var self = new KExpr.Self(region, null);
            var fieldName = RegionOf.region(region, field.name());
            var lhs = new KExpr.GetMember(region, self, fieldName, null);
            var rhs = new KExpr.Literal(region, field.name(), null);
            var assign = new KExpr.Assignment(region, lhs, rhs, null);
            expressions.add(assign);
        }


        var superCall = new KExpr.Call(callRegion, superLiteral, List.of(), arguments, null);
        expressions.add(superCall);

        var expression = new KExpr.Block(region, expressions, null);

        return new KMethodModel(
                name,
                mods,
                signature,
                paramNames,
                generics,
                expression,
                ImmutableList.of(),
                region,
                classPointer
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
