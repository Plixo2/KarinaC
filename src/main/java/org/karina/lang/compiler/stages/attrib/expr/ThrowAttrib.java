package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ThrowAttrib  {
    public static AttributionExpr attribThrow(@Nullable KType hint, AttributionContext ctx, KExpr.Throw expr) {

        var value = attribExpr(null, ctx, expr.value()).expr();

        if (!(value.type() instanceof KType.ClassType classType)) {
            Log.temp(value.region(), "Throw expression must be of struct");
            throw new Log.KarinaException();
        }
        //TODO use values to check if the class is throwable

        return of(ctx, new KExpr.Throw(
                expr.region(),
                value,
                classType
        ));
    }
}
