package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.lang.reflect.Modifier;
import java.util.*;

public class ClosureHelper {


    //TODO make better
    // test is the interface extends another interface that defines functions
    // than test if the functions are already implemented in the given argument
    // allow auto conversion in any way??


    /**
     * checks if a given class type is a functional interface and if it can be used to implement a
     * function given the parameter and return types.
     * @param toCheck the class to check
     * @return true if the class is a functional interface and can be used to implement a function
     */
    public static boolean canUseInterface(Region region, Context c, Model model, List<KType> types, KType returnType, KType.ClassType toCheck) {
        var paramsFromType = getParameterTypesFromInterface(c, model, types.size(), toCheck);
        if (paramsFromType == null || paramsFromType.returnType() == null) {
            return false;
        }
        var mappedParameters = paramsFromType.params();
        var methodReturnType = paramsFromType.returnType();

        for (var i = 0; i < mappedParameters.size(); i++) {
            var mappedParam = mappedParameters.get(i);
            var type = types.get(i);

            //TODO what equals or canAssign method should be used here?
            if (!mappedParam.equals(type)) {
                Log.recordType(Log.LogTypes.CLOSURE, "invalid parameter " + i, mappedParam, "from", type);
                return false;
            }
        }

        var returnMatch = returnType.equals(methodReturnType);
        Log.recordType(Log.LogTypes.CLOSURE, "return type ", returnMatch, returnType, "from", methodReturnType);
        return returnMatch;
    }

    /**
     * checks if a given class type is a functional interface
     * @return Null if the class is not a functional interface or if it has no single abstract methods.
     *         otherwise the parameters and return type of the abstract method for the functional interface
     *         (including generic projection).
     *
     */
    private static @Nullable ParamsAndReturn getParameterTypesFromInterface(Context c, Model model, int argsSize, KType.ClassType toCheck) {
        Log.recordType(Log.LogTypes.CLOSURE, toCheck.toString());
        var classModel = model.getClass(toCheck.pointer());
        if (!Modifier.isAbstract(classModel.modifiers())) {
            Log.recordType(Log.LogTypes.CLOSURE, "Not a interface");
            return null;
        } else if (Modifier.isFinal(classModel.modifiers())) {
            Log.recordType(Log.LogTypes.CLOSURE, "interface is final");
            return null;
        }
        var method = MethodHelper.getMethodsToImplementForClass(c, model, toCheck);

        if (method.size() > 1) {
            return null;
        }
        if (method.isEmpty()) {
            Log.recordType(Log.LogTypes.CLOSURE, "No method found");
            return null;
        }
        var methodToImplement = method.getFirst();

        //dont have to check, they should be always public
        //ctx.protection().canReference(ctx.owningClass(), pointer.classPointer(), methodModel.modifiers());

        if (methodToImplement.argumentTypes().length != argsSize) {
            Log.recordType(Log.LogTypes.CLOSURE, "invalid parameter count");
            return null;
        }
        var parameters = Arrays.stream(methodToImplement.argumentTypes()).toList();
        var methodReturnType = methodToImplement.returnType();

        return new ParamsAndReturn(parameters, methodReturnType);
    }


    /**
     * Constructs a new set of arguments with a potential hint, otherwise use the default arguments.
     * When a parameter type is already present, it will be used, otherwise the type of the hint will be used
     * When no valid hint is present, all non annotated arguments will be of type Resolvable.
     * @return The parameters and return type of the closure.
     */
    public static ArgsAndReturnType getClosureTypesFromHint(AttributionContext ctx, @Nullable KType hint, KExpr.Closure expr) {
        if (!expr.interfaces().isEmpty()) {
            hint = expr.interfaces().getFirst();
        }

        if (hint instanceof KType.FunctionType functionHint) {
            if (functionHint.arguments().size() == expr.args().size()) {
                var args = new ParamsAndReturn(functionHint.arguments(), functionHint.returnType());
                return getClosureTypes(expr, args);
            }
        } else if (hint instanceof KType.ClassType classType) {
            var paramsFromType = getParameterTypesFromInterface(ctx.intoContext(), ctx.model(), expr.args().size(), classType);
            if (paramsFromType != null) {
                return getClosureTypes(expr, paramsFromType);
            }
        }

        return defaultClosureTypes(expr);

    }


