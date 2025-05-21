package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.LowerError;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.stages.lower.special.*;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.*;

import java.util.List;
import java.util.Objects;

public class LowerExpr {


    public static KExpr lower(LoweringContext context, KExpr expr) {
        return Objects.requireNonNull(switch (expr) {
            case KExpr.Assignment assignment -> lowerAssignment(context, assignment);
            case KExpr.Binary binary -> lowerBinary(context, binary);
            case KExpr.Block block -> lowerBlock(context, block);
            case KExpr.Boolean aBoolean -> aBoolean;
            case KExpr.Branch branch -> lowerBranch(context, branch);
            case KExpr.Break aBreak -> aBreak;
            case KExpr.Call call -> lowerCall(context, call);
            case KExpr.Cast cast -> lowerCast(context, cast);
            case KExpr.Closure closure -> lowerClosure(context, closure);
            case KExpr.Continue aContinue -> aContinue;
            case KExpr.CreateArray createArray -> lowerCreateArray(context, createArray);
            case KExpr.CreateObject createObject -> {
                Log.temp(createObject.region(), "CreateObject no longer valid");
                throw new Log.KarinaException();
            }
            case KExpr.For aFor -> lowerFor(context, aFor);
            case KExpr.GetArrayElement getArrayElement -> lowerGetArrayElement(context, getArrayElement);
            case KExpr.GetMember getMember -> lowerGetMember(context, getMember);
            case KExpr.IsInstanceOf isInstanceOf -> lowerInstanceOf(context, isInstanceOf);
            case KExpr.Literal literal -> lowerLiteral(context, literal);
            case KExpr.Match match -> lowerMatch(context, match);
            case KExpr.Number number -> number;
            case KExpr.Return aReturn -> lowerReturn(context, aReturn);
            case KExpr.Self self -> lowerSelf(context, self);
            case KExpr.SpecialCall specialCall -> {
                Log.lowerError(new LowerError.NotValidAnymore(
                        specialCall.region(),
                        "Special Call cannot be expressed"
                ));
                throw new Log.KarinaException();
            }
            case KExpr.StringExpr stringExpr -> stringExpr;
            case KExpr.StringInterpolation stringInterpolation -> lowerStringInterpolation(context, stringInterpolation);
            case KExpr.Throw aThrow -> lowerThrow(context, aThrow);
            case KExpr.Unary unary -> lowerUnary(context, unary);
            case KExpr.Unwrap unwrap -> lowerUnwrap(context, unwrap);
            case KExpr.VariableDefinition variableDefinition -> lowerVariableDefinition(context, variableDefinition);
            case KExpr.While aWhile -> lowerWhile(context, aWhile);
            case KExpr.StaticPath staticPath -> {
                Log.lowerError(new LowerError.NotValidAnymore(
                        staticPath.region(),
                        "Paths cannot be expressed"
                ));
                throw new Log.KarinaException();
            }

        });
    }

    ///
    /// Signature:
    ///     `While(Region region, KExpr condition, KExpr body)`
    private static KExpr lowerWhile(LoweringContext ctx, KExpr.While expr) {
        var region = expr.region();
        var condition = lower(ctx, expr.condition());
        var body = lower(ctx, expr.body());
        return new KExpr.While(region, condition, body);
    }

    ///
    /// Signature:
    ///     `VariableDefinition(Region region, RegionOf<String> name, @Nullable KType varHint, KExpr value, @Nullable @Symbol Variable symbol)`
    private static KExpr lowerVariableDefinition(LoweringContext context, KExpr.VariableDefinition expr) {
        var region = expr.region();
        var name = expr.name();
        var varHint = expr.varHint();
        var value = lower(context, expr.value());
        var symbol = expr.symbol();
        assert symbol != null;
        return new KExpr.VariableDefinition(region, name, varHint, value, symbol);
    }


