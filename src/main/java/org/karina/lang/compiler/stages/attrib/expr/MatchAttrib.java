package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class MatchAttrib  {
    public static AttributionExpr attribMatch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Match expr) {

        Log.temp(expr.region(), "Match not implemented");
        throw new Log.KarinaException();
    }

}
