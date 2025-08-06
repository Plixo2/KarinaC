package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ContinueAttrib  {

    public static AttributionExpr attribContinue(@Nullable KType hint, AttributionContext ctx, KExpr.Continue expr) {
        if (!ctx.isLoop()) {
            Log.error(ctx, new AttribError.ControlFlow(expr.region(), "continue statement outside of loop"));
            throw new Log.KarinaException();
        }
        return of(ctx, expr);
    }

}