    public static @Nullable KType.ClassType getDefaultInterface(Context c, Region region, Model model, List<KType> args, KType returnType) {
        var interfaceToUse = DefaultClosureTable.getType(
                args.stream().map(KType::unpack).toList(),
                returnType.unpack()
        );
        if (interfaceToUse != null) {
            var classModel = model.getClassNullable(interfaceToUse.pointer());
            if (classModel == null) {
                Log.temp(c, region, "Default interface " + interfaceToUse + " not found in model, this should not happen");
                throw new Log.KarinaException();
            } else {
                if (interfaceToUse.generics().size() != classModel.generics().size()) {
                    Log.temp(c, region,
                            "Default interface " + interfaceToUse + " has wrong number of generics, this should not happen ("
                            + interfaceToUse.generics().size() + " != " + classModel.generics().size() + ")"
                    );
                    throw new Log.KarinaException();
                }
            }
        }
        return interfaceToUse;
    }

    public static boolean isInterfaceAlreadyAdded(KType.ClassType classType, List<KType.ClassType> interfaces) {
        return interfaces.stream().anyMatch(ref -> ref.pointer().equals(classType.pointer()));
    }


    /**
     * Returns the default arguments types and return type for a closure
     * All non annotated arguments will be of type Resolvable, including the return type
     */
    private static ArgsAndReturnType defaultClosureTypes(KExpr.Closure expr) {
        var newArgs = new ArrayList<NameAndOptType>();
        for (var arg : expr.args()) {
            var type = arg.type();
            if (type == null) {
                type = KType.Resolvable.newInstance();
            }
            var variable = new Variable(
                    arg.region(),
                    arg.name().value(),
                    type,
                    false,
                    true
            );

            newArgs.add(new NameAndOptType(
                    arg.region(),
                    arg.name(),
                    type,
                    variable
            ));
        }

        var returnType = expr.returnType();
        if (returnType == null) {
            returnType = KType.Resolvable.newInstanceAllowVoid();
        }

        return new ArgsAndReturnType(newArgs, returnType);
    }

    /**
     * Use the given type for each parameter when given, otherwise use the types from 'fromHint'
     * @return The parameters and return type of the closure.
     */
    private static ArgsAndReturnType getClosureTypes(KExpr.Closure expr, ParamsAndReturn fromHint) {
        var newArgs = new ArrayList<NameAndOptType>();
        for (var i = 0; i < fromHint.params().size(); i++) {
            var suggestedType = fromHint.params().get(i);
            var nameAndOptType = expr.args().get(i);
            var foundType = nameAndOptType.type();
            if (foundType == null) {
                foundType = suggestedType;
            }

            var variable = new Variable(
                    nameAndOptType.region(),
                    nameAndOptType.name().value(),
                    foundType,
                    false,
                    true
            );

            newArgs.add(new NameAndOptType(
                    nameAndOptType.region(),
                    nameAndOptType.name(),
                    foundType,
                    variable
            ));
        }
        var returnType = expr.returnType();
        if (returnType == null) {
            returnType = fromHint.returnType();
        }
        if (returnType == null) {
            returnType = KType.NONE;
        }

        return new ArgsAndReturnType(newArgs, returnType);
    }


    public static MethodHelper.MethodToImplement getMethodToImplement(Context c, Region region, Model model, KType.ClassType classType) {
        var method = MethodHelper.getMethodsToImplementForClass(c, model, classType);
        if (method.isEmpty()) {
            Log.temp(c, region, "Closure class has no method to implement, this should not happen");
            throw new Log.KarinaException();
        } else if (method.size() > 1) {
            Log.temp(c, region, "Closure class has more than one method to implement, this should not happen");
            throw new Log.KarinaException();
        }
        return method.getFirst();
    }

    public record ParamsAndReturn(List<KType> params, @Nullable KType returnType) {}
    public record ArgsAndReturnType(List<NameAndOptType> args, KType returnType) {}

    /**
     * Entry for a function. Each type can either be a primitive, void or KType.ROOT (when a generic is used).
     * @param args
     * @param returnType
     */
    private record FunctionClosureType(List<KType> args, KType returnType) { }

    /**
     * To get a class (given a list of arguments and a return type), that can be used as a closure.
     */
    private static class DefaultClosureTable {

