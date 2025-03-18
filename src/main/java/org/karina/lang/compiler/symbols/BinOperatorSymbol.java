package org.karina.lang.compiler.symbols;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.objects.BinaryOperator;
import org.karina.lang.compiler.objects.KType;

/**
 * Messy, but useful for bytecode generation
 */
public sealed interface BinOperatorSymbol {
    Region region();
    KType type();
//    BinaryOperator operator();
    sealed interface ObjectEquals extends BinOperatorSymbol {
        record Equal(Region region) implements ObjectEquals {}
        record NotEqual(Region region) implements ObjectEquals {}
        record StrictEqual(Region region) implements ObjectEquals {}
        record StrictNotEqual(Region region) implements ObjectEquals {}

        default KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.BOOL);
        }

        static @Nullable ObjectEquals fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case EQUAL -> new Equal(operator.region());
                case STRICT_EQUAL -> new StrictEqual(operator.region());
                case NOT_EQUAL -> new NotEqual(operator.region());
                case STRICT_NOT_EQUAL -> new StrictNotEqual(operator.region());
                case ADD, OR, AND, SUBTRACT, MULTIPLY, DIVIDE, MODULUS, CONCAT,
                     LESS_THAN_OR_EQUAL, LESS_THAN, GREATER_THAN, GREATER_THAN_OR_EQUAL -> null;
            };
        }
//
//        default BinaryOperator operator() {
//            return switch (this) {
//                case Equal _ -> BinaryOperator.EQUAL;
//                case NotEqual _ -> BinaryOperator.NOT_EQUAL;
//                case StrictEqual _ -> BinaryOperator.STRICT_EQUAL;
//                case StrictNotEqual _ -> BinaryOperator.STRICT_NOT_EQUAL;
//            };
//        }
    }

    sealed interface FloatOP extends BinOperatorSymbol {
        record Add(Region region) implements FloatOP {}
        record Subtract(Region region) implements FloatOP {}
        record Multiply(Region region) implements FloatOP {}
        record Divide(Region region) implements FloatOP {}
        record Modulus(Region region) implements FloatOP {}
        record Equal(Region region) implements FloatOP {}
        record NotEqual(Region region) implements FloatOP {}
        record LessThan(Region region) implements FloatOP {}
        record LessThanOrEqual(Region region) implements FloatOP {}
        record GreaterThan(Region region) implements FloatOP {}
        record GreaterThanOrEqual(Region region) implements FloatOP {}

        @Override
        default KType type() {
            var returnBool = switch (this) {
                case Equal ignored -> true;
                case GreaterThan ignored -> true;
                case GreaterThanOrEqual ignored -> true;
                case LessThan ignored -> true;
                case LessThanOrEqual ignored -> true;
                case NotEqual ignored -> true;
                default -> false;
            };
            if (returnBool) {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            } else {
                return new KType.PrimitiveType(KType.KPrimitive.FLOAT);
            }
        }


        static @Nullable FloatOP fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case ADD -> new Add(operator.region());
                case SUBTRACT -> new Subtract(operator.region());
                case MULTIPLY -> new Multiply(operator.region());
                case DIVIDE -> new Divide(operator.region());
                case MODULUS -> new Modulus(operator.region());
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL ->  new NotEqual(operator.region());
                case LESS_THAN ->  new LessThan(operator.region());
                case LESS_THAN_OR_EQUAL -> new LessThanOrEqual(operator.region());
                case GREATER_THAN -> new GreaterThan(operator.region());
                case GREATER_THAN_OR_EQUAL -> new GreaterThanOrEqual(operator.region());
                case CONCAT, AND, OR, STRICT_EQUAL, STRICT_NOT_EQUAL -> null;
            };
        }

