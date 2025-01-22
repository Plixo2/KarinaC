package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

public class SuperAttrib extends AttributionExpr {

    public static AttributionExpr attribSuper(@Nullable KType hint, AttributionContext ctx, KExpr.Super expr) {
        return of(ctx, expr);
    }

}