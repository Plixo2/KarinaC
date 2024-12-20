package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;

import java.math.BigDecimal;
import java.util.List;

public sealed interface KExpr {
    Span region();

//    KType type();

    record Block(Span region, List<KExpr> expressions) implements KExpr {}
    record VariableDefinition(Span region, SpanOf<String> name, @Nullable KType hint, KExpr value) implements KExpr {}
    record Branch(Span region, KExpr condition, KExpr thenArm, @Nullable KExpr elseArm, @Nullable SynatxObject.BranchPattern branchPattern) implements KExpr {}
    record While(Span region, KExpr condition, KExpr body) implements KExpr {}
    record For(Span region, SpanOf<String> name , KExpr iter, KExpr body) implements KExpr {}
    record Return(Span region, @Nullable KExpr value) implements KExpr {}
    record Break(Span region) implements KExpr {}
    record Continue(Span region) implements KExpr {}
    record Binary(Span region, KExpr left, SpanOf<SynatxObject.BinaryOperator> operator, KExpr right) implements KExpr {}
    record Unary(Span region, SpanOf<SynatxObject.UnaryOperator> operator, KExpr value) implements KExpr {}
    record InstanceOf(Span region, KExpr left, KType type) implements KExpr {}
    record GetMember(Span region, KExpr left, SpanOf<String> name) implements KExpr {}
    record Call(Span region, KExpr left, List<KType> generics, List<KExpr> arguments) implements KExpr {}
    record GetArrayElement(Span region, KExpr left, KExpr index) implements KExpr {}
    record IsInstance(Span region, KExpr left, KType type) implements KExpr {}
    record Cast(Span region, KExpr expression, KType type) implements KExpr {}
    record Assignment(Span region, KExpr left, KExpr right) implements KExpr {}
    record CreateArray(Span region, @Nullable KType hint, List<KExpr> elements) implements KExpr {}
    record Number(Span region, BigDecimal number) implements KExpr {}
    record Boolean(Span region, boolean value) implements KExpr {}
    record Literal(Span region, String name) implements KExpr {}
    record Self(Span region, @Nullable KType symbol) implements KExpr {}
    record StringExpr(Span region, String value) implements KExpr {}
    record Match(Span region, KExpr value, List<SynatxObject.MatchPattern> cases) implements KExpr {}
    record CreateObject(Span region, KType type, List<SynatxObject.NamedExpression> parameters) implements KExpr {}
    record Closure(Span region, List<SynatxObject.NameAndOptType> args, @Nullable KType returnType, List<KType> interfaces, KExpr body) implements KExpr {}
}
