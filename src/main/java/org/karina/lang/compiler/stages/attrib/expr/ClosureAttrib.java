package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.NameAndOptType;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.symbols.ClosureSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ClosureAttrib extends AttribExpr {
    public static AttribExpr attribClosure(
            @Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) {

        for (var arg : expr.args()) {
            checkForPreexistingVariable(ctx, arg.name());
        }

        var argsAndReturnType = getNewArgs(hint, expr);
        var newArgs = argsAndReturnType.args();
        var returnType = argsAndReturnType.returnType();

        var bodyContext = new AttributionContext(
                ctx.root(),
                ctx.selfType(),
                false,
                expr.region(),
                returnType,
                ctx.variables(),
                ctx.table(),
                ctx.checking()
        );

        //make all variables from the outside immutable in the body
        for (var variable : ctx.variables()) {
            bodyContext = bodyContext.markImmutable(variable);
        }

        for (var newArg : newArgs) {
            assert newArg.symbol() != null;
            bodyContext = bodyContext.addVariable(newArg.symbol());
        }


        var usageSelf = ctx.selfType() != null ? ctx.selfType().usageCount() : 0;
        var usageMap = new HashMap<Variable, Integer>();
        for (var variable : ctx.variables()) {
            usageMap.put(variable, variable.usageCount());
        }


        var newBody = attribExpr(returnType, bodyContext, expr.body()).expr();

        var usageSelfPost = ctx.selfType() != null ? ctx.selfType().usageCount() : 0;
        var captureSelf = usageSelfPost > usageSelf;

        var captures = new ArrayList<Variable>();
        for (var variable : ctx.variables()) {
            var usageCount = variable.usageCount();
            if (usageMap.get(variable) != usageCount) {
                captures.add(variable);
            }
        }


        if (doesReturn(newBody)) {
            // ok
        } else if (!returnType.isVoid()) {
            ctx.assign(newBody.region(), returnType, newBody.type());
        }

        //TODO check interfaces, if they are functional and disjointed

        var functionType = new KType.FunctionType(
                expr.region(), newArgs.stream().map(NameAndOptType::type).toList(), returnType,
                expr.interfaces()
        );
        var symbol = new ClosureSymbol(functionType, captures, captureSelf);
        return of(ctx, new KExpr.Closure(
                expr.region(),
                newArgs,
                returnType,
                expr.interfaces(),
                newBody, symbol
        ));
    }

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
                    returnType = new KType.PrimitiveType.VoidType(expr.region());
                }

                return new ArgsAndReturnType(newArgs, returnType);
            }
        }
        return defaultArgs(expr);

    }

    private static ArgsAndReturnType defaultArgs(KExpr.Closure expr) {
        var newArgs = new ArrayList<NameAndOptType>();
        for (var arg : expr.args()) {
            var type = arg.type();
            if (type == null) {
                type = new KType.Resolvable(arg.region());
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
            returnType = new KType.Resolvable(expr.region());
        }

        return new ArgsAndReturnType(newArgs, returnType);
    }


    private static void checkForPreexistingVariable(AttributionContext ctx, SpanOf<String> argument) {
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
}
