package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.util.ArrayList;

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
                elementType = new KType.PrimitiveType.Resolvable(true);
            }
        }

        if (elementType.isVoid()) {
            Log.attribError(new AttribError.NotSupportedType(expr.region(), elementType));
            throw new Log.KarinaException();
        }

        //TODO first get super type of all elements, or only when to type available?
        // then it fails, try to use makeAssignment

        var newElements = new ArrayList<KExpr>();
        for (var element : expr.elements()) {
            var newElement = attribExpr(elementType, ctx, element).expr();
            newElement = ctx.makeAssignment(newElement.region(), elementType, newElement);
            newElements.add(newElement);
        }

        return of(ctx, new KExpr.CreateArray(
                expr.region(),
                elementType,
                newElements,
                new KType.ArrayType(
                        elementType
                )
        ));

    }
}
