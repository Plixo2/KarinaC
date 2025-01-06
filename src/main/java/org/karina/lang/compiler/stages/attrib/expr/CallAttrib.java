package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.symbols.CallSymbol;
import org.karina.lang.compiler.stages.symbols.LiteralSymbol;
import org.karina.lang.compiler.stages.symbols.MemberSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallAttrib extends AttribExpr {
    public static AttribExpr attribCall(
            @Nullable KType hint, AttributionContext ctx, KExpr.Call expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var genericsAnnotated = !expr.generics().isEmpty();
        List<KExpr> newArguments;
        CallSymbol symbol;
        //OMG PATTERNS
        if (left instanceof KExpr.Literal(var ignored, var ignored2, LiteralSymbol.StaticFunction staticFunction)) {
            var referencedFunction = KTree.findAbsolutItem(ctx.root(), staticFunction.path());
            if (!(referencedFunction instanceof KTree.KFunction function)) {
                Log.temp(expr.region(), "Function not found");
                throw new Log.KarinaException();
            }
            HashMap<Generic, KType> mapped = new HashMap<>();
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
                    var type = new KType.PrimitiveType.Resolvable(expr.region());
                    mapped.put(generic, type);
                }
            }
            var staticReturnType = function.returnType();
            if (staticReturnType == null) {
                staticReturnType = new KType.PrimitiveType.VoidType(expr.region());
            }
            var returnType = replaceType(staticReturnType, mapped);

            newArguments = new ArrayList<>();
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

            symbol = new CallSymbol.CallStatic(
                    staticFunction.path(),
                    List.copyOf(mapped.values()),
                    returnType
            );


        } else if (left instanceof KExpr.GetMember(var ignored, var object, var name, MemberSymbol.VirtualFunctionSymbol sym)) {
            var referencedFunction = KTree.findAbsoluteVirtualFunction(ctx.root(), sym.path());
            var referencedClass = KTree.findAbsolutItem(ctx.root(), sym.classType().path().value());
            if (!(referencedFunction instanceof KTree.KFunction function) || !(referencedClass instanceof KTree.KStruct struct)) {
                Log.temp(expr.region(), "Function not found");
                throw new Log.KarinaException();
            }
            var mapped = new HashMap<Generic, KType>();

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
                    var type = new KType.PrimitiveType.Resolvable(expr.region());
                    mapped.put(generic, type);
                }
            }
            for (var i = 0; i < sym.classType().generics().size(); i++) {
                var generic = struct.generics().get(i);
                var type = sym.classType().generics().get(i);
                mapped.put(generic, type);
            }


            var vReturnType = function.returnType();
            if (vReturnType == null) {
                vReturnType = new KType.PrimitiveType.VoidType(expr.region());
            }
            var returnType = replaceType(vReturnType, mapped);

            newArguments = new ArrayList<>();
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

            symbol = new CallSymbol.CallVirtual(
                    name.region(),
                    sym.classType(),
                    sym.path(),
                    List.copyOf(mapped.values()),
                    returnType
            );
            left = object;
        } else if (left.type() instanceof KType.FunctionType functionType) {

            newArguments = new ArrayList<>();
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
                returnType = new KType.PrimitiveType.VoidType(expr.region());
            }

            symbol = new CallSymbol.CallDynamic(returnType);

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

}