    ///
    /// Signature:
    ///     `Unary(Region region, RegionOf<UnaryOperator> operator, KExpr value, @Nullable @Symbol UnaryOperatorSymbol symbol)`
    private static KExpr lowerUnary(LoweringContext context, KExpr.Unary expr) {
        var region = expr.region();
        var operator = expr.operator();
        var value = lower(context, expr.value());
        var symbol = expr.symbol();
        assert symbol != null;
        return new KExpr.Unary(region, operator, value, symbol);
    }

    ///
    /// Signature:
    ///     `Throw(Region region, KExpr value)`
    private static KExpr lowerThrow(LoweringContext context, KExpr.Throw expr) {
        var region = expr.region();
        var value = lower(context, expr.value());
        return new KExpr.Throw(region, value);
    }

    /// @See {@link LowerStringInterpolation}
    private static KExpr lowerStringInterpolation(LoweringContext context, KExpr.StringInterpolation expr) {
        var lowerStringInterpolation = new LowerStringInterpolation(expr);
        return lowerStringInterpolation.lower(context);
    }

    ///
    /// Signature:
    ///     `Self(Region region, @Nullable @Symbol Variable symbol)`
    private static KExpr lowerSelf(LoweringContext context, KExpr.Self self) {
        return context.lowerSelf(self);
    }

    ///
    /// Signature:
    ///     `Return(Region region, @Nullable KExpr value, @Nullable @Symbol KType returnType)`
    private static KExpr lowerReturn(LoweringContext context, KExpr.Return expr) {
        var region = expr.region();
        KExpr value = null;
        if (expr.value() != null) {
            value = lower(context, expr.value());
        }
        var symbol = expr.returnType();
        assert symbol != null;
        return new KExpr.Return(region, value, symbol);
    }

    ///
    /// Signature:
    ///     `Match(Region region, KExpr value, List<MatchPattern> cases)`
    private static KExpr lowerMatch(LoweringContext context, KExpr.Match expr) {
        Log.temp(expr.region(), "Not implemented yet");
        throw new Log.KarinaException();
    }

    ///
    /// Signature:
    ///     `IsInstanceOf(Region region, KExpr left, KType isType)`
    private static KExpr lowerInstanceOf(LoweringContext context, KExpr.IsInstanceOf expr) {
        var region = expr.region();
        var left = lower(context, expr.left());
        var isType = expr.isType();
        return new KExpr.IsInstanceOf(region, left, isType);
    }

    ///
    /// Signature:
    ///     `GetMember(Region region, KExpr left, RegionOf<String> name, boolean isNextACall, @Nullable @Symbol MemberSymbol symbol)`
    private static KExpr lowerGetMember(LoweringContext context, KExpr.GetMember expr) {
        var region = expr.region();
        var left = lower(context, expr.left());
        var name = expr.name();
        var isNextACall = expr.isNextACall();
        var symbol = expr.symbol();
        assert symbol != null;
        if (symbol instanceof MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol) {
            Log.lowerError(new LowerError.NotValidAnymore(virtualFunctionSymbol.region(), "Virtual functions cannot be expressed"));
            throw new Log.KarinaException();
        }

        return new KExpr.GetMember(region, left, name, isNextACall, symbol);
    }

    ///
    /// Signature:
    ///     `GetArrayElement(Region region, KExpr left, KExpr index, @Nullable @Symbol KType elementType)`
    private static KExpr lowerGetArrayElement(LoweringContext context, KExpr.GetArrayElement expr) {
        var region = expr.region();
        var left = lower(context, expr.left());
        var index = lower(context, expr.index());
        var symbol = expr.elementType();
        assert symbol != null;
        return new KExpr.GetArrayElement(region, left, index, symbol);
    }

