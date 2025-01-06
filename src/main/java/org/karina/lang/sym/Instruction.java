package org.karina.lang.sym;

import org.karina.lang.compiler.objects.BinaryOperator;

public sealed interface Instruction {

    record PushTrue() implements Instruction {}
    record PushFalse() implements Instruction {}
    record PushLong(long value) implements Instruction {}
    record PushFloat(double value) implements Instruction {}
    record IntToFloat() implements Instruction {}
    record FloatToInt() implements Instruction {}
    record Return() implements Instruction {}
    record ReturnVoid() implements Instruction {}
    record StoreVariable(int local) implements Instruction {}
    record StoreArray() implements Instruction {}
    record StoreField(int offset) implements Instruction {}
    record LoadVariable(int local) implements Instruction {}
    record LoadArray() implements Instruction {}
    record LoadField(int offset) implements Instruction {}

    record FloatMath(NumberMathOp op) implements Instruction {}
    record LongMath(NumberMathOp op) implements Instruction {}
    record BoolMath(BoolMathOp op) implements Instruction {}
    record Not() implements Instruction {}
    record Negate() implements Instruction {}
    record Pop() implements Instruction {}

    record NewObject(long hash, int size) implements Instruction {}

    enum NumberMathOp {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MODULUS,
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL;


        public static NumberMathOp fromBinaryOp(BinaryOperator operator) {
            return switch (operator) {
                case ADD -> NumberMathOp.ADD;
                case SUBTRACT -> NumberMathOp.SUBTRACT;
                case MULTIPLY -> NumberMathOp.MULTIPLY;
                case DIVIDE -> NumberMathOp.DIVIDE;
                case MODULUS -> NumberMathOp.MODULUS;
                case EQUAL -> NumberMathOp.EQUAL;
                case NOT_EQUAL -> NumberMathOp.NOT_EQUAL;
                case LESS_THAN -> NumberMathOp.LESS_THAN;
                case LESS_THAN_OR_EQUAL -> NumberMathOp.LESS_THAN_OR_EQUAL;
                case GREATER_THAN -> NumberMathOp.GREATER_THAN;
                case GREATER_THAN_OR_EQUAL -> NumberMathOp.GREATER_THAN_OR_EQUAL;
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        }
    }

    enum BoolMathOp {
        AND,
        OR,
        EQUAL,
        NOT_EQUAL;

        public static BoolMathOp fromBinaryOp(BinaryOperator operator) {
            return switch (operator) {
                case EQUAL -> BoolMathOp.EQUAL;
                case NOT_EQUAL -> BoolMathOp.NOT_EQUAL;
                case AND -> BoolMathOp.AND;
                case OR -> BoolMathOp.OR;
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        }
    }

}
