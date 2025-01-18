package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;

public class CreateArrayAttrib extends AttributionExpr {
    public static AttributionExpr attribCreateArray(
            @Nullable KType hint, AttributionContext ctx, KExpr.CreateArray expr) {

        KType elementType;
        if (expr.hint() != null) {
            elementType = expr.hint();
        } else {
            if (hint instanceof KType.ArrayType arrayType) {
                elementType = arrayType.elementType();
            } else {
                elementType = new KType.PrimitiveType.Resolvable(expr.region());
            }
        }
        var newElements = new ArrayList<KExpr>();
        for (var element : expr.elements()) {
            var newElement = attribExpr(elementType, ctx, element).expr();
            ctx.assign(newElement.region(), elementType, newElement.type());
            newElements.add(newElement);
        }

        return of(ctx, new KExpr.CreateArray(
                expr.region(),
                elementType,
                newElements,
                new KType.ArrayType(
                        expr.region(),
                        elementType
                )
        ));

    }
}
