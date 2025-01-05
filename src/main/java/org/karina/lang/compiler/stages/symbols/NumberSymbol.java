package org.karina.lang.compiler.stages.symbols;

import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KType;

public sealed interface NumberSymbol {
    KType type();

    record IntegerValue(Span region, int value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType.IntType(this.region);
        }
    }

    record LongValue(Span region, long value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType.LongType(this.region);
        }
    }

    record FloatValue(Span region, float value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType.FloatType(this.region);
        }
    }

    record DoubleValue(Span region, double value) implements NumberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType.DoubleType(this.region);
        }
    }
}
