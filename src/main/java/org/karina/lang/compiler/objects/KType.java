package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;

import java.util.List;

public sealed interface KType {

    Span region();

    enum KPrimitive {
        VOID,
        INT,
        FLOAT,
        BOOL,
        STRING,
        CHAR,
        DOUBLE,
        BYTE,
        SHORT,
        LONG
    }

    record UnprocessedType(Span region, SpanOf<ObjectPath> name, List<KType> generics) implements KType {}

    record ArrayType(Span region, KType elementType) implements KType {}

    record FunctionType(
            Span region,
            List<KType> arguments,
            @Nullable KType returnType,
            List<KType> interfaces
    ) implements KType {}

    record ClassType(Span region, SpanOf<ObjectPath> path, List<KType> generics) implements KType {}

    record GenericType(Span region, String name) implements KType {}
    record GenericLink(Span region, GenericType link) implements KType {}

    sealed interface PrimitiveType extends KType {

        default KPrimitive primitive() {

            return switch (this) {
                case VoidType ignored -> KPrimitive.VOID;
                case IntType ignored -> KPrimitive.INT;
                case FloatType ignored -> KPrimitive.FLOAT;
                case BoolType ignored -> KPrimitive.BOOL;
                case StringType ignored -> KPrimitive.STRING;
                case CharType ignored -> KPrimitive.CHAR;
                case DoubleType ignored -> KPrimitive.DOUBLE;
                case ByteType ignored -> KPrimitive.BYTE;
                case ShortType ignored -> KPrimitive.SHORT;
                case LongType ignored -> KPrimitive.LONG;
            };

        }
        record VoidType(Span region) implements PrimitiveType {}
        record IntType(Span region) implements PrimitiveType {}
        record FloatType(Span region) implements PrimitiveType {}
        record BoolType(Span region) implements PrimitiveType {}
        record StringType(Span region) implements PrimitiveType {}
        record CharType(Span region) implements PrimitiveType{}
        record DoubleType(Span region) implements PrimitiveType {}
        record ByteType(Span region) implements PrimitiveType {}
        record ShortType(Span region) implements PrimitiveType {}
        record LongType(Span region) implements PrimitiveType {}
    }

}
