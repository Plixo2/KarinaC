package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.jvm.JavaResource;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;

import java.util.*;

public sealed interface KType {
    Region KARINA_LIB = new TextSource(
            new JavaResource("karina standard library"),
            List.of("su-su-su-supernova")
    ).emptyRegion();

    Region JAVA_LIB = new TextSource(
            new JavaResource("java standard library"),
            List.of("cafebabe")
    ).emptyRegion();

    ClassType ROOT = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.ROOT_PATH),
            List.of()
    );

    ClassType STRING = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.STRING_PATH),
            List.of()
    );

    ClassType NUMBER = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.NUMBER_PATH),
            List.of()
    );

    ClassType DOUBLE = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.DOUBLE_PATH),
            List.of()
    );

    ClassType FLOAT = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.FLOAT_PATH),
            List.of()
    );

    ClassType LONG = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.LONG_PATH),
            List.of()
    );

    ClassType INTEGER = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.INTEGER_PATH),
            List.of()
    );

    ClassType BOOLEAN = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.BOOLEAN_PATH),
            List.of()
    );
    static ClassType ITERABLE(KType iter_type) {
        return new ClassType(
                ClassPointer.of(JAVA_LIB, ClassPointer.ITERABLE_PATH),
                List.of(iter_type)
        );
    }
    static ClassType CLASS_TYPE(KType clsType) {
        return new ClassType(
                ClassPointer.of(JAVA_LIB, ClassPointer.CLASS_TYPE),
                List.of(clsType)
        );
    }
    ClassType THROWABLE = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.THROWABLE_PATH),
            List.of()
    );

    VoidType NONE = new VoidType();

    static void validateBuildIns(JKModel model) {
        validatePointer(model, ROOT);
        validatePointer(model, NUMBER);
        validatePointer(model, STRING);
        validatePointer(model, ITERABLE(ROOT));
        validatePointer(model, CLASS_TYPE(ROOT));
        validatePointer(model, THROWABLE);

        validatePointer(model, BOOLEAN);
        validatePointer(model, INTEGER);
        validatePointer(model, LONG);
        validatePointer(model, FLOAT);
        validatePointer(model, DOUBLE);

    }
    private static void validatePointer(JKModel model, ClassType classType) {
        var classModel = model.classModels().get(classType.pointer().path());

        if (classModel == null) {
            Log.bytecode(classType.pointer().region(), classType.toString(), "Build-in class not found");
            throw new Log.KarinaException();
        } else if (classModel.generics().size() != classType.generics().size()) {
            Log.bytecode(classType.pointer().region(), classType.toString(), "Build-in class has wrong number of generics");
            throw new Log.KarinaException();
        }

    }


    default KType unpack() {
        if (this instanceof Resolvable resolvable) {
            if (resolvable.get() != null) {
                return resolvable.get().unpack();
            }
        }
        return this;
    }

    default boolean isVoid() {
        return this.unpack() instanceof VoidType;
    }

    default boolean isPrimitive() {
        return this instanceof PrimitiveType primitive;
    }

    default boolean isRoot() {
        return this instanceof ClassType classType &&
                classType.pointer().isRoot();
    }


    enum KPrimitive {
        INT,
        FLOAT,
        BOOL,
        CHAR,
        DOUBLE,
        BYTE,
        SHORT,
        LONG;


        public boolean isNumeric() {
            return switch (this) {
                case INT, FLOAT, DOUBLE, LONG, SHORT, CHAR, BYTE -> true;
                case BOOL -> false;
            };
        }
    }

    final class VoidType implements KType {
        private VoidType() {}

        @Override
        public String toString() {
            return "void";
        }
    }

    record UnprocessedType(Region region, RegionOf<ObjectPath> name, List<KType> generics) implements KType {

        @Override
        public String toString() {
            return "~" + this.name.value().toString() + ("<" + String.join(", ", this.generics.stream().map(KType::toString).toList()) + ">");
        }
    }

    record ArrayType(KType elementType) implements KType {

        @Override
        public String toString() {
            return "[" + this.elementType + "]";
        }
    }

    record FunctionType(
            List<KType> arguments,
            KType returnType,
            List<? extends KType> interfaces
    ) implements KType {

        @Override
        public String toString() {
            var returnType = this.returnType.toString();
            var impls = this.interfaces.isEmpty() ? "" : " impl (" + String.join(", ", this.interfaces.stream().map(KType::toString).toList()) + ")";
            return "fn(" + String.join(", ", this.arguments.stream().map(KType::toString).toList()) + ")" + impls + " -> " + returnType;
        }
    }

    record ClassType(ClassPointer pointer, List<KType> generics) implements KType {

        @Override
        public String toString() {
            String suffix = "";;
            if (!this.generics.isEmpty()) {
                var names = String.join(", ", this.generics.stream().map(KType::toString).toList());
                suffix = "<" + names + ">";
            }

            return this.pointer.path().mkString(".") + suffix;
        }
    }


    record GenericLink(Generic link) implements KType {

        @Override
        public String toString() {
            return this.link.name();
        }

    }

    //TODO test if the Resolvable is backed by a Generic, or just unknown
    // (i think only used for arrays?)
    final class Resolvable implements KType {
        private final boolean canUsePrimitives;
        private final boolean canUseVoid;

        public Resolvable() {
            this(false, false);
        }

        public Resolvable(boolean canUsePrimitives, boolean canUseVoid) {
            this.canUsePrimitives = canUsePrimitives;
            this.canUseVoid = canUseVoid;
        }

        public boolean canUsePrimitives() {
            return this.canUsePrimitives;
        }

        private @Nullable KType resolved = null;

        public boolean isResolved() {
            return this.resolved != null;
        }

        public @Nullable KType get() {
            return this.resolved;
        }

        /**
         * Make sure to call this before calling {@link #tryResolve}
         */
        public boolean canResolve(Region checkingRegion, KType resolved) {

            /*
             * return true if the resolved type refers to itself.
             * this is valid, but we still don't want to resolve it to anything in 'tryResolve'
             */
            if (resolved == this) {
                return true;
            }
            if (!this.canUseVoid && resolved.isVoid()) {
                return false;
            }

            if (!this.canUsePrimitives && resolved.isPrimitive()) {
                return false;
            }

            var dependencies = new ArrayList<Types.TypeDependency>();
            Types.putDependencies(0, resolved, dependencies);
            var index = Types.getTypeDependencyIndex(this, dependencies);
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
            Log.attribError(new AttribError.TypeCycle(checkingRegion, msg, graph));
            throw new Log.KarinaException();
        }

        /**
         * Check {@link #canResolve} before calling this method.
         * This does not guarantee that the type will be resolved,
         * since the Type might be checked against itself.
         * Checking a resolvable against itself will return true.
         * Test with {@link #isResolved} to be sure if it was resolved after calling this method.
         */
        public void tryResolve(Region region, KType resolved) {

            if (this.resolved != null) {
                Log.temp(region, "Type already resolved");
                throw new Log.KarinaException();
            }

            if (resolved != this) {
                this.resolved = resolved;
            }

        }

        @Override
        public String toString() {
            var code = this.hashCode() & 0xFFFF;
            var readable = Integer.toHexString(code).toUpperCase();
            if (this.resolved == null) {
                return "?" + readable;
            } else {
                return "?" + readable + " as " + this.resolved;
            }
        }
    }


    record PrimitiveType(KPrimitive primitive) implements KType {

        @Override
        public String toString() {
            return this.primitive().toString().toLowerCase();
        }

    }


}
