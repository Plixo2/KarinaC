package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

public sealed interface NumberSymbol {
    KType type();

    record IntegerValue(Region region, int value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.INT);
        }
    }

    record LongValue(Region region, long value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.LONG);
        }
    }

    record FloatValue(Region region, float value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.FLOAT);
        }
    }

    record DoubleValue(Region region, double value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
        }
    }
}
