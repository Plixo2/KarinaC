package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class StringAttrib  {

    public static AttributionExpr attribStringExpr(@Nullable KType hint, AttributionContext ctx, KExpr.StringExpr expr) {
        return of(ctx, expr);
    }


}
