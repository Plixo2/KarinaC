package org.karina.lang.compiler.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;

import java.util.List;

public sealed interface KType {

    Span region();

    default KType unpack() {
        if (this instanceof Resolvable resolvable) {
            if (resolvable.get() != null) {
                return resolvable.get().unpack();
            }
        }
        return this;
    }

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

    record UnprocessedType(Span region, SpanOf<ObjectPath> name, List<KType> generics) implements KType {

        @Override
        public String toString() {
            return "~" + this.name.value().toString() + ("<" + String.join(", ", this.generics.stream().map(KType::toString).toList()) + ">");
        }
    }

    record ArrayType(Span region, KType elementType) implements KType {

        @Override
        public String toString() {
            return "[" + this.elementType + "]";
        }
    }

    record FunctionType(
            Span region,
            List<KType> arguments,
            @Nullable KType returnType,
            List<KType> interfaces
    ) implements KType {

        @Override
        public String toString() {
            var returnType = this.returnType == null ? "void" : this.returnType.toString();
            var impls = this.interfaces.isEmpty() ? "" : " impl " + String.join(", ", this.interfaces.stream().map(KType::toString).toList());
            return "fn(" + String.join(", ", this.arguments.stream().map(KType::toString).toList()) + ") -> " + returnType + impls;
        }
    }

    record ClassType(Span region, SpanOf<ObjectPath> path, List<KType> generics) implements KType {

        @Override
        public String toString() {
            return this.path.value().mkString(".") + ("<" + String.join(", ", this.generics.stream().map(KType::toString).toList()) + ">");
        }

    }

    record GenericLink(Span region, Generic link) implements KType {

        @Override
        public String toString() {
            return this.link.name();
        }

    }

    @RequiredArgsConstructor
    final class Resolvable implements KType {
        @Getter
        @Accessors(fluent = true)
        private final Span region;

        private @Nullable KType resolved = null;

        public boolean isResolved() {
            return this.resolved != null;
        }

        public @Nullable KType get() {
            return this.resolved;
        }

        public void resolve(KType resolved) {

            Resolvable resolvable = resolved instanceof Resolvable resolved1 ? resolved1 : null;
            while (resolvable != null) {
                if (resolvable == this) {
                    Log.attribError(new AttribError.TypeCycle(this.region, "Bad cycle"));
                    throw new Log.KarinaException();
                }
                resolvable = resolvable.get() instanceof Resolvable resolvable1 ? resolvable1 : null;
            }

            if (this.resolved != null) {
                Log.temp(this.region, "Type already resolved");
                throw new Log.KarinaException();
            }
            this.resolved = resolved;

        }

        @Override
        public String toString() {
            if (this.resolved == null) {
                return "?";
            } else {
                return "? -> " + this.resolved;
            }
        }
    }

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
        record VoidType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record IntType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record FloatType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record BoolType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record StringType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record CharType(Span region) implements PrimitiveType{
            @Override
            public String toString() {
                return mkString();
            }
        }
        record DoubleType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record ByteType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record ShortType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }
        record LongType(Span region) implements PrimitiveType {
            @Override
            public String toString() {
                return mkString();
            }
        }


        default boolean isNumeric() {
            return switch (this.primitive()) {
                case INT, FLOAT, DOUBLE, LONG, SHORT, CHAR, BYTE -> true;
                default -> false;
            };
        }

        default String mkString() {
            return this.primitive().toString().toLowerCase();
        }

    }

}
