package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class SelfAttrib  {
    public static AttributionExpr attribSelf(
            @Nullable KType hint, AttributionContext ctx, KExpr.Self expr) {

        if (ctx.selfVariable() == null) {
            Log.error(ctx, new AttribError.UnqualifiedSelf(
                    expr.region(), expr.region()
            ));
            throw new Log.KarinaException();
        }
        ctx.selfVariable().incrementUsageCount();

        return of(ctx, new KExpr.Self(
                expr.region(),
                ctx.selfVariable()
        ));

    }
}
