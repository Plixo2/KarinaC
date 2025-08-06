package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

public class MatchAttrib  {
    public static AttributionExpr attribMatch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Match expr) {

        Log.temp(ctx, expr.region(), "Match not implemented");
        throw new Log.KarinaException();
    }

}
