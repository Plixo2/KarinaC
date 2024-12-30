package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.Log;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public sealed interface KExpr {
    Span region();

    default KType type() {
        var type = getType(this);
        if (type == null) {
            Log.temp(region(), "Type is null for " + this.getClass().getSimpleName());
            throw new Log.KarinaException();
        }
        return type;
    }

    private static KType getType(KExpr expr) {
        switch (expr) {
            case Assignment assignment -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Binary binary -> {
            }
            case Block block -> {
                return block.symbol();
            }
            case Boolean aBoolean -> {
                return new KType.PrimitiveType.BoolType(expr.region());
            }
            case Branch branch -> {

            }
            case Break aBreak -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case Call call -> {
            }
            case Cast cast -> {
            }
            case Closure closure -> {
            }
            case Continue aContinue -> {
            }
            case CreateArray createArray -> {
            }
            case CreateObject createObject -> {
                return createObject.symbol();
            }
            case For aFor -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case GetArrayElement getArrayElement -> {
            }
            case GetMember getMember -> {
            }
            case IsInstanceOf isInstanceOf -> {
                return new KType.PrimitiveType.BoolType(expr.region());
            }
            case Literal literal -> {
                return Objects.requireNonNull(literal.symbol()).type();
            }
            case Match match -> {

            }
            case Number number -> {
                return new KType.PrimitiveType.IntType(expr.region());
            }
            case Return aReturn -> {
                return new KType.PrimitiveType.StringType(expr.region());
            }
            case Self self -> {
                return self.symbol();
            }
            case StringExpr stringExpr -> {
                return new KType.PrimitiveType.StringType(expr.region());
            }
            case Unary unary -> {
            }
            case VariableDefinition variableDefinition -> {
                return new KType.PrimitiveType.VoidType(expr.region());
            }
            case While aWhile -> {
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
    record For(Span region, SpanOf<String> name , KExpr iter, KExpr body) implements KExpr {}
    record Return(Span region, @Nullable KExpr value) implements KExpr {}
    record Break(Span region) implements KExpr {}
    record Continue(Span region) implements KExpr {}
    record Binary(Span region, KExpr left, SpanOf<BinaryOperator> operator, KExpr right) implements KExpr {}
    record Unary(Span region, SpanOf<UnaryOperator> operator, KExpr value) implements KExpr {}
    record IsInstanceOf(Span region, KExpr left, KType isType) implements KExpr {}
    record GetMember(Span region, KExpr left, SpanOf<String> name) implements KExpr {}
    record Call(Span region, KExpr left, List<KType> generics, List<KExpr> arguments) implements KExpr {}
    record GetArrayElement(Span region, KExpr left, KExpr index) implements KExpr {}
    record Cast(Span region, KExpr expression, KType asType) implements KExpr {}
    record Assignment(Span region, KExpr left, KExpr right) implements KExpr {}
    record CreateArray(Span region, @Nullable KType hint, List<KExpr> elements, @Nullable @Symbol KType symbol) implements KExpr {}
    record Number(Span region, BigDecimal number) implements KExpr {}
    record Boolean(Span region, boolean value) implements KExpr {}
    record Literal(Span region, String name, @Nullable Variable symbol) implements KExpr {}
    record Self(Span region, @Nullable @Symbol KType symbol) implements KExpr {}
    record StringExpr(Span region, String value) implements KExpr {}
    record Match(Span region, KExpr value, List<MatchPattern> cases) implements KExpr {}
    record CreateObject(Span region, KType createType, List<NamedExpression> parameters, @Nullable @Symbol KType symbol) implements KExpr {}
    record Closure(Span region, List<NameAndOptType> args, @Nullable KType returnType, List<KType> interfaces, KExpr body) implements KExpr {}


}