        /* (...) -> void */
        private static final ObjectPath RUNNABLE = ObjectPath.fromJavaPath("java/lang/Runnable");
        private static final ObjectPath CONSUMER = ObjectPath.fromJavaPath("java/util/function/Consumer");
        private static final ObjectPath DOUBLE_CONSUMER = ObjectPath.fromJavaPath("java/util/function/DoubleConsumer");
        private static final ObjectPath INT_CONSUMER = ObjectPath.fromJavaPath("java/util/function/IntConsumer");
        private static final ObjectPath LONG_CONSUMER = ObjectPath.fromJavaPath("java/util/function/LongConsumer");
        private static final ObjectPath BI_CONSUMER = ObjectPath.fromJavaPath("java/util/function/BiConsumer");
        private static final ObjectPath OBJ_DOUBLE_CONSUMER = ObjectPath.fromJavaPath("java/util/function/ObjDoubleConsumer");
        private static final ObjectPath OBJ_INT_CONSUMER = ObjectPath.fromJavaPath("java/util/function/ObjIntConsumer");
        private static final ObjectPath OBJ_LONG_CONSUMER = ObjectPath.fromJavaPath("java/util/function/ObjLongConsumer");

        /* (...) -> object */
        private static final ObjectPath SUPPLIER = ObjectPath.fromJavaPath("java/util/function/Supplier");
        private static final ObjectPath FUNCTION = ObjectPath.fromJavaPath("java/util/function/Function");
        private static final ObjectPath DOUBLE_FUNCTION = ObjectPath.fromJavaPath("java/util/function/DoubleFunction");
        private static final ObjectPath INT_FUNCTION = ObjectPath.fromJavaPath("java/util/function/IntFunction");
        private static final ObjectPath LONG_FUNCTION = ObjectPath.fromJavaPath("java/util/function/LongFunction");
        private static final ObjectPath BI_FUNCTION = ObjectPath.fromJavaPath("java/util/function/BiFunction");
        private static final ObjectPath BINARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/BinaryOperator");
        private static final ObjectPath UNARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/UnaryOperator");

        /* (...) -> boolean */
        private static final ObjectPath BOOLEAN_SUPPLIER = ObjectPath.fromJavaPath("java/util/function/BooleanSupplier");
        private static final ObjectPath PREDICATE = ObjectPath.fromJavaPath("java/util/function/Predicate");
        private static final ObjectPath BI_PREDICATE = ObjectPath.fromJavaPath("java/util/function/BiPredicate");
        private static final ObjectPath DOUBLE_PREDICATE = ObjectPath.fromJavaPath("java/util/function/DoublePredicate");
        private static final ObjectPath INT_PREDICATE = ObjectPath.fromJavaPath("java/util/function/IntPredicate");
        private static final ObjectPath LONG_PREDICATE = ObjectPath.fromJavaPath("java/util/function/LongPredicate");

        /* (...) -> int */
        private static final ObjectPath TO_INT_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToIntFunction");
        private static final ObjectPath TO_INT_BI_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToIntBiFunction");
        private static final ObjectPath DOUBLE_TO_INT_FUNCTION = ObjectPath.fromJavaPath("java/util/function/DoubleToIntFunction");
        private static final ObjectPath INT_UNARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/IntUnaryOperator");
        private static final ObjectPath LONG_TO_INT_FUNCTION = ObjectPath.fromJavaPath("java/util/function/LongToIntFunction");
        private static final ObjectPath INT_SUPPLIER = ObjectPath.fromJavaPath("java/util/function/IntSupplier");
        private static final ObjectPath INT_BINARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/IntBinaryOperator");

        /* (...) -> long */
        private static final ObjectPath TO_LONG_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToLongFunction");
        private static final ObjectPath TO_LONG_BI_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToLongBiFunction");
        private static final ObjectPath DOUBLE_TO_LONG_FUNCTION = ObjectPath.fromJavaPath("java/util/function/DoubleToLongFunction");
        private static final ObjectPath INT_TO_LONG_FUNCTION = ObjectPath.fromJavaPath("java/util/function/IntToLongFunction");
        private static final ObjectPath LONG_UNARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/LongUnaryOperator");
        private static final ObjectPath LONG_SUPPLIER = ObjectPath.fromJavaPath("java/util/function/LongSupplier");
        private static final ObjectPath LONG_BINARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/LongBinaryOperator");

