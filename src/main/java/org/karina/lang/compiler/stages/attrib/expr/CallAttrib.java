package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.SpanOf;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallAttrib extends AttributionExpr {
    public static AttributionExpr attribCall(
            @Nullable KType hint, AttributionContext ctx, KExpr.Call expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var genericsAnnotated = !expr.generics().isEmpty();
        List<KExpr> newArguments = new ArrayList<>();
        CallSymbol symbol;
        //OMG PATTERNS
        if (left instanceof KExpr.Literal(var ignored, var ignored2, LiteralSymbol.StaticFunction staticFunction)) {
            symbol = getStatic(ctx, expr, staticFunction, genericsAnnotated, newArguments);
        } else if (left instanceof KExpr.GetMember(var ignored, var object, var name, MemberSymbol.VirtualFunctionSymbol sym)) {
            symbol = getVirtual(ctx, expr, name, sym, genericsAnnotated, newArguments);
            left = object;
        } else if (left instanceof KExpr.GetMember(var ignored, var object, var name, MemberSymbol.InterfaceFunctionSymbol sym)) {
            symbol = getInterface(ctx, expr, name, sym, genericsAnnotated, newArguments);
            left = object;
        } else if (left.type() instanceof KType.FunctionType functionType) {
            symbol = getFunctionalType(ctx, expr, functionType, newArguments);
        } else {
            Log.temp(expr.region(), "Invalid call");
            throw new Log.KarinaException();
        }


        return of(ctx, new KExpr.Call(
                expr.region(),
                left,
                expr.generics(),
                newArguments,
                symbol
        ));
    }

    private static @NotNull CallSymbol getStatic(
            AttributionContext ctx, KExpr.Call expr, LiteralSymbol.StaticFunction staticFunction,
            boolean genericsAnnotated, List<KExpr> newArguments
    ) {
        CallSymbol symbol;
        var referencedFunction = KTree.findAbsolutItem(ctx.root(), staticFunction.path());
        if (!(referencedFunction instanceof KTree.KFunction function)) {
            Log.temp(expr.region(), "Function not found, this should not happen");
            throw new Log.KarinaException();
        }
        HashMap<Generic, KType> mapped = new HashMap<>();
        putMappedGenerics(expr, genericsAnnotated, function, mapped);
        var returnType = putParameters(ctx, expr, newArguments, function, mapped);

        boolean inInterface = false;

        var mayInterface = KTree.findAbsolutItem(ctx.root(), staticFunction.path().everythingButLast());
        if (mayInterface instanceof KTree.KInterface) {
            inInterface = true;
        }

        symbol = new CallSymbol.CallStatic(
                staticFunction.path(),
                List.copyOf(mapped.values()),
                returnType,
                function.parameters().stream().map(KTree.KParameter::type).toList(),
                function.returnType(),
                inInterface
        );
        return symbol;
    }

    private static CallSymbol getInterface( AttributionContext ctx, KExpr.Call expr, SpanOf<String> name,
            MemberSymbol.InterfaceFunctionSymbol sym,
            boolean genericsAnnotated,
            List<KExpr> newArguments) {

        var referencedFunction = KTree.findAbsoluteInterfaceFunction(ctx.root(), sym.path());
        var referencedClass = KTree.findAbsolutItem(ctx.root(), sym.classType().path());
        if (!(referencedFunction instanceof KTree.KFunction function)) {
            Log.temp(expr.region(), "Function not found + " + sym.path());
            throw new Log.KarinaException();
        }
        var mapped = new HashMap<Generic, KType>();

        putMappedGenerics(expr, genericsAnnotated, function, mapped);
        if (referencedClass instanceof KTree.KInterface kInterface) {
            for (var i = 0; i < sym.classType().generics().size(); i++) {
                var generic = kInterface.generics().get(i);
                var type = sym.classType().generics().get(i);
                mapped.put(generic, type);
            }
        } else {
            Log.temp(expr.region(), "Interface not found");
            throw new Log.KarinaException();
        }

        var returnType = putParameters(ctx, expr, newArguments, function, mapped);

        return new CallSymbol.CallInterface(
                name.region(),
                sym.classType(),
                sym.path(),
                List.copyOf(mapped.values()),
                returnType,
                function.parameters().stream().map(KTree.KParameter::type).toList(),
                function.returnType()
        );
    }

    private static @NotNull CallSymbol getVirtual(
            AttributionContext ctx, KExpr.Call expr, SpanOf<String> name,
            MemberSymbol.VirtualFunctionSymbol sym, boolean genericsAnnotated,
            List<KExpr> newArguments
    ) {
        CallSymbol symbol;
        var referencedFunction = KTree.findAbsoluteVirtualFunction(ctx.root(), sym.path());
        var referencedClass = KTree.findAbsolutItem(ctx.root(), sym.classType().path());
        if (!(referencedFunction instanceof KTree.KFunction function)) {
            Log.temp(expr.region(), "Function not found");
            throw new Log.KarinaException();
        }
        if(!(referencedClass instanceof KTree.KStruct struct)) {
            Log.temp(expr.region(), "Class not found");
            throw new Log.KarinaException();
        }

        var mapped = new HashMap<Generic, KType>();

        putMappedGenerics(expr, genericsAnnotated, function, mapped);
        for (var i = 0; i < sym.classType().generics().size(); i++) {
            var generic = struct.generics().get(i);
            var type = sym.classType().generics().get(i);
            mapped.put(generic, type);
        }


        var returnType = putParameters(ctx, expr, newArguments, function, mapped);

        symbol = new CallSymbol.CallVirtual(
                name.region(),
                sym.classType(),
                sym.path(),
                List.copyOf(mapped.values()),
                returnType,
                function.parameters().stream().map(KTree.KParameter::type).toList(),
                function.returnType()
        );
        return symbol;
    }

    private static KType putParameters(
            AttributionContext ctx, KExpr.Call expr, List<KExpr> newArguments,
            KTree.KFunction function, HashMap<Generic, KType> mapped
    ) {
        var vReturnType = function.returnType();
        if (vReturnType == null) {
            vReturnType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        }
        var returnType = replaceType(vReturnType, mapped);

        var expected = function.parameters().size();
        var found = expr.arguments().size();
        if (found > expected) {
            var toMany = expr.arguments().get((found - expected) - 1);
            Log.attribError(new AttribError.ParameterCountMismatch(toMany.region(), expected));
            throw new Log.KarinaException();
        } else if (expected > found) {
            var missing = function.parameters().get((expected - found) - 1);
            Log.attribError(
                    new AttribError.MissingField(expr.region(), missing.name().value()));
            throw new Log.KarinaException();
        }


        for (var i = 0; i < expected; i++) {
            var parameter = function.parameters().get(i);
            var argument = expr.arguments().get(i);
            var argumentType = replaceType(parameter.type(), mapped);
            var newArgument = attribExpr(argumentType, ctx, argument).expr();
            ctx.assign(argument.region(), argumentType, newArgument.type());
            newArguments.add(newArgument);
        }
        return returnType;
    }

    private static void putMappedGenerics(
            KExpr.Call expr, boolean genericsAnnotated, KTree.KFunction function,
            HashMap<Generic, KType> mapped
    ) {
        if (genericsAnnotated) {
            if (expr.generics().size() != function.generics().size()) {
                Log.importError(
                        new ImportError.GenericCountMismatch(
                                expr.region(), function.name().value(),
                                function.generics().size(), expr.generics().size()
                        ));
                throw new Log.KarinaException();
            }

            for (var i = 0; i < function.generics().size(); i++) {
                var generic = function.generics().get(i);
                var type = expr.generics().get(i);
                mapped.put(generic, type);
            }
        } else {
            for (var i = 0; i < function.generics().size(); i++) {
                var generic = function.generics().get(i);
                var type = new KType.PrimitiveType.Resolvable();
                mapped.put(generic, type);
            }
        }
    }


    private static @NotNull CallSymbol getFunctionalType(
            AttributionContext ctx, KExpr.Call expr, KType.FunctionType functionType,
            List<KExpr> newArguments
    ) {
        CallSymbol symbol;
        var expected = functionType.arguments().size();
        var found = expr.arguments().size();
        if (found != expected) {
            var toMany = expr.arguments().get((found - expected) - 1);
            Log.attribError(new AttribError.ParameterCountMismatch(toMany.region(), expected));
            throw new Log.KarinaException();
        }

        for (var i = 0; i < expected; i++) {
            var parameter = functionType.arguments().get(i);
            var argument = expr.arguments().get(i);
            var newArgument = attribExpr(parameter, ctx, argument).expr();
            ctx.assign(argument.region(), parameter, newArgument.type());
            newArguments.add(newArgument);
        }

        var returnType = functionType.returnType();
        if (returnType == null) {
            returnType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        }

        symbol = new CallSymbol.CallDynamic(expr.region(), returnType);
        return symbol;
    }

}
