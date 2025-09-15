package org.karina.lang.compiler.utils;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.JavaResource;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;

import java.util.*;


/**
 * Represents a type in the Karina language.
 */
public sealed interface KType {

    Region KARINA_LIB = new DefaultTextSource(
            new JavaResource("karina standard library"),
            "<karina sdk>"
    ).emptyRegion();

    Region JAVA_LIB = new DefaultTextSource(
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

    ClassType ROOT = ClassPointer.of(JAVA_LIB, ClassPointer.ROOT_PATH).implement(List.of());

    ClassType STRING = ClassPointer.of(JAVA_LIB, ClassPointer.STRING_PATH).implement(List.of());

    ClassType NUMBER = ClassPointer.of(JAVA_LIB, ClassPointer.NUMBER_PATH).implement(List.of());

    ClassType DOUBLE_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.DOUBLE_PATH).implement(List.of());

    ClassType FLOAT_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.FLOAT_PATH).implement(List.of());

    ClassType LONG_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.LONG_PATH).implement(List.of());

    ClassType INTEGER_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.INTEGER_PATH).implement(List.of());

    ClassType BOOLEAN_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.BOOLEAN_PATH).implement(List.of());

    ClassType CHARACTER_CLASS = ClassPointer.of(JAVA_LIB, ClassPointer.CHARACTER_PATH).implement(List.of());

    ClassType THROWABLE = ClassPointer.of(JAVA_LIB, ClassPointer.THROWABLE_PATH).implement(List.of());

    ClassType AUTO_CLOSEABLE = ClassPointer.of(JAVA_LIB, ClassPointer.AUTO_CLOSEABLE_PATH).implement(List.of());

    ClassType MATCH_EXCEPTION = ClassPointer.of(JAVA_LIB, ClassPointer.MATCH_EXCEPTION_PATH).implement(List.of());


    ClassType KARINA_RANGE = ClassPointer.of(KARINA_LIB, ClassPointer.RANGE_PATH).implement(List.of());

    ClassType STRING_INTERPOLATION = ClassPointer.of(KARINA_LIB, ClassPointer.STRING_INTERPOLATION_PATH).implement(List.of());

    static ClassType ITERABLE(KType iter_type) {
        return ClassPointer.of(JAVA_LIB, ClassPointer.ITERABLE_PATH).implement(List.of(iter_type));
    }

    static ClassType ITERATOR(KType iter_type) {
        return ClassPointer.of(JAVA_LIB, ClassPointer.ITERATOR_PATH).implement(List.of(iter_type));
    }

    static ClassType CLASS_TYPE(KType clsType) {
        return ClassPointer.of(JAVA_LIB, ClassPointer.CLASS_TYPE_PATH).implement(List.of(clsType));
    }
    static ClassType KARINA_OPTION(KType inner) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_PATH).implement(List.of(inner));
    }
    static ClassType KARINA_OPTION_SOME(KType inner) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_SOME_PATH).implement(List.of(inner));
    }
    static ClassType KARINA_OPTION_NONE(KType inner) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.OPTION_NONE_PATH).implement(List.of(inner));
    }
    static ClassType KARINA_RESULT(KType ok, KType err) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_PATH).implement(List.of(ok, err));
    }
    static ClassType KARINA_RESULT_OK(KType ok, KType err) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_OK_PATH).implement(List.of(ok, err));
    }
    static ClassType KARINA_RESULT_ERR(KType ok, KType err) {
        return ClassPointer.of(KARINA_LIB, ClassPointer.RESULT_ERR_PATH).implement(List.of(ok, err));
    }


    static @Nullable ClassPointer FUNCTION_BASE(Model model, int args, boolean doesReturn) {
        final String FUNCTIONS_NAME = "Function";
        var name = FUNCTIONS_NAME + args + "_" + (doesReturn ? "1" : "0");

        var objectPath = ClassPointer.FUNCTIONS_BASE.append(name);
        return model.getClassPointer(KARINA_LIB, objectPath);
    }


    static void validateBuildIns(Context c, Model model) {
        validatePointer(c, model, ROOT);
        validatePointer(c, model, NUMBER);
        validatePointer(c, model, STRING);
        validatePointer(c, model, ITERABLE(ROOT));
        validatePointer(c, model, ITERATOR(ROOT));
        validatePointer(c, model, CLASS_TYPE(ROOT));
        validatePointer(c, model, THROWABLE);
        validatePointer(c, model, MATCH_EXCEPTION);

        validatePointer(c, model, BOOLEAN_CLASS);
        validatePointer(c, model, CHARACTER_CLASS);
        validatePointer(c, model, INTEGER_CLASS);
        validatePointer(c, model, LONG_CLASS);
        validatePointer(c, model, FLOAT_CLASS);
        validatePointer(c, model, DOUBLE_CLASS);

        validatePointer(c,model, KARINA_RANGE);
        validatePointer(c,model, STRING_INTERPOLATION);
        validatePointer(c,model, KARINA_OPTION(ROOT));
        validatePointer(c,model, KARINA_OPTION_SOME(ROOT));
        validatePointer(c,model, KARINA_OPTION_NONE(ROOT));
        validatePointer(c,model, KARINA_RESULT(ROOT, ROOT));
        validatePointer(c,model, KARINA_RESULT_ERR(ROOT, ROOT));
        validatePointer(c,model, KARINA_RESULT_OK(ROOT, ROOT));
    }

    private static void validatePointer(Context c, Model model, ClassType classType) {
        var classPointer = model.getClassPointer(classType.pointer().region(), classType.pointer().path());

        if (classPointer == null) {
            Log.bytecode(c, classType.pointer().region(), classType.toString(), "Build-in class not found");
            throw new Log.KarinaException();
        }
        var classModel = model.getClass(classPointer);
        if (classModel.generics().size() != classType.generics().size()) {
            Log.bytecode(c, classType.pointer().region(), classType.toString(), "Build-in class has wrong number of generics");
            throw new Log.KarinaException();
        }
    }



    default KType unpack() {
        if (this instanceof Resolvable resolvable) {
            var resolved = resolvable.get();
            if (resolved != null) {
                return resolved.unpack();
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

    String toString(Map<KType, Object> dejavu);

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

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof KType type) obj = type.unpack();
            return obj instanceof VoidType;
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            return toString();
        }
    }

    /**
     * Represents a type that is not yet processed. (before the import stage)
     * This type is no longer valid, after the import stage.
     */
    record UnprocessedType(Region region, RegionOf<ObjectPath> name, List<KType> generics) implements KType {

        @Override
        public String toString() {
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return this.name.value().toString() + (this.generics.isEmpty() ? "" : "<...>");
            }
            dejavu.put(this, this);
            var generics = String.join(
                    ", ",
                    this.generics.stream().map(ref -> ref.toString(dejavu)).toList()
            );
            dejavu.remove(this);
            return "~" + this.name.value().toString() + ("<" + generics + ">");

        }
    }

    record ArrayType(KType elementType) implements KType {

        @Override
        public String toString() {
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();
            return object instanceof ArrayType(KType type) && Objects.equals(this.elementType, type);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.elementType);
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return "[...]";
            }
            dejavu.put(this, this);
            var string = "[" + this.elementType.toString(dejavu) + "]";
            dejavu.remove(this);
            return string;
        }
    }

    record FunctionType(
            List<KType> arguments,
            KType returnType,
            List<? extends KType> interfaces
    ) implements KType {


        @Override
        public String toString() {
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();
            return object instanceof FunctionType(var args, var type, var interfaces2) &&
                    Objects.equals(this.returnType, type) &&
                    Objects.equals(this.arguments, args) &&
                    Objects.equals(this.interfaces, interfaces2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.arguments, this.returnType, this.interfaces);
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return "fn(...) -> ...";
            }
            dejavu.put(this, this);

            var returnType = this.returnType.toString(dejavu);
            var interfaces = String.join(
                    ", ",
                    this.interfaces.stream().map(ref -> ref.toString(dejavu)).toList()
            );
            var arguments = this.arguments.stream().map(ref -> ref.toString(dejavu)).toList();
            var impls = this.interfaces.isEmpty() ? "" : " impl (" + interfaces + ")";
            dejavu.remove(this);
            return "fn(" + String.join(", ", arguments) + ")" + impls + " -> " + returnType;
        }
    }

    record ClassType(ClassPointer pointer, List<KType> generics) implements KType {

        @Override
        public String toString() {
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();
            return object instanceof ClassType(ClassPointer pointer1, List<KType> generics1) &&
                    Objects.equals(this.pointer, pointer1) &&
                    Objects.equals(this.generics, generics1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.pointer, this.generics);
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return this.pointer.path().mkString("::") + (this.generics.isEmpty() ? "" : "<...>");
            }
            dejavu.put(this, this);

            String suffix = "";;
            if (!this.generics.isEmpty()) {
                var names = String.join(", ", this.generics.stream().map(ref -> ref.toString(dejavu)).toList());
                suffix = "<" + names + ">";
            }
            dejavu.remove(this);
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
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return this.link.name();
            }
            dejavu.put(this, this);
            var constraints = new ArrayList<KType>();
            if (this.link.superType() != null) {
                constraints.add(this.link.superType());
            }
            constraints.addAll(this.link.bounds());

            var constraintStr = constraints.stream().map(ref -> ref.toString(dejavu)).toList();
            dejavu.remove(this);
            return this.link.name() + (constraints.isEmpty() ? "" : ": " + String.join(" + ", constraintStr));
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();
            return object instanceof GenericLink(Generic link1) && Objects.equals(this.link, link1);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.link);
        }

    }

    /**
     * Represents a type that can resolve to another type.
     * This is mainly used for generics but also for other expressions.
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

        @Getter
        @Accessors(fluent = true)
        private final List<KType> constraints;


        private Resolvable(boolean canUsePrimitives, boolean canUseVoid, List<KType> constraints) {
            this.canUsePrimitives = canUsePrimitives;
            this.canUseVoid = canUseVoid;
            this.constraints = constraints;
        }

        public static Resolvable newInstance() {
            return new Resolvable(false, false, List.of());
        }

        public static Resolvable newInstanceAllowVoid() {
            return new Resolvable(false, true, List.of());
        }


        public static Resolvable newInstanceAllowPrimitives() {
            return new Resolvable(true, false, List.of());
        }

        public static Resolvable newInstanceFromGeneric(Generic generic) {
            var constraints = new ArrayList<KType>();
            if (generic.superType() != null) {
                constraints.add(generic.superType());
            }
            constraints.addAll(generic.bounds());

            return new Resolvable(false, false, constraints);
        }

        public boolean canUsePrimitives() {
            return this.canUsePrimitives;
        }

        public boolean canUseVoid() {
            return this.canUseVoid;
        }

        private @Nullable KType resolved = null;

        public boolean isResolved() {
            return this.resolved != null;
        }

        public @Nullable KType get() {
            return this.resolved;
        }

        public void collapseToObject() {
            if (!isResolved()) {
                this.resolved = KType.ROOT;
            }
        }

        /**
         * Make sure to call this before calling {@link #tryResolve}. Otherwise there might be cycles.
         */
        public boolean canResolve(IntoContext c, Region checkingRegion, KType resolved, TypeChecking typeChecking, boolean mutable) {
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

            for (var constraint : this.constraints) {
                if (!typeChecking.canAssignInner(c.intoContext(), checkingRegion, constraint, resolved, false)) {
                    return false;
                }
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
            Log.error(c, new AttribError.TypeCycle(checkingRegion, msg, graph));
            throw new Log.KarinaException();
        }

        /**
         * Check {@link #canResolve} before calling this method.
         * This does not guarantee that the type will be resolved,
         * since the Type might be checked against itself.
         * Checking a resolvable against itself will return true.
         * Test with {@link #isResolved} to be sure if it was resolved after calling this method.
         */
        public void tryResolve(IntoContext c, Region region, KType resolved) {
            resolved = resolved.unpack();

            if (this.resolved != null) {
                Log.temp(c, region, "Type already resolved");
                throw new Log.KarinaException();
            }

            if (resolved != this) {
                this.resolved = resolved;
            }

        }

        @Override
        public String toString() {
            return toString(new java.util.IdentityHashMap<>());
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();
            if (this.resolved != null) {
                return this.resolved.equals(object);
            }
            return this == object;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.canUsePrimitives, this.canUseVoid, this.resolved);
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            if (dejavu.containsKey(this)) {
                return "?...";
            }

            var code = this.hashCode() & 0xFFFF;
            var readable = Integer.toHexString(code).toUpperCase();
            if (this.resolved == null) {

                return "?" + readable;
            } else {
                dejavu.put(this, this);
                var string = "?" + readable + " as " + this.resolved.toString(dejavu);
                dejavu.remove(this);
                return string;
            }
        }
    }


    record PrimitiveType(KPrimitive primitive) implements KType {

        @Override
        public String toString() {
            return this.primitive().toString().toLowerCase();
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof KType type) object = type.unpack();

            return object instanceof PrimitiveType(var primitive1) && this.primitive == primitive1;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.primitive);
        }

        @Override
        public String toString(Map<KType, Object> dejavu) {
            return toString();
        }
    }


}