        /* (...) -> double */
        private static final ObjectPath TO_DOUBLE_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToDoubleFunction");
        private static final ObjectPath TO_DOUBLE_BI_FUNCTION = ObjectPath.fromJavaPath("java/util/function/ToDoubleBiFunction");
        private static final ObjectPath DOUBLE_UNARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/DoubleUnaryOperator");
        private static final ObjectPath INT_TO_DOUBLE_FUNCTION = ObjectPath.fromJavaPath("java/util/function/IntToDoubleFunction");
        private static final ObjectPath LONG_TO_DOUBLE_FUNCTION = ObjectPath.fromJavaPath("java/util/function/LongToDoubleFunction");
        private static final ObjectPath DOUBLE_SUPPLIER = ObjectPath.fromJavaPath("java/util/function/DoubleSupplier");
        private static final ObjectPath DOUBLE_BINARY_OPERATOR = ObjectPath.fromJavaPath("java/util/function/DoubleBinaryOperator");

        private static @Nullable KType.ClassType getType(List<KType> args, KType returnType) {
            return switch (returnType) {
                case KType.VoidType _ -> getConsumer(args);
                case KType w when isObject(w) -> getFunctions(args, returnType);
                case KType.PrimitiveType(var p) when p == KType.KPrimitive.BOOL -> getSupplier(args);
                case KType.PrimitiveType(var p) when p == KType.KPrimitive.INT -> getToIntFunction(args);
                case KType.PrimitiveType(var p) when p == KType.KPrimitive.LONG -> getToLongFunction(args);
                case KType.PrimitiveType(var p) when p == KType.KPrimitive.DOUBLE -> getToDoubleFunction(args);
                default -> null;
            };

        }

        //<editor-fold desc="(...) -> double" collapsed="true">

