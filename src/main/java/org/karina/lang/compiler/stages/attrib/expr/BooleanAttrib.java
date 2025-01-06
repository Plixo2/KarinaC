package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class BooleanAttrib extends AttribExpr {

    public static AttribExpr attribBoolean(@Nullable KType hint, AttributionContext ctx, KExpr.Boolean expr) {
        return of(ctx, expr);
    }

}
