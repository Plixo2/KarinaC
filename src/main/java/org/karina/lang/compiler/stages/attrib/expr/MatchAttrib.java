package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class MatchAttrib extends AttribExpr {
    public static AttribExpr attribMatch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Match expr) {

        Log.temp(expr.region(), "Destruct not implemented");
        throw new Log.KarinaException();
    }

}
