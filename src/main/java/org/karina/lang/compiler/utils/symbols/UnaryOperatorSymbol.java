package org.karina.lang.compiler.utils.symbols;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.UnaryOperator;

public sealed interface UnaryOperatorSymbol {
    Region region();
    KType type();

    sealed interface NegateOP extends UnaryOperatorSymbol {
        record FloatOP(Region region) implements NegateOP {
            @Override
            public KType type() {
                return KType.FLOAT;
            }
        }

        record IntOP(Region region) implements NegateOP {
            @Override
            public KType type() {
                return KType.INT;
            }
        }

        record LongOP(Region region) implements NegateOP {
            @Override
            public KType type() {
                return KType.LONG;
            }
        }

        record DoubleOP(Region region) implements NegateOP {
            @Override
            public KType type() {
                return KType.DOUBLE;
            }
        }
    }

    sealed interface NotOP extends UnaryOperatorSymbol {
        record BoolOP(Region region) implements NotOP {
            @Override
            public KType type() {
                return KType.BOOL;
            }
        }
    }

    static @Nullable UnaryOperatorSymbol fromOperator(KType.KPrimitive primitive, RegionOf<UnaryOperator> operator) {
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
