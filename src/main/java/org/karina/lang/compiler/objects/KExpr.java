package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;

import java.math.BigDecimal;
import java.util.List;

public sealed interface KExpr permits KExpr.ImportedExpr, KExpr.LinkedExpr, KExpr.TypedExpr {
    Span region();

    /**
     * First representation of the AST, before and after import resolution.
     */
    sealed interface ImportedExpr extends KExpr {}

    non-sealed interface LinkedExpr extends KExpr {}

    non-sealed interface TypedExpr extends KExpr {
        KType type();
    }

    record Block(Span region, List<KExpr> expressions) implements ImportedExpr {}
    record VariableDefinition(Span region, SpanOf<String> name, @Nullable KType hint, KExpr value) implements ImportedExpr {}
    record Branch(Span region, KExpr condition, KExpr thenArm, @Nullable KExpr elseArm, @Nullable SynatxObject.BranchPattern branchPattern) implements ImportedExpr {}
    record While(Span region, KExpr condition, KExpr body) implements ImportedExpr {}
    record For(Span region, SpanOf<String> name , KExpr iter, KExpr body) implements ImportedExpr {}
    record Return(Span region, @Nullable KExpr value) implements ImportedExpr {}
    record Break(Span region) implements ImportedExpr {}
    record Continue(Span region) implements ImportedExpr {}
    record Binary(Span region, KExpr left, SpanOf<SynatxObject.BinaryOperator> operator, KExpr right) implements ImportedExpr {}
    record Unary(Span region, SpanOf<SynatxObject.UnaryOperator> operator, KExpr value) implements ImportedExpr {}
    record InstanceOf(Span region, KExpr left, KType type) implements ImportedExpr {}
    record getMember(Span region, KExpr left, SpanOf<String> name) implements ImportedExpr {}
    record Call(Span region, KExpr left, List<KType> generics, List<KExpr> arguments) implements ImportedExpr {}
    record getArrayElement(Span region, KExpr left, KExpr index) implements ImportedExpr {}
    record IsInstance(Span region, KExpr left, KType type) implements ImportedExpr {}
    record Cast(Span region, KExpr expression, KType type) implements ImportedExpr {}
    record Assignment(Span region, KExpr left, KExpr right) implements ImportedExpr {}
    record CreateArray(Span region, @Nullable KType hint, List<KExpr> elements) implements ImportedExpr {}
    record Number(Span region, BigDecimal number) implements ImportedExpr {}
    record Boolean(Span region, boolean value) implements ImportedExpr {}
    record Literal(Span region, String name) implements ImportedExpr {}
    record Self(Span region) implements ImportedExpr {}
    record StringExpr(Span region, String value) implements ImportedExpr {}
    record Match(Span region, KExpr value, List<SynatxObject.MatchPattern> cases) implements ImportedExpr {}
    record CreateObject(Span region, KType type, List<SynatxObject.NamedExpression> parameters) implements ImportedExpr {}
    record Closure(Span region, List<SynatxObject.NameAndOptType> args, @Nullable KType returnType, List<KType> interfaces, KExpr body) implements ImportedExpr {}

    record LinkedSelf(Span region, KType type) implements LinkedExpr {
        @Override
        public KType type() {
            return this.type;
        }
    }
}
