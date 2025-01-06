package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class InstanceOfAttrib extends AttribExpr {
    public static AttribExpr attribInstanceOf(
            @Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();


        return of(ctx, new KExpr.IsInstanceOf(
                expr.region(),
                left,
                expr.isType()
        ));
    }

}
