package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class BreakAttrib  {

    public static AttributionExpr attribBreak(@Nullable KType hint, AttributionContext ctx, KExpr.Break expr) {
        if (!ctx.isLoop()) {
            Log.error(ctx, new AttribError.ControlFlow(expr.region(), "break statement outside of loop"));
            throw new Log.KarinaException();
        }
        return of(ctx, expr);
    }


}
