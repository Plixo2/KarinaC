package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class ReturnAttrib extends AttribExpr {

    public static AttribExpr attribReturn(
            @Nullable KType hint, AttributionContext ctx, KExpr.Return expr) {

        KExpr value;
        KType yieldType;
        if (expr.value() != null) {
            value = AttribExpr.attribExpr(ctx.returnType(), ctx, expr.value()).expr();
            yieldType = value.type();
        } else {
            value = null;
            yieldType = new KType.PrimitiveType.VoidType(expr.region());
        }

        ctx.assign(expr.region(), ctx.returnType(), yieldType);

        return of(ctx, new KExpr.Return(
                expr.region(),
                value,
                yieldType
        ));
    }




}
