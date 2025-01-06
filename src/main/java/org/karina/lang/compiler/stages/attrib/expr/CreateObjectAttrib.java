package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.NamedExpression;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateObjectAttrib extends AttribExpr {
    public static AttribExpr attribCreateObject(
            @Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) {

        var struct = ctx.getStruct(expr.createType().region(), expr.createType());
        //casting is ok, ctx.getStruct already checks for this
        var classType = (KType.ClassType) expr.createType();
        var annotatedGenerics = !classType.generics().isEmpty();

        //generate the new generics for the implementation
        List<KType> newGenerics;
        if (annotatedGenerics) {
            //We don't have to test for the length of the generics,
            // this should already be checked in the import stage
            newGenerics = classType.generics();
        } else {
            var genericCount = struct.generics().size();
            newGenerics = new ArrayList<>(genericCount);
            for (var ignored = 0; ignored < genericCount; ignored++) {
                newGenerics.add(new KType.PrimitiveType.Resolvable(expr.region()));
            }
        }

        if (newGenerics.size() != struct.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }
        //We map all generics here to the new implementation to replace fields in the struct.
        //The previous step should have already ensured that the size of generics is the same.
        Map<Generic, KType> mapped = new HashMap<>();
        for (var i = 0; i < newGenerics.size(); i++) {
            var generic = struct.generics().get(i);
            var type = newGenerics.get(i);
            mapped.put(generic, type);
        }

        //The new type with all the generics replaced
        var newType = new KType.ClassType(
                expr.createType().region(),
                classType.path(),
                newGenerics
        );

        //check all Parameters,
        //check if all names are correct,
        //and also check the type with the replaced Type (implemented Generics)
        var newParameters = new ArrayList<NamedExpression>();

        var openParameters = new ArrayList<>(expr.parameters());
        for (var field : struct.fields()) {
            var fieldType = replaceType(field.type(), mapped);
            var foundParameter = openParameters
                    .stream().filter(ref -> ref.name().equals(field.name())).findFirst();
            if (foundParameter.isEmpty()) {
                Log.attribError(new AttribError.MissingField(
                        expr.region(),
                        field.name().value()
                ));
                throw new Log.KarinaException();
            } else {
                var parameter = foundParameter.get();
                openParameters.remove(parameter);
                var attribField = attribExpr(fieldType, ctx, parameter.expr()).expr();

                ctx.assign(parameter.name().region(), fieldType, attribField.type());

                newParameters.add(new NamedExpression(
                        parameter.region(),
                        parameter.name(),
                        attribField
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
        ));

    }
}
