package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class ContinueAttrib extends AttributionExpr {

    public static AttributionExpr attribContinue(@Nullable KType hint, AttributionContext ctx, KExpr.Continue expr) {
        if (!ctx.isLoop()) {
            Log.attribError(new AttribError.ControlFlow(expr.region(), "continue statement outside of loop"));
            throw new Log.KarinaException();
        }
        return of(ctx, expr);
    }

}
