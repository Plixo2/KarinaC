package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class ThrowAttrib extends AttributionExpr {
    public static AttributionExpr attribThrow(@Nullable KType hint,AttributionContext ctx, KExpr.Throw expr) {

        var value = attribExpr(null, ctx, expr.value()).expr();

        if (!(value.type() instanceof KType.ClassType classType)) {
            Log.temp(value.region(), "Throw expression must be of struct");
            throw new Log.KarinaException();
        }
        //TODO use annotations to check if the class is throwable

        return of(ctx, new KExpr.Throw(
                expr.region(),
                value,
                classType
        ));
    }
}
