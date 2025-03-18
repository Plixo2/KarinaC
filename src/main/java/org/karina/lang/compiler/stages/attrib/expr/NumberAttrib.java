package org.karina.lang.compiler.stages.attrib.expr;

import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.NumberSymbol;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.Map;
import java.util.Set;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class NumberAttrib  {
    public static AttributionExpr attribNumber(
            @Nullable KType hint, AttributionContext ctx, KExpr.Number expr) {

        var number = expr.number();
        var hasFraction = number.stripTrailingZeros().scale() > 0 || expr.decimalAnnotated();
        NumberSymbol symbol;
        if (hasFraction) {
            if (isPrimitive(hint, KType.KPrimitive.FLOAT)) {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            } else {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.doubleValue());
            }
        } else {
            if (isPrimitive(hint, KType.KPrimitive.DOUBLE)) {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.longValue());
            } else if (isPrimitive(hint, KType.KPrimitive.FLOAT)) {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            } else if (isPrimitive(hint, KType.KPrimitive.LONG)) {
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

    private static boolean isPrimitive(@Nullable KType hint, KType.KPrimitive primitive) {
        if (hint == null) {
            return false;
        }
        if (hint instanceof KType.PrimitiveType(var primitiveHint) && primitiveHint == primitive) {
            return true;
        }
        if (hint instanceof KType.ClassType classType) {
            var path = PRIMITIVE_CONVERSIONS.get(primitive);
            if (path == null) {
                return false;
            }
            return classType.pointer().equals(path);
        }

        return false;
    }


    private static final Map<KType.KPrimitive, ClassPointer> PRIMITIVE_CONVERSIONS = Map.of(
            KType.KPrimitive.INT, KType.INTEGER.pointer(),
            KType.KPrimitive.LONG, KType.LONG.pointer(),
            KType.KPrimitive.FLOAT, KType.FLOAT.pointer(),
            KType.KPrimitive.DOUBLE, KType.DOUBLE.pointer()
    );
}
