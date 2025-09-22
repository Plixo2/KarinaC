package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.compiler.utils.symbols.ClosureSymbol;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ClosureAttrib  {
    public static AttributionExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) {
        var owningClass = ctx.model().getClass(ctx.owningClass());
        var region = expr.region();

        for (var arg : expr.args()) {
            checkForPreexistingVariable(ctx, arg.name());
        }

        var argsAndReturnType = ClosureHelper.getClosureTypesFromHint(ctx, hint, expr);

        var newArgs = argsAndReturnType.args();
        var returnType = argsAndReturnType.returnType();

        if (!Types.isTypeAccessible(ctx.protection(), owningClass, returnType)) {
            Log.error(ctx, new ImportError.AccessViolation(
                    region,
                    owningClass.name(),
                    null,
                    returnType
            ));
            throw new Log.KarinaException();
        }

        var bodyContext = new AttributionContext(
                ctx.model(),
                ctx.c(),
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
            //TODO dont
            bodyContext = bodyContext.markImmutable(variable);
        }

        for (var newArg : newArgs) {
            assert newArg.symbol() != null;
            if (newArg.type() != null && newArg.type().isVoid()) {
                Log.error(ctx, new AttribError.NotSupportedType(newArg.region(), newArg.type()));
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


        var newBody = attribExpr(returnType, bodyContext, expr.body()).expr();


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

        for (var arg : args) {
            if (!Types.isTypeAccessible(ctx.protection(), owningClass, arg)) {
                Log.error(ctx, new ImportError.AccessViolation(
                        region,
                        owningClass.name(),
                        null,
                        arg
                ));
                throw new Log.KarinaException();
            }
        }

        List<KType.ClassType> interfaces = new ArrayList<>();

        for (var anInterface : expr.interfaces()) {
            if (!(anInterface instanceof KType.ClassType classType)) {
                Log.error(ctx, new AttribError.NotAClass(region, anInterface));
                throw new Log.KarinaException();
            }
            var model = ctx.model().getClass(classType.pointer());
            if (!Modifier.isInterface(model.modifiers())) {
                Log.error(ctx, new AttribError.NotAInterface(region, classType));
                throw new Log.KarinaException();
            }
            if (!Types.isTypeAccessible(ctx.protection(), owningClass, classType)) {
                Log.error(ctx, new ImportError.AccessViolation(
                        region,
                        owningClass.name(),
                        RegionOf.region(region, classType.pointer()),
                        classType
                ));
                throw new Log.KarinaException();
            }

            if (!ClosureHelper.canUseInterface(region, ctx.intoContext(), ctx.model(), args, returnType, classType)) {
                Log.error(ctx, new AttribError.NotAValidInterface(region, args, returnType, classType));
                throw new Log.KarinaException();
            }

            //TODO check if interface is a supertype or subType of a already added interface
            var alreadyAdded = ClosureHelper.isInterfaceAlreadyAdded(classType, interfaces);
            if (alreadyAdded) {
                Log.error(ctx, new AttribError.DuplicateInterface(region, classType));
                throw new Log.KarinaException();
            }

            interfaces.add(classType);
        }

        if (hint instanceof KType.ClassType classType) {
            //TODO check if interface is a supertype or subType of a already added interface
            var alreadyAdded = ClosureHelper.isInterfaceAlreadyAdded(classType, interfaces);
            if (!alreadyAdded && ClosureHelper.canUseInterface(region, ctx.intoContext(), ctx.model(), args, returnType, classType)) {
                interfaces.add(classType);
            }
        }

        var primaryInterface = ClosureHelper.getDefaultInterface(ctx.intoContext(), region, ctx.model(), args, returnType);
        var readable = "fn(" + args.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ") -> " + returnType;
        if (primaryInterface != null) {
            var alreadyAdded = ClosureHelper.isInterfaceAlreadyAdded(primaryInterface, interfaces);
            if (!alreadyAdded) {
                if (ClosureHelper.canUseInterface(region, ctx.intoContext(), ctx.model(), args, returnType, primaryInterface)) {
                    interfaces.add(primaryInterface);
                }
            }
            interfaces = sortInterfaces(ctx, primaryInterface.pointer(), interfaces);
        }

        if (interfaces.isEmpty()) {
            Log.temp(ctx, region,
                    "Cannot find default interface for " + readable + ". " +
                    "Please specify an interface via 'impl' or give the closure more type hints."
            );
            throw new Log.KarinaException();
        }

        var functionType = new KType.FunctionType(
                args,
                returnType,
                interfaces
        );

        var variables = newArgs.stream().map(NameAndOptType::symbol).toList();
        var symbol = new ClosureSymbol(functionType, captures, captureSelf, ctx.selfVariable(), variables);

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
            Log.error(ctx, new AttribError.DuplicateVariable(
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
