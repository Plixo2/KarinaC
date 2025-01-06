package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.symbols.*;

import java.math.BigDecimal;
import java.util.List;

public sealed interface KExpr {
    Span region();

    default KType type() {
        var type = getType(this);
        if (type == null) {
            Log.temp(region(), "Type is null for " + this.getClass().getSimpleName());
            throw new Log.KarinaException();
        }
        return type.unpack();
    }

    private static KType getType(KExpr expr) {
        switch (expr) {
            case Assignment assignment -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Binary binary -> {
                if (binary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return binary.symbol().type();
            }
            case Block block -> {
                return block.symbol();
            }
            case Boolean aBoolean -> {
                return new KType.PrimitiveType.BoolType(expr.region());
            }
            case Branch branch -> {
                return branch.symbol();
            }
            case Break aBreak -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Call call -> {
                if (call.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return call.symbol().returnType();
            }
            case Cast cast -> {
                if (cast.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return cast.symbol().type();
            }
            case Closure closure -> {
                if (closure.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return closure.symbol().type();
            }
            case Continue aContinue -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case CreateArray createArray -> {
                return createArray.symbol();
            }
            case CreateObject createObject -> {
                return createObject.symbol();
            }
            case For aFor -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case GetArrayElement getArrayElement -> {
                return getArrayElement.elementType();
            }
            case GetMember getMember -> {
                if (getMember.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return getMember.symbol().type();
            }
            case IsInstanceOf isInstanceOf -> {
                return new KType.PrimitiveType.BoolType(expr.region());
            }
            case Literal literal -> {
                if (literal.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return literal.symbol().type();
            }
            case Match match -> {

            }
            case Number number -> {
                if (number.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return number.symbol().type();
            }
            case Return aReturn -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Self self -> {
                if (self.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return self.symbol().type();
            }
            case StringExpr stringExpr -> {
                return new KType.PrimitiveType.StringType(expr.region());
            }
            case Unary unary -> {
                if (unary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return unary.symbol().type();
            }
            case VariableDefinition variableDefinition -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case While aWhile -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Throw aThrow -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
        }
        Log.temp(expr.region(), "Unimplemented type for " + expr.getClass().getSimpleName());
        throw new Log.KarinaException();
    }

    record Block(Span region, List<KExpr> expressions, @Nullable @Symbol KType symbol) implements KExpr {}
    record VariableDefinition(Span region, SpanOf<String> name, @Nullable KType hint, KExpr value, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Branch(Span region, KExpr condition, KExpr thenArm, @Nullable KExpr elseArm, @Nullable BranchPattern branchPattern, @Nullable @Symbol KType symbol) implements KExpr {}
    record While(Span region, KExpr condition, KExpr body) implements KExpr {}
    record For(Span region, SpanOf<String> name , KExpr iter, KExpr body, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Return(Span region, @Nullable KExpr value) implements KExpr {}
    record Break(Span region) implements KExpr {}
    record Continue(Span region) implements KExpr {}
    record Binary(Span region, KExpr left, SpanOf<BinaryOperator> operator, KExpr right, @Nullable @Symbol BinOperatorSymbol symbol) implements KExpr {}
    record Unary(Span region, SpanOf<UnaryOperator> operator, KExpr value, @Nullable @Symbol UnaryOperatorSymbol symbol) implements KExpr {}
    record IsInstanceOf(Span region, KExpr left, KType isType) implements KExpr {}
    record GetMember(Span region, KExpr left, SpanOf<String> name, @Nullable @Symbol MemberSymbol symbol) implements KExpr {}
    record Call(Span region, KExpr left, List<KType> generics, List<KExpr> arguments, @Nullable @Symbol CallSymbol symbol) implements KExpr {}
    record GetArrayElement(Span region, KExpr left, KExpr index, @Nullable @Symbol KType elementType) implements KExpr {}
    record Cast(Span region, KExpr expression, KType asType, @Nullable @Symbol CastSymbol symbol) implements KExpr {}
    record Assignment(Span region, KExpr left, KExpr right, @Nullable @Symbol AssignmentSymbol symbol) implements KExpr {}
    record CreateArray(Span region, @Nullable KType hint, List<KExpr> elements, @Nullable @Symbol KType symbol) implements KExpr {}
    record Number(Span region, BigDecimal number, boolean decimalAnnotated, @Nullable @Symbol NumberSymbol symbol) implements KExpr {}
    record Boolean(Span region, boolean value) implements KExpr {}
    record Literal(Span region, String name, @Nullable @Symbol LiteralSymbol symbol) implements KExpr {}
    record Self(Span region, @Nullable @Symbol Variable symbol) implements KExpr {}
    record StringExpr(Span region, String value) implements KExpr {}
    record Match(Span region, KExpr value, List<MatchPattern> cases) implements KExpr {}
    record CreateObject(Span region, KType createType, List<NamedExpression> parameters, @Nullable @Symbol KType.ClassType symbol) implements KExpr {}
    record Closure(Span region, List<NameAndOptType> args, @Nullable KType returnType, List<KType> interfaces, KExpr body, @Nullable @Symbol ClosureSymbol symbol) implements KExpr {}
    record Throw(Span region, KExpr value, @Nullable @Symbol KType.ClassType symbol) implements KExpr {}


}
