package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class SelfAttrib extends AttribExpr {
    public static AttribExpr attribSelf(
            @Nullable KType hint, AttributionContext ctx, KExpr.Self expr) {

        if (ctx.selfType() == null) {
            Log.attribError(new AttribError.UnqualifiedSelf(
                    expr.region(), ctx.methodRegion()
            ));
            throw new Log.KarinaException();
        }
        ctx.selfType().incrementUsageCount();

        return of(ctx, new KExpr.Self(
                expr.region(),
                ctx.selfType()
        ));

    }
}
