package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class ReturnAttrib extends AttributionExpr {

    public static AttributionExpr attribReturn(
            @Nullable KType hint, AttributionContext ctx, KExpr.Return expr) {

        KExpr value;
        KType yieldType;
        if (expr.value() != null) {
            value = AttributionExpr.attribExpr(ctx.returnType(), ctx, expr.value()).expr();
            yieldType = value.type();
        } else {
            value = null;
            yieldType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        }

        ctx.assign(expr.region(), ctx.returnType(), yieldType);

        return of(ctx, new KExpr.Return(
                expr.region(),
                value,
                yieldType
        ));
    }




}
