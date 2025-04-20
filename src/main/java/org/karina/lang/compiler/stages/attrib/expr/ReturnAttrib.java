package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ReturnAttrib  {

    public static AttributionExpr attribReturn(
            @Nullable KType hint, AttributionContext ctx, KExpr.Return expr) {

        KExpr value;
        KType returnType;
        if (expr.value() != null) {
            value = AttributionExpr.attribExpr(ctx.returnType(), ctx, expr.value()).expr();
            returnType = value.type();

            value = ctx.makeAssignment(expr.region(), ctx.returnType(), value);
        } else {
            value = null;
            returnType = KType.NONE;

            if (!ctx.returnType().isVoid()) {
                Log.attribError(new AttribError.ControlFlow(expr.region(), "Invalid return statement"));
                throw new Log.KarinaException();
            }
        }

        return of(ctx, new KExpr.Return(
                expr.region(),
                value,
                returnType
        ));
    }




}
