package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.util.ArrayList;
import java.util.List;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CreateArrayAttrib  {
    public static AttributionExpr attribCreateArray(
            @Nullable KType hint, AttributionContext ctx, KExpr.CreateArray expr) {

        KType elementType;
        if (expr.hint() != null) {
            elementType = expr.hint();
        } else {
            if (hint instanceof KType.ArrayType(KType type)) {
                elementType = type;
            } else {
                elementType = new KType.PrimitiveType.Resolvable(true, false);
            }
        }

        if (elementType.isVoid()) {
            Log.error(ctx, new AttribError.NotSupportedType(expr.region(), elementType));
            throw new Log.KarinaException();
        }


        List<KExpr> unmappedElements;


        try (var fork = ctx.intoContext().<KExpr>fork()){
            for (var element : expr.elements()) {
                fork.collect(subC -> attribExpr(elementType, ctx.withNewContext(subC), element).expr());
            }
            unmappedElements = fork.dispatch();
        }

        // check if we can convert every element to a common type
        var mappedElements = new ArrayList<KExpr>();
        for (var unmappedElement : unmappedElements) {
            var conversion = ctx.getConversion(unmappedElement.region(), elementType, unmappedElement, true, false);
            if (conversion != null) {
                mappedElements.add(conversion);
            } else {
                break;
            }
        }

        KType currentType = elementType;

        // if we can't convert every element to a common type, we need to find a common super type
        if (mappedElements.size() != unmappedElements.size()) {
            mappedElements.clear();

            for (var unMappedElement : unmappedElements) {
                var superType = ctx.checking().superType(ctx, unMappedElement.region(), currentType, unMappedElement.type());
                if (superType == null) {
                    //dont report error right away, we still can use root for primitive conversion
                    currentType = KType.ROOT;
                    break;
                } else {
                    currentType = superType;
                }
            }

            // ... and then convert every element to that type, otherwise fail
            for (var unMappedElement : unmappedElements) {
                var conversion = ctx.makeAssignment(unMappedElement.region(), currentType, unMappedElement);
                mappedElements.add(conversion);
            }

        }

        //should not happen, but just in case check again for void type
        if (elementType.isVoid()) {
            Log.error(ctx, new AttribError.NotSupportedType(expr.region(), elementType));
            throw new Log.KarinaException();
        }


        return of(ctx, new KExpr.CreateArray(
                expr.region(),
                elementType,
                mappedElements,
                new KType.ArrayType(
                        currentType
                )
        ));

    }
}
