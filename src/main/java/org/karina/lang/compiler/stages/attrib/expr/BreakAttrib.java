package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class BreakAttrib extends AttribExpr {

    public static AttribExpr attribBreak(@Nullable KType hint, AttributionContext ctx, KExpr.Break expr) {
        if (!ctx.isLoop()) {
            Log.attribError(new AttribError.ControlFlow(expr.region(), "break statement outside of loop"));
            throw new Log.KarinaException();
        }
        return of(ctx, expr);
    }


}
