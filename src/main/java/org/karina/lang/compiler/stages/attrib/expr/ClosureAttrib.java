package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.ClosureSymbol;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ClosureAttrib  {
    public static AttributionExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) {

        var logName = "Attributing closure";
        Log.beginType(Log.LogTypes.CLOSURE, logName);
        var owningMethod = ctx.owningMethod();
        var region = expr.region();
        Log.recordType(Log.LogTypes.CLOSURE, "Closure", region, "in", ctx.owningClass(), "/", owningMethod != null ? owningMethod.name() : null);

        for (var arg : expr.args()) {
            checkForPreexistingVariable(ctx, arg.name());
        }

        var argsAndReturnType = ClosureHelper.getClosureTypesFromHint(ctx, hint, expr);

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
            // dont... do this.
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

        Log.recordType(Log.LogTypes.CLOSURE,
                "parameter types pre body",
                argsAndReturnType.args().stream().map(NameAndOptType::type).toList()
        );

        var newBody = attribExpr(returnType, bodyContext, expr.body()).expr();

        Log.recordType(Log.LogTypes.CLOSURE,
                "parameter types post body",
                argsAndReturnType.args().stream().map(NameAndOptType::type).toList()
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
        newBody = MethodHelper.createRetuningExpression(newBody, returnType, ctx);

        var args = newArgs.stream().map(NameAndOptType::type).toList();

        List<KType.ClassType> interfaces = new ArrayList<>();

        for (var anInterface : expr.interfaces()) {
            Log.beginType(Log.LogTypes.CLOSURE, "Checking annotated interface");
            if (!(anInterface instanceof KType.ClassType classType)) {
                Log.record("Impl is not a class");
                Log.attribError(new AttribError.NotAClass(region, anInterface));
                throw new Log.KarinaException();
            }
            var model = ctx.model().getClass(classType.pointer());
            if (!Modifier.isInterface(model.modifiers())) {
                Log.record("Impl is not a interface");
                Log.attribError(new AttribError.NotAInterface(region, classType));
                throw new Log.KarinaException();
            }
            if (!ClosureHelper.canUseInterface(region, ctx, args, returnType, classType)) {
                Log.record("Impl is not a matching interface");
                Log.attribError(new AttribError.NotAValidInterface(region, args, returnType, classType));
                throw new Log.KarinaException();
            }
            Log.endType(Log.LogTypes.CLOSURE, "Checking annotated interface");

            //TODO check if interface is a supertype or subType of a already added interface
            var alreadyAdded = interfaces.stream().anyMatch(ref -> ref.pointer().equals(classType.pointer()));
            if (alreadyAdded) {
                Log.record("Interface already added");
                Log.attribError(new AttribError.DuplicateInterface(region, classType));
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
            } else if (ClosureHelper.canUseInterface(region, ctx, args, returnType, classType)) {
                Log.recordType(Log.LogTypes.CLOSURE, "Using hint as interface");
                interfaces.add(classType);
            }
            Log.endType(Log.LogTypes.CLOSURE, "Checking hint interface");
        }

        var doesReturn = !returnType.isVoid();
        var primaryInterface = KType.FUNCTION_BASE(ctx.model(), args.size(), doesReturn);
        if (primaryInterface == null) {
            Log.temp(region, "Cannot find default interface for " + args.size() + " arguments and return type " + returnType);
            throw new Log.KarinaException();
        }
        {
            var totalGenerics = args.size() + (doesReturn ? 1 : 0);

            var model = ctx.model().getClass(primaryInterface);
            if (model.generics().size() != totalGenerics) {
                Log.temp(region, "Expected " + totalGenerics + " generics, but got " + model.generics().size());
                throw new Log.KarinaException();
            }

            var generics = new ArrayList<KType>();
            for (var i = 0; i < totalGenerics; i++) {
                generics.add(new KType.Resolvable());
            }

            var classType = new KType.ClassType(primaryInterface, generics);
            var alreadyAdded = interfaces.stream().anyMatch(ref -> ref.pointer().equals(classType.pointer()));
            if (!alreadyAdded) {
                if (ClosureHelper.canUseInterface(region, ctx, args, returnType, classType)) {
                    Log.recordType(Log.LogTypes.CLOSURE, "Using default as interface");
                    interfaces.add(classType);
                } else {
                    Log.recordType(Log.LogTypes.CLOSURE, "(error) Could not use default as interface");
                }
            } else {
                Log.recordType(Log.LogTypes.CLOSURE, "default already added");
            }

        }
        interfaces = sortInterfaces(ctx, primaryInterface, interfaces);

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
                region,
                newArgs,
                returnType,
                interfaces,
                newBody,
                symbol
        ));

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


    private static List<KType.ClassType> sortInterfaces(AttributionContext ctx, ClassPointer primary, List<KType.ClassType> interfaces) {

        return interfaces.stream().sorted(Comparator.comparingInt(ref -> {
            if (ref.pointer().equals(primary)) {
                return 0;
            } else {
                return 1;
            }
        })).toList();

    }


}
