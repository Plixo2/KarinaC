package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.CastSymbol;
import org.karina.lang.compiler.utils.CastTo;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CastAttrib  {

    public static AttributionExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) {

        KExpr left;
        KType toType;
        switch (expr.cast()) {
            case CastTo.AutoCast _ -> {
                if (hint == null) {
                    Log.attribError(new AttribError.UnknownCast(expr.region()));
                    throw new Log.KarinaException();
                }
                toType = hint;
                //give hint, we want to convert to the type anyway
                left = attribExpr(hint, ctx, expr.expression()).expr();
            }
            case CastTo.CastToType(var to) -> {
                //dont give hint, it should be casted to the type, no conversion
                left = attribExpr(null, ctx, expr.expression()).expr();
                toType = to;
            }
        }

        var type = left.type();

        if (!(type instanceof KType.PrimitiveType(KType.KPrimitive fromPrimitive))) {
            Log.temp(left.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (!(toType instanceof KType.PrimitiveType(KType.KPrimitive toPrimitive))) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (type.isVoid()) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), type));
            throw new Log.KarinaException();
        }
        if (!fromPrimitive.isNumeric() || !toPrimitive.isNumeric()) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Cast(
                expr.region(),
                left,
                expr.cast(),
                new CastSymbol(fromPrimitive, toPrimitive)
        ));
    }
}
