package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.CastSymbol;

public class CastAttrib extends AttributionExpr {

    public static AttributionExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) {
        var left = attribExpr(null, ctx, expr.expression()).expr();
        var type = left.type();
        var toType = expr.asType();

        if (!(type instanceof KType.PrimitiveType fromPrimitive)) {
            Log.temp(left.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (!(toType instanceof KType.PrimitiveType toPrimitive)) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (fromPrimitive.primitive() == KType.KPrimitive.VOID) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), type));
            throw new Log.KarinaException();
        }
        if (toPrimitive.primitive() == KType.KPrimitive.VOID) {
            Log.attribError(new AttribError.NotSupportedType(expr.region(), toType));
            throw new Log.KarinaException();
        }
        if (!fromPrimitive.isNumeric() || !toPrimitive.isNumeric()) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Cast(
                expr.region(),
                left,
                toType,
                new CastSymbol(fromPrimitive, toPrimitive)
        ));
    }
}
