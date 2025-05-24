package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.JavaResource;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;

import java.util.*;


/**
 * Represents a type in the Karina language.
 */
public sealed interface KType {

    Region KARINA_LIB = new TextSource(
            new JavaResource("karina standard library"),
            "<karina sdk>"
    ).emptyRegion();

    Region JAVA_LIB = new TextSource(
            new JavaResource("java standard library"),
         "<java sdk>"
    ).emptyRegion();

    VoidType NONE = new VoidType();
    PrimitiveType INT = new PrimitiveType(KPrimitive.INT);
    PrimitiveType DOUBLE = new PrimitiveType(KPrimitive.DOUBLE);
    PrimitiveType LONG = new PrimitiveType(KPrimitive.LONG);
    PrimitiveType FLOAT = new PrimitiveType(KPrimitive.FLOAT);
    PrimitiveType CHAR = new PrimitiveType(KPrimitive.CHAR);
    PrimitiveType BYTE = new PrimitiveType(KPrimitive.BYTE);
    PrimitiveType SHORT = new PrimitiveType(KPrimitive.SHORT);
    PrimitiveType BOOL = new PrimitiveType(KPrimitive.BOOL);

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

    ClassType DOUBLE_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.DOUBLE_PATH),
            List.of()
    );

    ClassType FLOAT_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.FLOAT_PATH),
            List.of()
    );

    ClassType LONG_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.LONG_PATH),
            List.of()
    );

    ClassType INTEGER_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.INTEGER_PATH),
            List.of()
    );

    ClassType BOOLEAN_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.BOOLEAN_PATH),
            List.of()
    );

    ClassType CHARACTER_CLASS = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.CHARACTER_PATH),
            List.of()
    );

    ClassType THROWABLE = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.THROWABLE_PATH),
            List.of()
    );

    ClassType MATCH_EXCEPTION = new ClassType(
            ClassPointer.of(JAVA_LIB, ClassPointer.MATCH_EXCEPTION_PATH),
            List.of()
    );

    ClassType KARINA_RANGE = new ClassType(
            ClassPointer.of(KARINA_LIB, ClassPointer.RANGE_PATH),
            List.of()
    );

    ClassType STRING_INTERPOLATION = new ClassType(
            ClassPointer.of(KARINA_LIB, ClassPointer.STRING_INTERPOLATION_PATH),
            List.of()
    );

    static ClassType ITERABLE(KType iter_type) {
        return new ClassType(
                ClassPointer.of(JAVA_LIB, ClassPointer.ITERABLE_PATH),
                List.of(iter_type)
        );
    }

    static ClassType ITERATOR(KType iter_type) {
        return new ClassType(
                ClassPointer.of(JAVA_LIB, ClassPointer.ITERATOR_PATH),
                List.of(iter_type)
        );
    }

    static ClassType CLASS_TYPE(KType clsType) {
        return new ClassType(
                ClassPointer.of(JAVA_LIB, ClassPointer.CLASS_TYPE_PATH),
                List.of(clsType)
        );
    }
    static ClassType KARINA_OPTION(KType inner) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_PATH),
                List.of(inner)
        );
    }
    static ClassType KARINA_OPTION_SOME(KType inner) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_SOME_PATH),
                List.of(inner)
        );
    }
    static ClassType KARINA_OPTION_NONE(KType inner) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_NONE_PATH),
                List.of(inner)
        );
    }
    static ClassType KARINA_RESULT(KType ok, KType err) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_PATH),
                List.of(ok, err)
        );
    }
    static ClassType KARINA_RESULT_OK(KType ok, KType err) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_OK_PATH),
                List.of(ok, err)
        );
    }
    static ClassType KARINA_RESULT_ERR(KType ok, KType err) {
        return new ClassType(
                ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_ERR_PATH),
                List.of(ok, err)
        );
    }


    static @Nullable ClassPointer FUNCTION_BASE(Model model, int args, boolean doesReturn) {
        final String FUNCTIONS_NAME = "Function";
        var name = FUNCTIONS_NAME + args + "_" + (doesReturn ? "1" : "0");


        var objectPath = ClassPointer.FUNCTIONS_BASE.append(name);
        Log.recordType(Log.LogTypes.CLOSURE, objectPath.toString());
        return model.getClassPointer(KARINA_LIB, objectPath);
    }


    static void validateBuildIns(Model model) {
        validatePointer(model, ROOT);
        validatePointer(model, NUMBER);
        validatePointer(model, STRING);
        validatePointer(model, ITERABLE(ROOT));
        validatePointer(model, ITERATOR(ROOT));
        validatePointer(model, CLASS_TYPE(ROOT));
        validatePointer(model, THROWABLE);
        validatePointer(model, MATCH_EXCEPTION);

        validatePointer(model, BOOLEAN_CLASS);
        validatePointer(model, CHARACTER_CLASS);
        validatePointer(model, INTEGER_CLASS);
        validatePointer(model, LONG_CLASS);
        validatePointer(model, FLOAT_CLASS);
        validatePointer(model, DOUBLE_CLASS);

        validatePointer(model, KARINA_RANGE);
        validatePointer(model, STRING_INTERPOLATION);
        validatePointer(model, KARINA_OPTION(ROOT));
        validatePointer(model, KARINA_OPTION_SOME(ROOT));
        validatePointer(model, KARINA_OPTION_NONE(ROOT));
        validatePointer(model, KARINA_RESULT(ROOT, ROOT));
        validatePointer(model, KARINA_RESULT_ERR(ROOT, ROOT));
        validatePointer(model, KARINA_RESULT_OK(ROOT, ROOT));
    }

    private static void validatePointer(Model model, ClassType classType) {
        var classPointer = model.getClassPointer(classType.pointer().region(), classType.pointer().path());

        if (classPointer == null) {
            Log.bytecode(classType.pointer().region(), classType.toString(), "Build-in class not found");
            throw new Log.KarinaException();
        }
        var classModel = model.getClass(classPointer);
        if (classModel.generics().size() != classType.generics().size()) {
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
        return this.unpack() instanceof PrimitiveType;
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

    /**
     * Represents a type that is not yet processed. (before the import stage)
     * This type is no longer valid, after the import stage.
     */
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

            return this.pointer.path().mkString("::") + suffix;
        }
    }

    /**
     * Represents a generic type. The Generic is either defined on a class or a method.
     * @param link The generic type.
     */
    record GenericLink(Generic link) implements KType {

        @Override
        public String toString() {
            return this.link.name();
        }

    }

    /**
     * Represents a type that can resolve to another type.
     * This is mainly used for generics but also for other expressions.
     *
     * TODO test if the Resolvable is backed by a Generic, or just unknown
     */
    final class Resolvable implements KType {

        /**
         * Generics have to be objects, so this is everywhere else.
         */
        private final boolean canUsePrimitives;
        /**
         * Used for return types of closures.
         */
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
         * Make sure to call this before calling {@link #tryResolve}. Otherwise there might be cycles.
         */
        public boolean canResolve(Region checkingRegion, KType resolved) {
            //TODO this?
            resolved = resolved.unpack();
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

            // no cycle detected
            if (index == -1) {
                return true;
            }

            // cycle detected
            // build the cycle graph for logging
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
            var from = "while trying to assign " + this + " to " + resolved;
            var msg = "Lazy Type cycle: " + readable + " (" + from + ")";
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
            resolved = resolved.unpack();

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