    ///
    /// Signature:
    ///     `CreateArray(Region region, @Nullable KType hint, List<KExpr> elements, @Nullable @Symbol KType.ArrayType symbol) `
    private static KExpr lowerCreateArray(LoweringContext context, KExpr.CreateArray expr) {
        var region = expr.region();
        var hint = expr.hint();
        var elements = expr.elements().stream()
                .map(element -> lower(context, element))
                .toList();
        var symbol = expr.symbol();
        assert symbol != null;
        return new KExpr.CreateArray(region, hint, elements, symbol);
    }

    ///
    /// Signature:
    ///     `Cast(Region region, KExpr expression, CastTo cast, @Nullable @Symbol CastSymbol symbol)`
    private static KExpr lowerCast(LoweringContext context, KExpr.Cast expr) {
        var region = expr.region();
        var expression = lower(context, expr.expression());
        var cast = expr.cast();
        var symbol = expr.symbol();
        assert symbol != null;
        return new KExpr.Cast(region, expression, cast, symbol);
    }

    ///
    /// Some variables have to be replaced with their references in closures.
    /// Signature:
    ///     `Literal(Region region, String name, @Nullable @Symbol LiteralSymbol symbol)`
    private static KExpr lowerLiteral(LoweringContext context, KExpr.Literal expr) {
        var region = expr.region();
        var name = expr.name();
        var symbol = expr.symbol();
        assert symbol != null;
        switch (symbol) {
            case LiteralSymbol.StaticClassReference staticClassReference -> {
                //ok (classtype)
            }
            case LiteralSymbol.StaticFieldReference staticFieldReference -> {
                //ok
            }
            case LiteralSymbol.Null aNull -> {
                // ok
            }
            case LiteralSymbol.StaticMethodReference staticMethodReference -> {
                Log.lowerError(new LowerError.NotValidAnymore(staticMethodReference.region(), "Static Method cannot be expressed"));
                throw new Log.KarinaException();
            }
            case LiteralSymbol.VariableReference variableReference -> {
                //ok
                var mapped = context.lowerVariableReference(variableReference);
                if (mapped != null) {
                    Log.recordType(Log.LogTypes.LOWERING, "(ok) Variable ", variableReference.variable().name(), " replaced");
                    return mapped;
                }
                Log.recordType(Log.LogTypes.LOWERING, "Variable ", variableReference.variable().name(), "not replaced");
            }
        }
        return new KExpr.Literal(region, name, symbol);
    }

    ///
    /// Replaces calls to function types with calls to the primary first interface
    ///
    ///
    /// Signature:
    ///     `Call(Region region, KExpr left, List<KType> generics, List<KExpr> arguments, @Nullable @Symbol CallSymbol symbol)`
    private static KExpr lowerCall(LoweringContext context, KExpr.Call expr) {
        var region = expr.region();
        var symbol = expr.symbol();

        var generics = expr.generics();
        var arguments = expr.arguments().stream()
                            .map(arg -> lower(context, arg))
                            .toList();

        assert symbol != null;
        KExpr left;
        switch (symbol) {
            case CallSymbol.CallDynamic callDynamic -> {

                Log.recordType(Log.LogTypes.LOWERING,"CallDynamic", callDynamic.region());
                left = lower(context, expr.left());
                if (left.type() instanceof KType.FunctionType functionType) {
                    Log.recordType(Log.LogTypes.LOWERING, "Type is ", left.type());
                    if (functionType.interfaces().isEmpty()) {
                        Log.temp(expr.region(), "No interface to call, this should not happen");
                        throw new Log.KarinaException();
                    }
                    var firstToCall = functionType.interfaces().getFirst();
                    if (!(firstToCall instanceof KType.ClassType classType)) {
                        Log.temp(expr.region(), "Invalid interface");
                        throw new Log.KarinaException();
                    }
                    var toCall = LowerClosure.getMethodToImplement(
                            expr.region(),
                            context.model(),
                            classType
                    );
                    var newSymbol = new CallSymbol.CallVirtual(
                            toCall.originalMethodPointer(), List.of(), callDynamic.returnType(), true
                    );
                    return new KExpr.Call(region, left, generics, arguments, newSymbol);
                } else {
                    Log.lowerError(new LowerError.NotValidAnymore(callDynamic.region(), "Dynamic Call cannot be expressed"));
                    throw new Log.KarinaException();
                }

            }
            case CallSymbol.CallStatic callStatic -> {
                left = expr.left(); //ignore
            }
            case CallSymbol.CallSuper callSuper -> {
                left = expr.left(); //also ignore?
            }
            case CallSymbol.CallVirtual callVirtual -> {
                left = lower(context, expr.left());
            }
        }
        return new KExpr.Call(region, left, generics, arguments, symbol);
    }