//        default BinaryOperator operator() {
//            return switch (this) {
//                case Add ignored -> BinaryOperator.ADD;
//                case Subtract ignored -> BinaryOperator.SUBTRACT;
//                case Multiply ignored -> BinaryOperator.MULTIPLY;
//                case Divide ignored -> BinaryOperator.DIVIDE;
//                case Modulus ignored -> BinaryOperator.MODULUS;
//                case Equal ignored -> BinaryOperator.EQUAL;
//                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
//                case LessThan ignored -> BinaryOperator.LESS_THAN;
//                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
//                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
//                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
//            };
//        }

    }

    sealed interface DoubleOP extends BinOperatorSymbol {
        record Add(Region region) implements DoubleOP {}
        record Subtract(Region region) implements DoubleOP {}
        record Multiply(Region region) implements DoubleOP {}
        record Divide(Region region) implements DoubleOP {}
        record Modulus(Region region) implements DoubleOP {}
        record Equal(Region region) implements DoubleOP {}
        record NotEqual(Region region) implements DoubleOP {}
        record LessThan(Region region) implements DoubleOP {}
        record LessThanOrEqual(Region region) implements DoubleOP {}
        record GreaterThan(Region region) implements DoubleOP {}
        record GreaterThanOrEqual(Region region) implements DoubleOP {}

        @Override
        default KType type() {
            var returnBool = switch (this) {
                case Equal ignored -> true;
                case GreaterThan ignored -> true;
                case GreaterThanOrEqual ignored -> true;
                case LessThan ignored -> true;
                case LessThanOrEqual ignored -> true;
                case NotEqual ignored -> true;
                default -> false;
            };
            if (returnBool) {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            } else {
                return new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
            }
        }

        static @Nullable BinOperatorSymbol.DoubleOP fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case ADD -> new Add(operator.region());
                case SUBTRACT -> new Subtract(operator.region());
                case MULTIPLY -> new Multiply(operator.region());
                case DIVIDE -> new Divide(operator.region());
                case MODULUS -> new Modulus(operator.region());
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL ->  new NotEqual(operator.region());
                case LESS_THAN ->  new LessThan(operator.region());
                case LESS_THAN_OR_EQUAL -> new LessThanOrEqual(operator.region());
                case GREATER_THAN -> new GreaterThan(operator.region());
                case GREATER_THAN_OR_EQUAL -> new GreaterThanOrEqual(operator.region());
                case STRICT_EQUAL, STRICT_NOT_EQUAL, CONCAT, AND, OR -> null;
            };
        }

//        default BinaryOperator operator() {
//            return switch (this) {
//                case Add ignored -> BinaryOperator.ADD;
//                case Subtract ignored -> BinaryOperator.SUBTRACT;
//                case Multiply ignored -> BinaryOperator.MULTIPLY;
//                case Divide ignored -> BinaryOperator.DIVIDE;
//                case Modulus ignored -> BinaryOperator.MODULUS;
//                case Equal ignored -> BinaryOperator.EQUAL;
//                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
//                case LessThan ignored -> BinaryOperator.LESS_THAN;
//                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
//                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
//                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
//            };
//        }

    }

    sealed interface LongOP extends BinOperatorSymbol {
        record Add(Region region) implements LongOP {}
        record Subtract(Region region) implements LongOP {}
        record Multiply(Region region) implements LongOP {}
        record Divide(Region region) implements LongOP {}
        record Modulus(Region region) implements LongOP {}
        record Equal(Region region) implements LongOP {}
        record NotEqual(Region region) implements LongOP {}
        record LessThan(Region region) implements LongOP {}
        record LessThanOrEqual(Region region) implements LongOP {}
        record GreaterThan(Region region) implements LongOP {}
        record GreaterThanOrEqual(Region region) implements LongOP {}

        @Override
        default KType type() {
            var returnBool = switch (this) {
                case Equal ignored -> true;
                case GreaterThan ignored -> true;
                case GreaterThanOrEqual ignored -> true;
                case LessThan ignored -> true;
                case LessThanOrEqual ignored -> true;
                case NotEqual ignored -> true;
                default -> false;
            };
            if (returnBool) {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            } else {
                return new KType.PrimitiveType(KType.KPrimitive.LONG);
            }
        }

        static @Nullable BinOperatorSymbol.LongOP fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case ADD -> new Add(operator.region());
                case SUBTRACT -> new Subtract(operator.region());
                case MULTIPLY -> new Multiply(operator.region());
                case DIVIDE -> new Divide(operator.region());
                case MODULUS -> new Modulus(operator.region());
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL ->  new NotEqual(operator.region());
                case LESS_THAN ->  new LessThan(operator.region());
                case LESS_THAN_OR_EQUAL -> new LessThanOrEqual(operator.region());
                case GREATER_THAN -> new GreaterThan(operator.region());
                case GREATER_THAN_OR_EQUAL -> new GreaterThanOrEqual(operator.region());
                case STRICT_EQUAL, STRICT_NOT_EQUAL, CONCAT, AND, OR -> null;
            };
        }

