package org.karina.lang.compiler.objects;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.symbols.BranchYieldSymbol;

public class Expressions {

    /**
     * Should be called using expr.type()
     */
    static KType getType(KExpr expr) {
        switch (expr) {
            case KExpr.Assignment _ -> {
                return KType.NONE;
            }
            case KExpr.Binary binary -> {
                if (binary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return binary.symbol().type();
            }
            case KExpr.Block block -> {
                if (block.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return block.symbol();
            }
            case KExpr.Boolean _ -> {
                return KType.BOOL;
            }
            case KExpr.Branch branch -> {
                if (branch.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return branch.symbol().type();
            }
            case KExpr.Break aBreak -> {
                return KType.NONE;
            }
            case KExpr.Call call -> {
                if (call.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return call.symbol().returnType();
            }
            case KExpr.Cast cast -> {
                if (cast.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return cast.symbol().type();
            }
            case KExpr.Closure closure -> {
                if (closure.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return closure.symbol().type();
            }
            case KExpr.Continue aContinue -> {
                return KType.NONE;
            }
            case KExpr.CreateArray createArray -> {
                if (createArray.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return createArray.symbol();
            }
            case KExpr.CreateObject createObject -> {
                Log.temp(expr.region(), "CreateObject should be replaced with a call to the constructor");
                throw new Log.KarinaException();
//                if (createObject.symbol() == null) {
//                    Log.temp(expr.region(), "Symbol is null");
//                    throw new Log.KarinaException();
//                }
//                return createObject.symbol();
            }
            case KExpr.For aFor -> {
                return KType.NONE;
            }
            case KExpr.GetArrayElement getArrayElement -> {
                if (getArrayElement.elementType() == null) {
                    Log.temp(expr.region(), "Element type is null");
                    throw new Log.KarinaException();
                }
                return getArrayElement.elementType();
            }
            case KExpr.GetMember getMember -> {
                if (getMember.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return getMember.symbol().type();
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                return KType.BOOL;
            }
            case KExpr.Literal literal -> {
                if (literal.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return literal.symbol().type();
            }
            case KExpr.Match match -> {
                Log.temp(expr.region(), "Match not implemented");
                throw new Log.KarinaException();
            }
            case KExpr.Number number -> {
                if (number.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return number.symbol().type();
            }
            case KExpr.Return aReturn -> {
                return KType.NONE;
            }
            case KExpr.Self self -> {
                if (self.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                return self.symbol().type();
            }
            case KExpr.StringExpr stringExpr -> {
                if (stringExpr.isChar()) {
                    return KType.CHAR;
                } else {
                    return KType.STRING;
                }
            }
            case KExpr.Unwrap unwrap -> {
                if (unwrap.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return unwrap.symbol().unpackedType();
            }
            case KExpr.Unary unary -> {
                if (unary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return unary.symbol().type();
            }
            case KExpr.VariableDefinition variableDefinition -> {
                return KType.NONE;
            }
            case KExpr.While aWhile -> {
                return KType.NONE;
            }
            case KExpr.Throw aThrow -> {
                return KType.NONE;
            }
            case KExpr.SpecialCall aSpecialCall -> {
                return KType.NONE;
            }
            case KExpr.StringInterpolation stringInterpolation -> {
                return KType.STRING;
            }
            case KExpr.StaticPath staticPath -> {
                return KType.NONE;
            }
        }
    }


    static boolean doesReturn(KExpr expr) {
        return switch (expr) {
            case KExpr.Block block -> {
                //TODO replace with symbol
                if (block.expressions().isEmpty()) {
                    yield false;
                } else {
                    yield doesReturn(block.expressions().getLast());
                }
            }
            case KExpr.Branch branch -> {
                if (branch.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    throw new Log.KarinaException();
                }
                yield branch.symbol() instanceof BranchYieldSymbol.Returns;
            }

            case KExpr.Return aReturn -> true;
            //Also include loop control
            case KExpr.Continue aContinue -> true;
            case KExpr.Break aBreak -> true;
            //and throw
            case KExpr.Throw aThrow -> true;

            case KExpr.Boolean aBoolean -> false;
            case KExpr.Assignment assignment -> false;
            case KExpr.Binary binary -> false;
            case KExpr.Call call -> false;
            case KExpr.Cast cast -> false;
            case KExpr.Closure closure -> false;
            case KExpr.CreateArray createArray -> false;
            case KExpr.CreateObject createObject -> false;
            case KExpr.For aFor -> false;
            case KExpr.GetArrayElement getArrayElement -> false;
            case KExpr.GetMember getMember -> false;
            case KExpr.IsInstanceOf isInstanceOf -> false;
            case KExpr.Literal literal -> false;
            case KExpr.Match match -> false;
            case KExpr.Number number -> false;
            case KExpr.Self self -> false;
            case KExpr.StringExpr stringExpr ->false;
            case KExpr.StringInterpolation stringInterpolation -> false;
            case KExpr.Unary unary -> false;
            case KExpr.VariableDefinition variableDefinition ->false;
            case KExpr.While aWhile -> false;
            case KExpr.SpecialCall aSpecialCall -> false;
            case KExpr.Unwrap unwrap -> false;
            case KExpr.StaticPath staticPath -> false;
        };
    }

}
