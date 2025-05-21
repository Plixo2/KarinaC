package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public static boolean canUseInterface(Region region, AttributionContext ctx, List<KType> types, KType returnType, KType.ClassType toCheck) {
        var paramsFromType = getParameterTypesFromInterface(ctx, types.size(), toCheck);
        if (paramsFromType == null || paramsFromType.returnType() == null) {
            return false;
        }
        var mappedParameters = paramsFromType.params();
        var methodReturnType = paramsFromType.returnType();

        for (var i = 0; i < mappedParameters.size(); i++) {
            var mappedParam = mappedParameters.get(i);
            var type = types.get(i);

            if (!ctx.checking().canAssign(region, mappedParam, type, true)) {
                Log.recordType(Log.LogTypes.CLOSURE, "invalid parameter " + i,mappedParam, "from", type);
                return false;
            }
        }

        var returnMatch = ctx.checking().canAssign(region, returnType, methodReturnType, true);
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
    private static @Nullable ParamsAndReturn getParameterTypesFromInterface(AttributionContext ctx, int argsSize, KType.ClassType toCheck) {
        Log.recordType(Log.LogTypes.CLOSURE, toCheck.toString());
        var classModel = ctx.model().getClass(toCheck.pointer());
        if (!Modifier.isAbstract(classModel.modifiers())) { //TODO replace with isAbstract check?
            Log.recordType(Log.LogTypes.CLOSURE, "Not a interface");
            return null;
        } else if (Modifier.isFinal(classModel.modifiers())) {
            Log.recordType(Log.LogTypes.CLOSURE, "interface is final");
            return null;
        }

        var method = MethodHelper.getMethodsToImplementForClass(ctx.model(), toCheck);

        if (method.size() > 1) {
            return null;
        }
        if (method.isEmpty()) {
            Log.recordType(Log.LogTypes.CLOSURE, "No method found");
            return null;
        }
        var methodToImplement = method.getFirst();

//        MethodPointer pointer = null;
//        MethodModel methodModel = null;
//        for (var method : classModel.methods()) {
//            if (!Modifier.isAbstract(method.modifiers())) {
//                continue;
//            }
//            if (pointer == null) {
//                pointer = method.pointer();
//                methodModel = method;
//            } else {
//                return null;
//            }
//        }
//        if (pointer == null) {
//            Log.recordType(Log.LogTypes.CLOSURE, "No method found");
//            return null;
//        }

        //dont have to check, they should be always public
        //ctx.protection().canReference(ctx.owningClass(), pointer.classPointer(), methodModel.modifiers());

        if (methodToImplement.argumentTypes().length != argsSize) {
            Log.recordType(Log.LogTypes.CLOSURE, "invalid parameter count");
            return null;
        }
        var parameters = Arrays.stream(methodToImplement.argumentTypes()).toList();
        var methodReturnType = methodToImplement.returnType();

        return new ParamsAndReturn(parameters, methodReturnType);


//        var mapped = new HashMap<Generic, KType>();
//        for (var i = 0; i < classModel.generics().size(); i++) {
//            var generic = classModel.generics().get(i);
//            var type = toCheck.generics().get(i);
//            mapped.put(generic, type);
//        }
//        var methodGenerics = methodModel.generics();
//        for (var i = 0; i < methodGenerics.size(); i++) {
//            var generic = methodGenerics.get(i);
//            var type = new KType.Resolvable();
//            mapped.put(generic, type);
//        }
//        for (var genericKTypeEntry : mapped.entrySet()) {
//            Log.recordType(Log.LogTypes.CLOSURE, "generic",
//                    genericKTypeEntry.getKey(),
//                    " -> ",
//                    genericKTypeEntry.getValue()
//            );
//        }
//        var methodReturnType = Types.projectGenerics(methodModel.signature().returnType(), mapped);
//
//        var unmappedParameters = methodModel.signature().parameters();
//        var mappedParameters = unmappedParameters.stream().map(ref ->
//                Types.projectGenerics(ref, mapped)
//        ).toList();
//        return new ParamsAndReturn(mappedParameters, methodReturnType);

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
            var paramsFromType = getParameterTypesFromInterface(ctx, expr.args().size(), classType);
            if (paramsFromType != null) {
                return getClosureTypes(expr, paramsFromType);
            }
        }

        return defaultClosureTypes(expr);


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
                type = new KType.Resolvable();
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
            returnType = new KType.Resolvable(false, true);
        }

        return new ArgsAndReturnType(newArgs, returnType);
    }

    /**
     * Use the given type for each parameter when given, otherwise use the types from 'fromHint'
     * @return The parameters and return type of the closure.
     */
    private static ArgsAndReturnType getClosureTypes(KExpr.Closure expr, ClosureHelper.ParamsAndReturn fromHint) {
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
    


    public record ParamsAndReturn(List<KType> params, @Nullable KType returnType) {}
    public record ArgsAndReturnType(List<NameAndOptType> args, KType returnType) {}

}
