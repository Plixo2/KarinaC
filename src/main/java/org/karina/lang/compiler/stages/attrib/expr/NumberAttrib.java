package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.NumberSymbol;

public class NumberAttrib extends AttribExpr {
    public static AttribExpr attribNumber(
            @Nullable KType hint, AttributionContext ctx, KExpr.Number expr) {

        var number = expr.number();
        var hasFraction = number.stripTrailingZeros().scale() > 0 || expr.decimalAnnotated();
        NumberSymbol symbol;
        if (hasFraction) {
            if (ctx.isPrimitive(hint, KType.KPrimitive.DOUBLE)) {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.doubleValue());
            } else {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            }
        } else {
            if (ctx.isPrimitive(hint, KType.KPrimitive.DOUBLE)) {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.longValue());
            } else if (ctx.isPrimitive(hint, KType.KPrimitive.FLOAT)) {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            } else if (ctx.isPrimitive(hint, KType.KPrimitive.LONG)) {
                try {
                    symbol = new NumberSymbol.LongValue(expr.region(), number.longValueExact());
                } catch(ArithmeticException e1) {
                    Log.syntaxError(expr.region(), "Number too large for long");
                    throw new Log.KarinaException();
                }
            } else {
                try {
                    symbol = new NumberSymbol.IntegerValue(expr.region(), number.intValueExact());
                } catch(ArithmeticException e1) {
                    try {
                        symbol = new NumberSymbol.LongValue(expr.region(), number.longValueExact());
                    } catch(ArithmeticException e2) {
                        Log.syntaxError(expr.region(), "Number too large for long");
                        throw new Log.KarinaException();
                    }
                }
            }
        }
        return of(ctx, new KExpr.Number(
                expr.region(),
                number,
                expr.decimalAnnotated(),
                symbol
        ));

    }
}
