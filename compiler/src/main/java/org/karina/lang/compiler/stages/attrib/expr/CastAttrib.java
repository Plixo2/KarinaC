package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.CastSymbol;
import org.karina.lang.compiler.utils.CastTo;
import org.karina.lang.compiler.utils.Region;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CastAttrib  {

    public static AttributionExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) {
        //TODO add ability for boxing

        KExpr left;
        KType toType;
        var castTo = expr.cast();
        CastSymbol symbol;
        switch (castTo) {
            case CastTo.AutoCast _ -> {
                if (hint == null) {
                    Log.error(ctx, new AttribError.UnknownCast(expr.region()));
                    throw new Log.KarinaException();
                }
                toType = hint;
                //give hint, we want to convert to the type anyway
                left = attribExpr(hint, ctx, expr.expression()).expr();
                var leftType = left.type();
                symbol = numericCast(ctx, expr.region(), leftType, toType, castTo);
            }
            case CastTo.CastToType(var to) -> {
                KType hintType = to;
                if (to.isPrimitive()) {
                    hintType = null;
                }

                //dont give hint, it should be casted to the type, no conversion
                left = attribExpr(hintType, ctx, expr.expression()).expr();
                toType = to;
                var leftType = left.type();

                if (to instanceof KType.ClassType classType && leftType instanceof KType.ClassType leftClassType) {
                    if (!ctx.checking().canAssign(ctx, expr.region(), classType, leftType, true)) {
                        Log.error(ctx, new AttribError.InvalidNarrowingCast(expr.region()));
                        throw new Log.KarinaException();
                    }
                    symbol = new CastSymbol.UpCast(leftClassType, classType);
                } else {
                    symbol = numericCast(ctx, expr.region(), leftType, toType, castTo);
                }
            }
        }



        return of(ctx, new KExpr.Cast(
                expr.region(),
                left, castTo,
                symbol
        ));
    }

    private static CastSymbol numericCast(AttributionContext ctx, Region region, KType left, KType right, CastTo castTo) {
        if (!(left instanceof KType.PrimitiveType(KType.KPrimitive fromPrimitive))) {
            if (castTo instanceof CastTo.AutoCast) {
                Log.error(ctx, new AttribError.UnknownCast(region));
            } else {
                Log.temp(ctx, region, "Non Numeric Cast");
            }
            throw new Log.KarinaException();
        }
        if (!(right instanceof KType.PrimitiveType(KType.KPrimitive toPrimitive))) {
            if (castTo instanceof CastTo.AutoCast) {
                Log.error(ctx, new AttribError.UnknownCast(region));
            } else {
                Log.temp(ctx, region, "Non Numeric Cast");
            }
            throw new Log.KarinaException();
        }
        if (!fromPrimitive.isNumeric() || !toPrimitive.isNumeric()) {
            if (castTo instanceof CastTo.AutoCast) {
                Log.error(ctx, new AttribError.UnknownCast(region));
            } else {
                Log.temp(ctx, region, "Non Numeric Cast");
            }
            throw new Log.KarinaException();
        }
        if (fromPrimitive == toPrimitive) {
            Log.warn(ctx, region, "Unnecessary Cast");
        }

        return new CastSymbol.PrimitiveCast(fromPrimitive, toPrimitive);
    }
}