    /// @See {@link LowerBranch}
    private static KExpr lowerBranch(LoweringContext context, KExpr.Branch expr) {
        var lowerBranch = new LowerBranch(expr);
        return lowerBranch.lower(context);
    }

    ///
    /// Signature:
    ///     `Block(Region region, List<KExpr> expressions, @Nullable @Symbol KType symbol, @Symbol boolean doesReturn)`
    private static KExpr lowerBlock(LoweringContext context, KExpr.Block block) {
        var region = block.region();
        var expressions = block.expressions().stream()
                .map(expr -> lower(context, expr))
                .toList();
        var symbol = block.symbol();
        var doesReturn = block.doesReturn();
        return new KExpr.Block(region, expressions, symbol, doesReturn);
    }

    ///
    /// Signature:
    ///     `Binary(Region region, KExpr left, RegionOf<BinaryOperator> operator, KExpr right, @Nullable @Symbol BinOperatorSymbol symbol)`
    private static KExpr lowerBinary(LoweringContext context, KExpr.Binary expr) {
        var region = expr.region();
        var left = lower(context, expr.left());
        var operator = expr.operator();
        var right = lower(context, expr.right());
        var symbol = expr.symbol();
        assert symbol != null;
        return new KExpr.Binary(region, left, operator, right, symbol);
    }

    ///
    /// Signature:
    ///     `Assignment(Region region, KExpr left, KExpr right, @Nullable @Symbol AssignmentSymbol symbol)`
    private static KExpr lowerAssignment(LoweringContext ctx, KExpr.Assignment expr) {
        var region = expr.region();
        var left = lower(ctx, expr.left());
        var right = lower(ctx, expr.right());
        assert expr.symbol() != null;
        var symbol = switch (expr.symbol()) {
            case AssignmentSymbol.ArrayElement(KExpr array, KExpr index, KType elementType) -> {
                var arrayLower = lower(ctx, array);
                var indexLower = lower(ctx, index);
                yield new AssignmentSymbol.ArrayElement(arrayLower, indexLower, elementType);
            }
            case AssignmentSymbol.Field(KExpr object, FieldPointer pointer, KType fieldType, KType fieldOwner) -> {
                var objectLower = lower(ctx, object);
                yield new AssignmentSymbol.Field(objectLower, pointer, fieldType, fieldOwner);
            }
            case AssignmentSymbol.LocalVariable localVariable -> {
                yield localVariable;
            }
            case AssignmentSymbol.StaticField staticField -> {
                yield staticField;
            }
        };

        return new KExpr.Assignment(region, left, right, symbol);
    }


    /// @See {@link LowerFor}
    private static KExpr lowerFor(LoweringContext ctx, KExpr.For aFor) {
        var lowerFor = new LowerFor(aFor);
        return lowerFor.lower(ctx);
    }


    ///  @See {@link LowerClosure}
    private static KExpr lowerClosure(LoweringContext ctx, KExpr.Closure closure) {
        var lowerClosure = new LowerClosure(closure);
        return lowerClosure.lower(ctx);
    }

    /// See {@link LowerUnwrap}
    private static KExpr lowerUnwrap(LoweringContext context, KExpr.Unwrap expr) {
        var lowerUnwrap = new LowerUnwrap(expr);
        return lowerUnwrap.lower(context);
    }
}
