package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

public sealed interface NumberSymbol {

    record IntegerValue(Region region, int value) implements NumberSymbol { }

    record LongValue(Region region, long value) implements NumberSymbol { }

    record FloatValue(Region region, float value) implements NumberSymbol { }

    record DoubleValue(Region region, double value) implements NumberSymbol { }

    default KType type() {
        return switch (this) {
            case DoubleValue _ -> new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
            case FloatValue _ -> new KType.PrimitiveType(KType.KPrimitive.FLOAT);
            case IntegerValue _ -> new KType.PrimitiveType(KType.KPrimitive.INT);
            case LongValue _ -> new KType.PrimitiveType(KType.KPrimitive.LONG);
        };
    }
}
