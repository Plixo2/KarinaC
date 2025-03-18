package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;
public class BooleanAttrib {

    public static AttributionExpr attribBoolean(@Nullable KType hint, AttributionContext ctx, KExpr.Boolean expr) {
        return of(ctx, expr);
    }

}