//        default BinaryOperator operator() {
//            return switch (this) {
//                case Add ignored -> BinaryOperator.ADD;
//                case Subtract ignored -> BinaryOperator.SUBTRACT;
//                case Multiply ignored -> BinaryOperator.MULTIPLY;
//                case Divide ignored -> BinaryOperator.DIVIDE;
//                case Modulus ignored -> BinaryOperator.MODULUS;
//                case Equal ignored -> BinaryOperator.EQUAL;
//                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
//                case LessThan ignored -> BinaryOperator.LESS_THAN;
//                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
//                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
//                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
//            };
//        }

    }

    sealed interface IntOP extends BinOperatorSymbol {

        record Add(Region region) implements IntOP {}
        record Subtract(Region region) implements IntOP {}
        record Multiply(Region region) implements IntOP {}
        record Divide(Region region) implements IntOP {}
        record Modulus(Region region) implements IntOP {}
        record Equal(Region region) implements IntOP {}
        record NotEqual(Region region) implements IntOP {}
        record LessThan(Region region) implements IntOP {}
        record LessThanOrEqual(Region region) implements IntOP {}
        record GreaterThan(Region region) implements IntOP {}
        record GreaterThanOrEqual(Region region) implements IntOP {}

        @Override
        default KType type() {
            var returnBool = switch (this) {
                case Equal ignored -> true;
                case GreaterThan ignored -> true;
                case GreaterThanOrEqual ignored -> true;
                case LessThan ignored -> true;
                case LessThanOrEqual ignored -> true;
                case NotEqual ignored -> true;
                default -> false;
            };
            if (returnBool) {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            } else {
                return new KType.PrimitiveType(KType.KPrimitive.INT);
            }
        }

        static @Nullable IntOP fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case ADD -> new Add(operator.region());
                case SUBTRACT -> new Subtract(operator.region());
                case MULTIPLY -> new Multiply(operator.region());
                case DIVIDE -> new Divide(operator.region());
                case MODULUS -> new Modulus(operator.region());
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL ->  new NotEqual(operator.region());
                case LESS_THAN ->  new LessThan(operator.region());
                case LESS_THAN_OR_EQUAL -> new LessThanOrEqual(operator.region());
                case GREATER_THAN -> new GreaterThan(operator.region());
                case GREATER_THAN_OR_EQUAL -> new GreaterThanOrEqual(operator.region());
                case STRICT_NOT_EQUAL, STRICT_EQUAL, CONCAT, AND, OR -> null;
            };
        }
//
//        default BinaryOperator operator() {
//            return switch (this) {
//                case Add ignored -> BinaryOperator.ADD;
//                case Subtract ignored -> BinaryOperator.SUBTRACT;
//                case Multiply ignored -> BinaryOperator.MULTIPLY;
//                case Divide ignored -> BinaryOperator.DIVIDE;
//                case Modulus ignored -> BinaryOperator.MODULUS;
//                case Equal ignored -> BinaryOperator.EQUAL;
//                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
//                case LessThan ignored -> BinaryOperator.LESS_THAN;
//                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
//                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
//                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
//            };
//        }

    }

    sealed interface BoolOP extends BinOperatorSymbol {
        record And(Region region) implements BoolOP {}
        record Or(Region region) implements BoolOP {}
        record Equal(Region region) implements BoolOP {}
        record NotEqual(Region region) implements BoolOP {}

        @Override
        default KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.BOOL);
        }

        static @Nullable BinOperatorSymbol.BoolOP fromOperator(RegionOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL -> new NotEqual(operator.region());
                case AND -> new And(operator.region());
                case OR -> new Or(operator.region());
                default -> null;
            };
        }

//        default BinaryOperator operator() {
//            return switch (this) {
//                case Equal ignored -> BinaryOperator.EQUAL;
//                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
//                case And ignored -> BinaryOperator.AND;
//                case Or ignored -> BinaryOperator.OR;
//            };
//        }
    }

}
