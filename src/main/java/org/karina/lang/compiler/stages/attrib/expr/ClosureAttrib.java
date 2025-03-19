package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.ClosureSymbol;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ClosureAttrib  {
    public static AttributionExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) {

        var logName = "Attributing closure";
        Log.beginType(Log.LogTypes.CLOSURE, logName);
        var owningMethod = ctx.owningMethod();
        Log.recordType(Log.LogTypes.CLOSURE, "Closure", expr.region(), "in", ctx.owningClass(), "/", owningMethod != null ? owningMethod.name() : null);

        for (var arg : expr.args()) {
            checkForPreexistingVariable(ctx, arg.name());
        }


        var argsAndReturnType = getNewArgs(ctx, hint, expr);

        var newArgs = argsAndReturnType.args();
        var returnType = argsAndReturnType.returnType();

        var bodyContext = new AttributionContext(
                ctx.model(),
                ctx.selfVariable(),
                false,
                null, //we get rid of the owning method here
                ctx.owningClass(), //make sure to keep this when compiling the synthetic class
                returnType,
                ctx.variables(),
                ctx.table(),
                ctx.checking(),
                ctx.protection()
        );

        //make all variables from the outside immutable in the body
        for (var variable : ctx.variables()) {
            bodyContext = bodyContext.markImmutable(variable);
        }

        for (var newArg : newArgs) {
            assert newArg.symbol() != null;
            if (newArg.type() != null && newArg.type().isVoid()) {
                Log.attribError(new AttribError.NotSupportedType(newArg.region(), newArg.type()));
                throw new Log.KarinaException();
            }
            if (newArg.symbol().name().equals("_")) {
                continue;
            }
            bodyContext = bodyContext.addVariable(newArg.symbol());
        }

        var usageSelf = ctx.selfVariable() != null ? ctx.selfVariable().usageCount() : 0;
        var usageMap = new HashMap<Variable, Integer>();
        for (var variable : ctx.variables()) {
            usageMap.put(variable, variable.usageCount());
        }
        Log.recordType(Log.LogTypes.CLOSURE, "parameter types pre body",
                argsAndReturnType.args.stream().map(NameAndOptType::type).toList()
        );
        var newBody = attribExpr(returnType, bodyContext, expr.body()).expr();
        Log.recordType(Log.LogTypes.CLOSURE, "parameter types post body",
                argsAndReturnType.args.stream().map(NameAndOptType::type).toList()
        );

        var usageSelfPost = ctx.selfVariable() != null ? ctx.selfVariable().usageCount() : 0;
        var captureSelf = usageSelfPost > usageSelf;

        var captures = new ArrayList<Variable>();
        for (var variable : ctx.variables()) {
            var usageCount = variable.usageCount();
            if (usageMap.get(variable) != usageCount) {
                captures.add(variable);
            }
        }

        if (!newBody.doesReturn() && !returnType.isVoid()) {
            newBody = ctx.makeAssignment(newBody.region(), returnType, newBody);
        }
        var args = newArgs.stream().map(NameAndOptType::type).toList();

        var interfaces = new ArrayList<KType.ClassType>();

        for (var anInterface : expr.interfaces()) {
            Log.beginType(Log.LogTypes.CLOSURE, "Checking annotated interface");
            if (!(anInterface instanceof KType.ClassType classType)) {
                Log.record("Impl is not a class");
                Log.attribError(new AttribError.NotAClass(expr.region(), anInterface));
                throw new Log.KarinaException();
            }
            var model = ctx.model().getClass(classType.pointer());
            if (!Modifier.isInterface(model.modifiers())) {
                Log.record("Impl is not a interface");
                Log.attribError(new AttribError.NotAInterface(expr.region(), classType));
                throw new Log.KarinaException();
            }
            if (!canUseInterface(expr.region(), ctx, args, returnType, classType)) {
                Log.record("Impl is not a matching interface");
                Log.attribError(new AttribError.NotAValidInterface(expr.region(), args, returnType, classType));
                throw new Log.KarinaException();
            }
            Log.endType(Log.LogTypes.CLOSURE, "Checking annotated interface");

            //TODO check if interface is a supertype or subType of a already added interface
            var alreadyAdded = interfaces.stream().anyMatch(ref -> ref.pointer().equals(classType.pointer()));
            if (alreadyAdded) {
                Log.record("Interface already added");
                Log.attribError(new AttribError.DuplicateInterface(expr.region(), classType));
                throw new Log.KarinaException();
            }

            interfaces.add(classType);
        }

        Log.recordType(Log.LogTypes.CLOSURE, "with hint", hint);
        if (hint instanceof KType.ClassType classType) {
            Log.beginType(Log.LogTypes.CLOSURE, "Checking hint interface");
            //TODO check if interface is a supertype or subType of a already added interface
            var alreadyAdded = interfaces.stream().anyMatch(ref -> ref.pointer().equals(classType.pointer()));
            if (alreadyAdded) {
                Log.recordType(Log.LogTypes.CLOSURE, "hint interface already added");
            } else  if (canUseInterface(expr.region(), ctx, args, returnType, classType)) {
                Log.recordType(Log.LogTypes.CLOSURE, "Using hint as interface");
                interfaces.add(classType);
            }
            Log.endType(Log.LogTypes.CLOSURE, "Checking hint interface");
        }

        var functionType = new KType.FunctionType(
                args,
                returnType,
                interfaces
        );
        Log.recordType(Log.LogTypes.CLOSURE, "Function type", functionType);

        var variables = newArgs.stream().map(NameAndOptType::symbol).toList();
        var symbol = new ClosureSymbol(functionType, captures, captureSelf, ctx.selfVariable(), variables);

        Log.endType(Log.LogTypes.CLOSURE, logName);

        return of(ctx, new KExpr.Closure(
                expr.region(),
                newArgs,
                returnType,
                interfaces,
                newBody,
                symbol
        ));

    }

    /**
     * Constructs a new set of arguments with a potential hint, otherwise use the default arguments. 
     * When a parameter type is already present, it will be used, otherwise the type of the hint will be used
     * When no valid hint is present, all non annotated arguments will be of type Resolvable
     */
    private static ArgsAndReturnType getNewArgs(AttributionContext ctx, @Nullable KType hint, KExpr.Closure expr) {
        /*if (hint == null) {
            if (!expr.interfaces().isEmpty()) {
                hint = expr.interfaces().getFirst();
            }
        } else if (hint instanceof KType.ClassType classType) {

        }*/
        if (!expr.interfaces().isEmpty()) {
            hint = expr.interfaces().getFirst();
        }

        if (hint instanceof KType.FunctionType functionHint) {
            if (functionHint.arguments().size() == expr.args().size()) {
                var args = new ParamsAndReturn(functionHint.arguments(), functionHint.returnType());
                return getFromTypes(expr, args);
            }
        } else if (hint instanceof KType.ClassType classType) {
            var paramsFromType = getParamsFromType(ctx, expr.args().size(), classType);
            if (paramsFromType != null) {
                return getFromTypes(expr, paramsFromType);
            }
        }

        return defaultArgs(expr);


    }

    /**
     * Use the given type for each parameter when given, otherwise use the types from 'fromHint'
     */
    private static ArgsAndReturnType getFromTypes(KExpr.Closure expr, ParamsAndReturn fromHint) {
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
            returnType = KType.VOID;
        }

        return new ArgsAndReturnType(newArgs, returnType);
    }

    /**
     * Returns the default arguments types and return type for a closure
     * All non annotated arguments will be of type Resolvable, including the return type
     */
    private static ArgsAndReturnType defaultArgs(KExpr.Closure expr) {
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


    private static void checkForPreexistingVariable(AttributionContext ctx, RegionOf<String> argument) {
        var name = argument.value();
        if(ctx.variables().contains(name)) {
            Log.attribError(new AttribError.DuplicateVariable(
                    Objects.requireNonNull(ctx.variables().get(name)).region(),
                    argument.region(), name
            ));
            throw new Log.KarinaException();
        }
    }



    //TODO make better
    // test is the interface extends another interface that defines functions
    // than test if the functions are already implemented in the given argument
    // allow auto conversion in any way??
    //TODO construct bridge methods when compiling the synthetic class
    private static boolean canUseInterface(Region region, AttributionContext ctx, List<KType> types, KType returnType, KType.ClassType toCheck) {
        var paramsFromType = getParamsFromType(ctx, types.size(), toCheck);
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
     * checks if a given class type is a functional interface and returns the parameters and return type of the abstract method
     */
    private static @Nullable ParamsAndReturn getParamsFromType(AttributionContext ctx, int argsSize, KType.ClassType toCheck) {
        Log.recordType(Log.LogTypes.CLOSURE, toCheck.toString());
        var classModel = ctx.model().getClass(toCheck.pointer());
        if (!Modifier.isInterface(classModel.modifiers())) {
            Log.recordType(Log.LogTypes.CLOSURE, "Not a interface");
            return null;
        }
        MethodPointer pointer = null;
        MethodModel methodModel = null;
        for (var method : classModel.methods()) {
            if (!Modifier.isAbstract(method.modifiers())) {
                continue;
            }
            if (pointer == null) {
                pointer = method.pointer();
                methodModel = method;
            } else {
                return null;
            }
        }
        if (pointer == null) {
            Log.recordType(Log.LogTypes.CLOSURE, "No method found");
            return null;
        }

        //dont have to check, they should be always public
        //ctx.protection().canReference(ctx.owningClass(), pointer.classPointer(), methodModel.modifiers());

        if (methodModel.signature().parameters().size() != argsSize) {
            Log.recordType(Log.LogTypes.CLOSURE, "invalid parameter count");
            return null;
        }

        var mapped = new HashMap<Generic, KType>();
        for (var i = 0; i < classModel.generics().size(); i++) {
            var generic = classModel.generics().get(i);
            var type = toCheck.generics().get(i);
            mapped.put(generic, type);
        }
        var methodGenerics = methodModel.generics();
        for (var i = 0; i < methodGenerics.size(); i++) {
            var generic = methodGenerics.get(i);
            var type = new KType.Resolvable();
            mapped.put(generic, type);
        }
        for (var genericKTypeEntry : mapped.entrySet()) {
            Log.recordType(Log.LogTypes.CLOSURE, "generic",
                    genericKTypeEntry.getKey(),
                    " -> ",
                    genericKTypeEntry.getValue()
            );

        }
        var methodReturnType = Types.projectGenerics(methodModel.signature().returnType(), mapped);

        var unmappedParameters = methodModel.signature().parameters();
        var mappedParameters = unmappedParameters.stream().map(ref ->
                Types.projectGenerics(ref, mapped)
        ).toList();

        return new ParamsAndReturn(mappedParameters, methodReturnType);
    }


    record ParamsAndReturn(List<KType> params, @Nullable KType returnType) {}

    private record ArgsAndReturnType(List<NameAndOptType> args, KType returnType) {}
}
