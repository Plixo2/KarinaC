package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ThrowAttrib  {
    public static AttributionExpr attribThrow(@Nullable KType hint, AttributionContext ctx, KExpr.Throw expr) {

        var value = attribExpr(KType.THROWABLE, ctx, expr.value()).expr();

        value = ctx.makeAssignment(expr.region(), KType.THROWABLE, value);

        return of(ctx, new KExpr.Throw(
                expr.region(),
                value
        ));
    }
}