        /// `(...) -> double`
        ///
        /// | Interface                  | Signature                 |
        /// | --------------------       | ------------              |
        /// | `ToDoubleFunction<T>`      | T → double                |
        /// | `ToDoubleBiFunction<T, U>` | (T, U) → double           |
        /// | `DoubleUnaryOperator`      | double → double           |
        /// | `IntToDoubleFunction`      | int → double              |
        /// | `LongToDoubleFunction`     | long → double             |
        /// | `DoubleSupplier`           | () → double               |
        /// | `DoubleBinaryOperator`     | (double, double) → double |
        private static @Nullable KType.ClassType getToDoubleFunction(List<KType> args) {
            return getPrimitiveFunction(
                    args,
                    TO_DOUBLE_FUNCTION,
                    TO_DOUBLE_BI_FUNCTION,
                    DOUBLE_UNARY_OPERATOR,
                    INT_TO_DOUBLE_FUNCTION,
                    LONG_TO_DOUBLE_FUNCTION,
                    DOUBLE_SUPPLIER,
                    KType.KPrimitive.DOUBLE,
                    DOUBLE_BINARY_OPERATOR
            );
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> long" collapsed="true">

        /// `(...) -> long`
        ///
        /// | Interface                | Signature           |
        /// | --------------------     | ------------        |
        /// | `ToLongFunction<T>`      | T → long          |
        /// | `ToLongBiFunction<T, U>` | (T, U) → long       |
        /// | `DoubleToLongFunction`   | double → long       |
        /// | `IntToLongFunction`      | int → long          |
        /// | `LongUnaryOperator`      | long → long         |
        /// | `LongSupplier`           | () → long           |
        /// | `LongBinaryOperator`     | (long, long) → long |
        private static @Nullable KType.ClassType getToLongFunction(List<KType> args) {
            return getPrimitiveFunction(
                    args,
                    TO_LONG_FUNCTION,
                    TO_LONG_BI_FUNCTION,
                    DOUBLE_TO_LONG_FUNCTION,
                    INT_TO_LONG_FUNCTION,
                    LONG_UNARY_OPERATOR,
                    LONG_SUPPLIER,
                    KType.KPrimitive.LONG,
                    LONG_BINARY_OPERATOR
            );
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> int" collapsed="true">

        /// `(...) -> int`
        ///
        /// | Interface               | Signature          |
        /// | --------------------    | ------------       |
        /// | `ToIntFunction<T>`        | T → int          |
        /// | `ToIntBiFunction<T, U>` | (T, U) → int       |
        /// | `DoubleToIntFunction`   | double → int       |
        /// | `IntUnaryOperator`      | int → int          |
        /// | `LongToIntFunction`     | long → int         |
        /// | `IntSupplier`           | () → int           |
        /// | `IntBinaryOperator`     | (int, int) → int   |
        private static @Nullable KType.ClassType getToIntFunction(List<KType> args) {
            return getPrimitiveFunction(
                    args,
                    TO_INT_FUNCTION,
                    TO_INT_BI_FUNCTION,
                    DOUBLE_TO_INT_FUNCTION,
                    INT_UNARY_OPERATOR,
                    LONG_TO_INT_FUNCTION,
                    INT_SUPPLIER,
                    KType.KPrimitive.INT,
                    INT_BINARY_OPERATOR
            );
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> boolean" collapsed="true">

        /// `(...) -> boolean`
        ///
        /// | Interface              | Signature          |
        /// | --------------------   | ------------       |
        /// | `Predicate<T>`         | T → boolean        |
        /// | `BiPredicate<T, U>`    | (T, U) → boolean   |
        /// | `DoublePredicate`      | double → boolean   |
        /// | `IntPredicate`         | int → boolean      |
        /// | `LongPredicate`        | long → boolean     |
        /// | `BooleanSupplier`      | () → boolean       |
        private static @Nullable KType.ClassType getSupplier(List<KType> args) {
            return getPrimitiveFunction(
                    args,
                    PREDICATE,
                    BI_PREDICATE,
                    DOUBLE_PREDICATE,
                    INT_PREDICATE,
                    LONG_PREDICATE,
                    BOOLEAN_SUPPLIER,
                    KType.KPrimitive.BOOL,
                    null // no binary operator for boolean
            );
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> Object" collapsed="true">

        /// `(...) -> Object`
        ///
        /// | Interface              | Signature          |
        /// | --------------------   | ------------       |
        /// | `BiFunction<T, U, R>`  | (T, U) → R         |
        /// | `BinaryOperator<T>`    | (T, T) → T         |
        /// | `DoubleFunction<R>`    | double → R         |
        /// | `Function<T, R>`       | T → R              |
        /// | `IntFunction<R>`       | int → R            |
        /// | `LongFunction<R>`      | long → R           |
        /// | `Supplier<T>`          | () → T             |
        /// | `UnaryOperator<T>`     | T → T              |
        private static @Nullable KType.ClassType getFunctions(List<KType> args, KType returnType) {

            if (args.isEmpty()) {
                return mkClassType(SUPPLIER, Collections.singletonList(returnType));
            }
            var first = args.getFirst();
            if (args.size() == 1) {
                return switch (first) {
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.DOUBLE ->
                            mkClassType(DOUBLE_FUNCTION, Collections.singletonList(returnType));
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.INT ->
                            mkClassType(INT_FUNCTION, Collections.singletonList(returnType));
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.LONG ->
                            mkClassType(LONG_FUNCTION, Collections.singletonList(returnType));
                    case KType w when isObject(w) -> {
                        if (first.equals(returnType)) {
                            yield mkClassType(UNARY_OPERATOR, Collections.singletonList(first));
                        } else {
                            yield mkClassType(FUNCTION, List.of(first, returnType));
                        }
                    }
                    default -> null;
                };
            } else if (args.size() == 2) {
                var second = args.get(1);
                if (!isObject(first) || !isObject(second)) {
                    return null;
                }
                if (first.equals(second) && first.equals(returnType)) {
                    return mkClassType(BINARY_OPERATOR, List.of(first));
                } else {
                    return mkClassType(BI_FUNCTION, List.of(first, second, returnType));
                }
            } else {
                return null;
            }
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> void" collapsed="true">

        /// `(...) -> void`
        ///
        /// | Interface              | Signature          |
        /// | --------------------   | ------------       |
        /// | `Runnable`             | () → void          |
        /// | `Consumer<T>`          | T → void           |
        /// | `DoubleConsumer`       | double → void      |
        /// | `IntConsumer`          | int → void         |
        /// | `LongConsumer`         | long → void        |
        /// | `BiConsumer<T, U>`     | (T, U) → void      |
        /// | `ObjDoubleConsumer<T>` | (T, double) → void |
        /// | `ObjIntConsumer<T>`    | (T, int) → void    |
        /// | `ObjLongConsumer<T>`   | (T, long) → void   |
        private static @Nullable KType.ClassType getConsumer(List<KType> args) {

            if (args.isEmpty()) {
                return mkClassType(RUNNABLE, Collections.emptyList());
            }
            var first = args.getFirst();
            if (args.size() == 1) {
                return switch (first) {
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.DOUBLE ->
                            mkClassType(DOUBLE_CONSUMER, Collections.emptyList());
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.INT ->
                            mkClassType(INT_CONSUMER, Collections.emptyList());
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.LONG ->
                            mkClassType(LONG_CONSUMER, Collections.emptyList());
                    case KType w when isObject(w) ->
                            mkClassType(CONSUMER, Collections.singletonList(first));
                    default -> null;
                };
            } else if (args.size() == 2) {
                var second = args.get(1);
                if (!isObject(first)) {
                    return null;
                }
                return switch (second) {
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.DOUBLE ->
                            mkClassType(OBJ_DOUBLE_CONSUMER, Collections.singletonList(first));
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.INT ->
                            mkClassType(OBJ_INT_CONSUMER, Collections.singletonList(first));
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.LONG ->
                            mkClassType(OBJ_LONG_CONSUMER, Collections.singletonList(first));
                    case KType w when isObject(w) ->
                            mkClassType(BI_CONSUMER, List.of(first, second));
                    default -> null;
                };

            } else {
                return null;
            }
        }

        //</editor-fold>

        //<editor-fold desc="(...) -> primitive" collapsed="true">

        /// Helper for `(...) -> primitive`
        ///
        /// Ordering:
        ///
        /// | Interface                     | Signature                          |
        /// | --------------------          | ------------                       |
        /// | `PrimitiveFunction<T>`        | T → primitive                      |
        /// | `ToPrimitiveBiFunction<T, U>` | (T, U) → primitive                 |
        /// | `DoubleToPrimitiveFunction`   | double → primitive                 |
        /// | `IntToPrimitiveFunction`      | int → primitive                    |
        /// | `LongToPrimitiveFunction`     | long → primitive                   |
        /// | `PrimitiveSupplier`           | () → primitive                     |
        /// | `PrimitiveBinaryFunction`     | (primitive, primitive) → primitive |
        ///
        /// `PrimitiveBinaryFunction` can be null
        ///
        private static @Nullable KType.ClassType getPrimitiveFunction(
                List<KType> args, ObjectPath ObjectArg, ObjectPath ObjectObjectArg,
                ObjectPath doubleArg, ObjectPath IntArg, ObjectPath LongArg, ObjectPath noArgs,
                KType.KPrimitive primitive, @Nullable ObjectPath binaryOperator
        ) {
            if (args.isEmpty()) {
                return mkClassType(noArgs, Collections.emptyList());
            }
            var first = args.getFirst();
            if (args.size() == 1) {
                return switch (first) {
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.DOUBLE ->
                            mkClassType(doubleArg, Collections.emptyList());
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.INT ->
                            mkClassType(IntArg, Collections.emptyList());
                    case KType.PrimitiveType(var p) when p == KType.KPrimitive.LONG ->
                            mkClassType(LongArg, Collections.emptyList());
                    case KType w when isObject(w) ->
                            mkClassType(ObjectArg, Collections.singletonList(first));
                    default -> null;
                };
            } else if (args.size() == 2) {
                var second = args.get(1);
                if (first instanceof KType.PrimitiveType(var p) && second instanceof KType.PrimitiveType(var p2) && p == primitive && p2 == primitive) {
                    if (binaryOperator != null) {
                        return mkClassType(binaryOperator, Collections.emptyList());
                    } else {
                        return null;
                    }
                } else if (!isObject(first) || !isObject(second)) {
                    return null;
                }
                return mkClassType(ObjectObjectArg, List.of(first, second));
            } else {
                return null;
            }
        }

        //</editor-fold>

        private static boolean isObject(KType type) {
            return Types.hasIdentity(type);
        }

        private static KType.ClassType mkClassType(ObjectPath path, List<KType> generics) {
            return ClassPointer.of(KType.JAVA_LIB, path).implement(generics);
        }

    }
}
