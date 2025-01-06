package org.karina.lang.compiler.stages.symbols;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.objects.BinaryOperator;
import org.karina.lang.compiler.objects.KType;

public sealed interface BinOperatorSymbol {
    Span region();
    KType type();
    BinaryOperator operator();


    sealed interface FloatOP extends BinOperatorSymbol {
        record Add(Span region) implements FloatOP {}
        record Subtract(Span region) implements FloatOP {}
        record Multiply(Span region) implements FloatOP {}
        record Divide(Span region) implements FloatOP {}
        record Modulus(Span region) implements FloatOP {}
        record Equal(Span region) implements FloatOP {}
        record NotEqual(Span region) implements FloatOP {}
        record LessThan(Span region) implements FloatOP {}
        record LessThanOrEqual(Span region) implements FloatOP {}
        record GreaterThan(Span region) implements FloatOP {}
        record GreaterThanOrEqual(Span region) implements FloatOP {}

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
                return new KType.PrimitiveType.BoolType(region());
            } else {
                return new KType.PrimitiveType.FloatType(region());
            }
        }


        static @Nullable FloatOP fromOperator(SpanOf<BinaryOperator> operator) {
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
                case CONCAT, AND, OR -> null;
            };
        }

        default BinaryOperator operator() {
            return switch (this) {
                case Add ignored -> BinaryOperator.ADD;
                case Subtract ignored -> BinaryOperator.SUBTRACT;
                case Multiply ignored -> BinaryOperator.MULTIPLY;
                case Divide ignored -> BinaryOperator.DIVIDE;
                case Modulus ignored -> BinaryOperator.MODULUS;
                case Equal ignored -> BinaryOperator.EQUAL;
                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
                case LessThan ignored -> BinaryOperator.LESS_THAN;
                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
            };
        }

    }

    sealed interface DoubleOP extends BinOperatorSymbol {
        record Add(Span region) implements DoubleOP {}
        record Subtract(Span region) implements DoubleOP {}
        record Multiply(Span region) implements DoubleOP {}
        record Divide(Span region) implements DoubleOP {}
        record Modulus(Span region) implements DoubleOP {}
        record Equal(Span region) implements DoubleOP {}
        record NotEqual(Span region) implements DoubleOP {}
        record LessThan(Span region) implements DoubleOP {}
        record LessThanOrEqual(Span region) implements DoubleOP {}
        record GreaterThan(Span region) implements DoubleOP {}
        record GreaterThanOrEqual(Span region) implements DoubleOP {}

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
                return new KType.PrimitiveType.BoolType(region());
            } else {
                return new KType.PrimitiveType.DoubleType(region());
            }
        }

        static @Nullable BinOperatorSymbol.DoubleOP fromOperator(SpanOf<BinaryOperator> operator) {
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
                case CONCAT, AND, OR -> null;
            };
        }

        default BinaryOperator operator() {
            return switch (this) {
                case Add ignored -> BinaryOperator.ADD;
                case Subtract ignored -> BinaryOperator.SUBTRACT;
                case Multiply ignored -> BinaryOperator.MULTIPLY;
                case Divide ignored -> BinaryOperator.DIVIDE;
                case Modulus ignored -> BinaryOperator.MODULUS;
                case Equal ignored -> BinaryOperator.EQUAL;
                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
                case LessThan ignored -> BinaryOperator.LESS_THAN;
                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
            };
        }

    }

    sealed interface LongOP extends BinOperatorSymbol {
        record Add(Span region) implements LongOP {}
        record Subtract(Span region) implements LongOP {}
        record Multiply(Span region) implements LongOP {}
        record Divide(Span region) implements LongOP {}
        record Modulus(Span region) implements LongOP {}
        record Equal(Span region) implements LongOP {}
        record NotEqual(Span region) implements LongOP {}
        record LessThan(Span region) implements LongOP {}
        record LessThanOrEqual(Span region) implements LongOP {}
        record GreaterThan(Span region) implements LongOP {}
        record GreaterThanOrEqual(Span region) implements LongOP {}

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
                return new KType.PrimitiveType.BoolType(region());
            } else {
                return new KType.PrimitiveType.LongType(region());
            }
        }

        static @Nullable BinOperatorSymbol.LongOP fromOperator(SpanOf<BinaryOperator> operator) {
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
                case CONCAT, AND, OR -> null;
            };
        }

        default BinaryOperator operator() {
            return switch (this) {
                case Add ignored -> BinaryOperator.ADD;
                case Subtract ignored -> BinaryOperator.SUBTRACT;
                case Multiply ignored -> BinaryOperator.MULTIPLY;
                case Divide ignored -> BinaryOperator.DIVIDE;
                case Modulus ignored -> BinaryOperator.MODULUS;
                case Equal ignored -> BinaryOperator.EQUAL;
                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
                case LessThan ignored -> BinaryOperator.LESS_THAN;
                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
            };
        }

    }

    sealed interface IntOP extends BinOperatorSymbol {

        record Add(Span region) implements IntOP {}
        record Subtract(Span region) implements IntOP {}
        record Multiply(Span region) implements IntOP {}
        record Divide(Span region) implements IntOP {}
        record Modulus(Span region) implements IntOP {}
        record Equal(Span region) implements IntOP {}
        record NotEqual(Span region) implements IntOP {}
        record LessThan(Span region) implements IntOP {}
        record LessThanOrEqual(Span region) implements IntOP {}
        record GreaterThan(Span region) implements IntOP {}
        record GreaterThanOrEqual(Span region) implements IntOP {}

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
                return new KType.PrimitiveType.BoolType(region());
            } else {
                return new KType.PrimitiveType.IntType(region());
            }
        }

        static @Nullable IntOP fromOperator(SpanOf<BinaryOperator> operator) {
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
                case CONCAT, AND, OR -> null;
            };
        }

        default BinaryOperator operator() {
            return switch (this) {
                case Add ignored -> BinaryOperator.ADD;
                case Subtract ignored -> BinaryOperator.SUBTRACT;
                case Multiply ignored -> BinaryOperator.MULTIPLY;
                case Divide ignored -> BinaryOperator.DIVIDE;
                case Modulus ignored -> BinaryOperator.MODULUS;
                case Equal ignored -> BinaryOperator.EQUAL;
                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
                case LessThan ignored -> BinaryOperator.LESS_THAN;
                case LessThanOrEqual ignored -> BinaryOperator.LESS_THAN_OR_EQUAL;
                case GreaterThan ignored -> BinaryOperator.GREATER_THAN;
                case GreaterThanOrEqual ignored -> BinaryOperator.GREATER_THAN_OR_EQUAL;
            };
        }

    }

    sealed interface BoolOP extends BinOperatorSymbol {
        record And(Span region) implements BoolOP {}
        record Or(Span region) implements BoolOP {}
        record Equal(Span region) implements BoolOP {}
        record NotEqual(Span region) implements BoolOP {}

        @Override
        default KType type() {
            return new KType.PrimitiveType.BoolType(region());
        }

        static @Nullable BinOperatorSymbol.BoolOP fromOperator(SpanOf<BinaryOperator> operator) {
            return switch (operator.value()) {
                case EQUAL -> new Equal(operator.region());
                case NOT_EQUAL -> new NotEqual(operator.region());
                case AND -> new And(operator.region());
                case OR -> new Or(operator.region());
                default -> null;
            };
        }

        default BinaryOperator operator() {
            return switch (this) {
                case Equal ignored -> BinaryOperator.EQUAL;
                case NotEqual ignored -> BinaryOperator.NOT_EQUAL;
                case And ignored -> BinaryOperator.AND;
                case Or ignored -> BinaryOperator.OR;
            };
        }
    }

}
