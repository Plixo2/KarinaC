package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class StringAttrib extends AttributionExpr {

    public static AttributionExpr attribStringExpr(
            @Nullable KType hint, AttributionContext ctx, KExpr.StringExpr expr) { return of(ctx, expr);}


}
