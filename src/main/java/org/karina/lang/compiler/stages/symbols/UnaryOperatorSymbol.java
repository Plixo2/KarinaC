package org.karina.lang.compiler.stages.symbols;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.UnaryOperator;

public sealed interface UnaryOperatorSymbol {
    Span region();
    KType type();

    sealed interface NegateOP extends UnaryOperatorSymbol {
        record FloatOP(Span region) implements NegateOP {
            @Override
            public KType type() {
                return new KType.PrimitiveType.FloatType(this.region);
            }
        }

        record IntOP(Span region) implements NegateOP {
            @Override
            public KType type() {
                return new KType.PrimitiveType.IntType(this.region);
            }
        }

        record LongOP(Span region) implements NegateOP {
            @Override
            public KType type() {
                return new KType.PrimitiveType.LongType(this.region);
            }
        }

        record DoubleOP(Span region) implements NegateOP {
            @Override
            public KType type() {
                return new KType.PrimitiveType.DoubleType(this.region);
            }
        }
    }

    sealed interface NotOP extends UnaryOperatorSymbol {
        record BoolOP(Span region) implements NotOP {
            @Override
            public KType type() {
                return new KType.PrimitiveType.BoolType(this.region);
            }
        }
    }

    static @Nullable UnaryOperatorSymbol fromOperator(KType.KPrimitive primitive, SpanOf<UnaryOperator> operator) {
        return switch (operator.value()) {
            case NOT -> {
                if (primitive == KType.KPrimitive.BOOL) {
                    yield new NotOP.BoolOP(operator.region());
                } else {
                    yield null;
                }
            }
            case NEGATE -> switch (primitive) {
                case INT -> new NegateOP.IntOP(operator.region());
                case FLOAT -> new NegateOP.FloatOP(operator.region());
                case DOUBLE -> new NegateOP.DoubleOP(operator.region());
                case LONG -> new NegateOP.LongOP(operator.region());
                default -> null;
            };
        };
    }


}
