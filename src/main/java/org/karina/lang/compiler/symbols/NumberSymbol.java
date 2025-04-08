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
            case DoubleValue _ -> KType.DOUBLE;
            case FloatValue _ -> KType.FLOAT;
            case IntegerValue _ -> KType.INT;
            case LongValue _ -> KType.LONG;
        };
    }
}
