package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.NameAndOptType;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;
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

        var interfaces = new ArrayList<>(expr.interfaces());

        for (var arg : expr.args()) {
            checkForPreexistingVariable(ctx, arg.name());
        }

        KType.ClassType additional = null;

        if (hint instanceof KType.ClassType classType) {
            var hintClass = ctx.model().getClass(classType.pointer());
            if (Modifier.isInterface(hintClass.modifiers())) {
                additional = classType;
            }
        }
        //todo use interfaces for inference
        if (useNoInterfaces(expr.region(), ctx, interfaces, additional)) {
            interfaces.add(additional);
        }


        var argsAndReturnType = getNewArgs(hint, expr);
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

        if (!newBody.doesReturn() && !returnType.isVoid()) {
            newBody = ctx.makeAssignment(newBody.region(), returnType, newBody);
        }


        var functionType = new KType.FunctionType(
                newArgs.stream().map(NameAndOptType::type).toList(),
                returnType,
                interfaces
        );

        var variables = newArgs.stream().map(NameAndOptType::symbol).toList();
        var symbol = new ClosureSymbol(functionType, captures, captureSelf, ctx.selfVariable(), variables);
        return of(ctx, new KExpr.Closure(
                expr.region(),
                newArgs,
                returnType,
                expr.interfaces(),
                newBody,
                symbol
        ));

    }

    /**
     * Constructs a new set of arguments with a potential hint, otherwise use the default arguments. 
     * When a parameter type is already present, it will be used, otherwise the type of the hint will be used
     * When no valid hint is present, all non annotated arguments will be of type Resolvable
     */
    private static ArgsAndReturnType getNewArgs(@Nullable KType hint, KExpr.Closure expr) {
        if (hint instanceof KType.FunctionType functionHint) {
            if (functionHint.arguments().size() == expr.args().size()) {
                var newArgs = new ArrayList<NameAndOptType>();
                for (var i = 0; i < functionHint.arguments().size(); i++) {
                    var suggestedType = functionHint.arguments().get(i);
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
                    returnType = functionHint.returnType();
                }
                if (returnType == null) {
                    returnType = KType.VOID;
                }

                return new ArgsAndReturnType(newArgs, returnType);
            }
        }
        //else if (hint instanceOf ClassType && hint.isInterface) ...
        //TODO implement interface
        
        return defaultArgs(expr);

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
            returnType = new KType.Resolvable();
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

    private record ArgsAndReturnType(List<NameAndOptType> args, KType returnType) {}

    /**
     * Validates all interfaces
     * @return true if the hint should be used
     */
    private static boolean useNoInterfaces(Region region, AttributionContext ctx, List<KType> interfaces, @Nullable KType.ClassType additional) {
        for (var anInterface : interfaces) {
            if (!(anInterface instanceof KType.ClassType classType)) {
                Log.attribError(new AttribError.NotAStruct(region, anInterface));
                throw new Log.KarinaException();
            }
            var clazz = ctx.model().getClass(classType.pointer());
            if (!Modifier.isInterface(clazz.modifiers())) {
                Log.attribError(new AttribError.NotAInterface(region, classType));
                throw new Log.KarinaException();
            }

            //TODO rest to validate function and same return type and arguments
            // then return them as hints for the arguments
        }

        if (additional != null) {
            var clazz = ctx.model().getClass(additional.pointer());
            if (!Modifier.isInterface(clazz.modifiers())) {
                return false;
            }

            //TODO basically the same as above
            return true;
        } else {
            return false;
        }
    }
}
