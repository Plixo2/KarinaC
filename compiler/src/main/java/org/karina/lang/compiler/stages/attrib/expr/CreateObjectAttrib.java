package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CreateObjectAttrib  {
    public static AttributionExpr attribCreateObject(@Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) {


        if (!(expr.createType() instanceof KType.ClassType classType)) {
            Log.error(ctx, new AttribError.NotAClass(expr.region(), expr.createType()));
            throw new Log.KarinaException();
        }

        var owningClass = ctx.model().getClass(ctx.owningClass());
        var referenceClass = ctx.model().getClass(classType.pointer());

        if (!Types.isTypeAccessible(ctx.protection(), owningClass, classType)) {
            Log.error(ctx, new ImportError.AccessViolation(
                    expr.region(),
                    owningClass.name(),
                    RegionOf.region(referenceClass.region(), classType.pointer()),
                    classType
            ));
            throw new Log.KarinaException();
        }

        var newClassType = createSafeClassType(ctx, classType);

        var _ = findConstructors(expr.region(), ctx, classType.pointer(), expr.parameters());

        var call = new KExpr.Call(
                expr.region(),
                new KExpr.SpecialCall(expr.region(), new InvocationType.NewInit(newClassType)),
                List.of(),
                expr.parameters().stream().map(NamedExpression::expr).toList(),
                null
        );
        var callAttrib = attribExpr(hint, ctx, call).expr();

        return of(ctx, callAttrib);

    }

    private static KType.ClassType createSafeClassType(AttributionContext ctx, KType.ClassType classType) {
                var classModel = ctx.model().getClass(classType.pointer());

        var annotatedGenerics = !classType.generics().isEmpty();

        List<KType> newGenerics;
        if (annotatedGenerics) {
            newGenerics = classType.generics();
        } else {
            newGenerics = new ArrayList<>();
            for (var generic : classModel.generics()) {
                newGenerics.add(KType.Resolvable.newInstanceFromGeneric(generic));
            }
        }

        return classType.pointer().implement(newGenerics);

    }

    private static MethodCollection findConstructors(
            Region region,
            AttributionContext ctx,
            ClassPointer classPointer,
            List<NamedExpression> params
    ) {

        var classToInit = ctx.model().getClass(classPointer);
        var collection = classToInit.getMethodCollectionShallow("<init>");
        collection = collection.filter(ref -> {
            var methodModel = ctx.model().getMethod(ref);
            var modifiers = methodModel.modifiers();
            return !Modifier.isStatic(modifiers)
                    && ctx.protection().isMethodAccessible(ctx.model().getClass(ctx.owningClass()), ref);
        });

        var names = params.stream().map(ref -> ref.name().value()).toList();

        var callableWithName = collection.filter(ref -> {
            var methodModel = ctx.model().getMethod(ref);
            return filterConstructors(methodModel, names);
        });

        if (callableWithName.isEmpty()) {
            var available = new ArrayList<RegionOf<List<String>>>();
            for (var methodPointer : collection) {
                var methodModel = ctx.model().getMethod(methodPointer);
                available.add(RegionOf.region(methodModel.region(), methodModel.parameters()));
            }

            Log.error(ctx, new AttribError.MissingConstructor(
                    region,
                    classToInit.name(),
                    names,
                    available
            ));
            throw new Log.KarinaException();
        }

        return collection;
    }

    private static boolean filterConstructors(MethodModel methodModel, List<String> args) {

        var methodParams = methodModel.parameters();
        if (args.size() != methodParams.size()) {
            return false;
        }

        for (var i = 0; i < methodParams.size(); i++) {
            var parameter = methodParams.get(i);
            var param = args.get(i);
            if (!parameter.equals(param) && !param.equals("_")) {
                return false;
            }
        }
        return true;
    }


}
