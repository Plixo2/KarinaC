package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CreateObjectAttrib  {
    public static AttributionExpr attribCreateObject(@Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) {


        if (!(expr.createType() instanceof KType.ClassType classType)) {
            Log.attribError(new AttribError.NotAClass(expr.region(), expr.createType()));
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


/*        var classModel = ctx.model().getClass(pointer);

        var annotatedGenerics = !generics.isEmpty();

        //generate the new generics for the implementation
        List<KType> newGenerics;
        if (annotatedGenerics) {
            //We don't have to test for the length of the generics,
            // this should already be checked in the import stage
            newGenerics = generics;
        } else {
            var genericCount = classModel.generics().size();
            newGenerics = new ArrayList<>(genericCount);
            for (var ignored = 0; ignored < genericCount; ignored++) {
                newGenerics.add(new KType.PrimitiveType.Resolvable());
            }
        }

        if (newGenerics.size() != classModel.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }
        //We map all generics here to the new implementation to replace fields in the struct.
        //The previous step should have already ensured that the size of generics is the same.
        Map<Generic, KType> mapped = new HashMap<>();
        for (var i = 0; i < newGenerics.size(); i++) {
            var generic = classModel.generics().get(i);
            var type = newGenerics.get(i);
            mapped.put(generic, type);
        }

        //The new fieldType with all the generics replaced
        var newType = new KType.ClassType(
                pointer,
                newGenerics
        );

        //check all Parameters,
        //check if all names are correct,
        //and also check the fieldType with the replaced Type (implemented Generics)
        var newParameters = new ArrayList<NamedExpression>();

        var openParameters = new ArrayList<>(expr.parameters());
        for (var field : classModel.fields()) {
            var fieldType = Types.projectGenerics(field.type(), mapped);
            var foundParameter = openParameters
                    .stream().filter(ref -> ref.name().value().equals(field.name())).findFirst();
            if (foundParameter.isEmpty()) {
                Log.attribError(new AttribError.MissingField(
                        expr.region(),
                        field.name()
                ));
                throw new Log.KarinaException();
            } else {
                var parameter = foundParameter.get();
                openParameters.remove(parameter);
                var attribField = attribExpr(fieldType, ctx, parameter.expr()).expr();
                attribField = ctx.makeAssignment(parameter.name().region(), fieldType, attribField);

                newParameters.add(new NamedExpression(
                        parameter.region(),
                        parameter.name(),
                        attribField,
                        field.type()
                ));
            }
        }
        if (!openParameters.isEmpty()) {
            var toMany = openParameters.getFirst();
            Log.attribError(new AttribError.UnknownField(toMany.name().region(), toMany.name().value()));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.CreateObject(
                expr.region(),
                newType,
                newParameters,
                newType
        ));*/

    }

    private static KType.ClassType createSafeClassType(AttributionContext ctx, KType.ClassType classType) {
                var classModel = ctx.model().getClass(classType.pointer());

        var annotatedGenerics = !classType.generics().isEmpty();

        List<KType> newGenerics;
        if (annotatedGenerics) {
            newGenerics = classType.generics();
        } else {
            var genericCount = classModel.generics().size();
            newGenerics = new ArrayList<>(genericCount);
            for (var ignored = 0; ignored < genericCount; ignored++) {
                newGenerics.add(new KType.PrimitiveType.Resolvable());
            }
        }

        return new KType.ClassType(
                classType.pointer(),
                newGenerics
        );

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
                    && ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
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

            Log.attribError(new AttribError.MissingConstructor(
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
