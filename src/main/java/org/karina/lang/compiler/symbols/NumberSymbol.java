package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KType;

public sealed interface NumberSymbol {
    KType type();

    record IntegerValue(Span region, int value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.INT);
        }
    }

    record LongValue(Span region, long value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.LONG);
        }
    }

    record FloatValue(Span region, float value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.FLOAT);
        }
    }

    record DoubleValue(Span region, double value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
        }
    }
}
