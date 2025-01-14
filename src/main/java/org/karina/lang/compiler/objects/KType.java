package org.karina.lang.compiler.objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.utils.SpanOf;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;

import java.util.*;

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

    default boolean isVoid() {
        return this instanceof PrimitiveType.VoidType;
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

    record AnyClass(Span region) implements KType {
        @Override
        public String toString() {
            return "Any";
        }
    }

    record GenericLink(Span region, Generic link) implements KType {

        @Override
        public String toString() {
            return this.link.name();
        }

    }

    final class Resolvable implements KType {
        @Getter
        @Accessors(fluent = true)
        private final Span region;

        private int hashCode = 0;

        public Resolvable(Span region) {
            var start = region.start();
            var end = region.end();
            this.hashCode = Objects.hash(start, end);

            this.region = region;
        }

        private @Nullable KType resolved = null;

        public boolean isResolved() {
            return this.resolved != null;
        }

        public @Nullable KType get() {
            return this.resolved;
        }

        public boolean canResolve(Span checkingRegion, KType resolved) {
            if (resolved == this) {
                return true;
            }

            var dependencies = new ArrayList<TypeDependency>();
            getDependencies(0, resolved, dependencies);
            var index = getIndex(this, dependencies);
            if (index == -1) {
                return true;
            }

            var linearDependencies = new ArrayList<KType>();
            var dependency = dependencies.get(index);
            linearDependencies.add(dependency.type());
            var previousLevel = dependency.level();
            var currentIndex = index;
            while (currentIndex >= 0) {
                dependency = dependencies.get(currentIndex);
                if (dependency.level() == previousLevel) {
                    currentIndex--;
                    continue;
                }
                linearDependencies.add(dependency.type());
                previousLevel = dependency.level();
            }
            var related = linearDependencies.stream().map(KType::region).toList();
            var readable = String.join(" via ", linearDependencies.stream().map(Object::toString).toList().reversed());
            var graph = new ArrayList<String>();
            for (var i = 0; i < dependencies.size(); i++) {
                var typeDependency = dependencies.get(i);
                String suffix = "";
                if (i == index) {
                    suffix = " <- Source";
                } else if (i == 0) {
                    suffix = " <- Target";
                }

                graph.add("    ".repeat(typeDependency.level()) + typeDependency.type() + suffix);
            }
            var msg = "Lazy Type cycle: " + readable;
            Log.attribError(new AttribError.TypeCycle(checkingRegion, msg, related, graph));
            throw new Log.KarinaException();
        }

        public void tryResolve(KType resolved) {

            if (this.resolved != null) {
                Log.temp(this.region, "Type already resolved");
                throw new Log.KarinaException();
            }

            if (resolved != this) {
                this.resolved = resolved;
            }

        }

        @Override
        public String toString() {
            var code = this.hashCode & 0xFFFF;
            var readable = Integer.toHexString(code).toUpperCase();
            if (this.resolved == null) {
                return "?" + readable;
            } else {
                return "?" + readable + " as " + this.resolved;
//                return this.resolved.toString();
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

    private static void getDependencies(int level, KType type, List<TypeDependency> dependencies) {
        switch (type) {
            case ArrayType arrayType -> {
                dependencies.add(new TypeDependency(arrayType, level));
                getDependencies(level + 1, arrayType.elementType(), dependencies);
            }
            case ClassType classType -> {
                dependencies.add(new TypeDependency(classType, level));
                for (var generic : classType.generics()) {
                    getDependencies(level + 1, generic, dependencies);
                }
            }
            case FunctionType functionType -> {
                dependencies.add(new TypeDependency(functionType, level));
                for (var argument : functionType.arguments()) {
                    getDependencies(level + 1, argument, dependencies);
                }
                if (functionType.returnType() != null) {
                    getDependencies(level + 1, functionType.returnType(), dependencies);
                }
                for (var impl : functionType.interfaces()) {
                    getDependencies(level + 1, impl, dependencies);
                }
            }
            case GenericLink genericLink -> {
                dependencies.add(new TypeDependency(genericLink, level));
            }
            case PrimitiveType primitiveType -> {
                dependencies.add(new TypeDependency(primitiveType, level));
            }
            case Resolvable resolvable -> {
                dependencies.add(new TypeDependency(resolvable, level));
                if (resolvable.get() != null) {
                    getDependencies(level + 1, resolvable.get(), dependencies);
                }
            }
            case AnyClass anyClass -> {
            }
            case UnprocessedType unprocessedType -> {
            }
        }
    }

    record TypeDependency(KType type, int level) { }

    private static int getIndex(KType type, List<TypeDependency> dependencies) {
        for (var i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).type() == type) {
                return i;
            }
        }
        return -1;
    }
}
