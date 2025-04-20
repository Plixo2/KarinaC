package org.karina.lang.compiler.objects;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.symbols.*;
import org.karina.lang.compiler.utils.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public sealed interface KExpr {
    Region region();

    /**
     * Only allowed on expressions from {@link AttributionExpr#attribExpr}.
     * @return The type of this expression with all generics already resolved.
     */
    default KType type() {
        return Objects.requireNonNull(Expressions.getType(this)).unpack();
    }

    /**
     * Only allowed on expressions from {@link AttributionExpr#attribExpr}.
     */
    default boolean doesReturn() {
        return Expressions.doesReturn(this);
    }


    record Block(Region region, List<KExpr> expressions, @Nullable @Symbol KType symbol, @Symbol boolean doesReturn) implements KExpr {}
    record VariableDefinition(Region region, RegionOf<String> name, @Nullable KType varHint, KExpr value, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Branch(Region region, KExpr condition, KExpr thenArm, @Nullable ElsePart elseArm, @Nullable BranchPattern branchPattern, @Nullable @Symbol BranchYieldSymbol symbol) implements KExpr {}
    record While(Region region, KExpr condition, KExpr body) implements KExpr {}
    record For(Region region, NameAndOptType varPart, KExpr iter, KExpr body, @Nullable @Symbol IteratorTypeSymbol symbol) implements KExpr {}
    record Return(Region region, @Nullable KExpr value, @Nullable @Symbol KType returnType) implements KExpr {}
    record Break(Region region) implements KExpr {}
    record Continue(Region region) implements KExpr {}
    record Binary(Region region, KExpr left, RegionOf<BinaryOperator> operator, KExpr right, @Nullable @Symbol BinOperatorSymbol symbol) implements KExpr {}
    record Unary(Region region, RegionOf<UnaryOperator> operator, KExpr value, @Nullable @Symbol UnaryOperatorSymbol symbol) implements KExpr {}
    record IsInstanceOf(Region region, KExpr left, KType isType) implements KExpr {}
    record GetMember(Region region, KExpr left, RegionOf<String> name, boolean isNextACall, @Nullable @Symbol MemberSymbol symbol) implements KExpr {}
    record Call(Region region, KExpr left, List<KType> generics, List<KExpr> arguments, @Nullable @Symbol CallSymbol symbol) implements KExpr {}
    record GetArrayElement(Region region, KExpr left, KExpr index, @Nullable @Symbol KType elementType) implements KExpr {}
    record Cast(Region region, KExpr expression, CastTo cast, @Nullable @Symbol CastSymbol symbol) implements KExpr {}
    record Assignment(Region region, KExpr left, KExpr right, @Nullable @Symbol AssignmentSymbol symbol) implements KExpr {}
    record CreateArray(Region region, @Nullable KType hint, List<KExpr> elements, @Nullable @Symbol KType.ArrayType symbol) implements KExpr {}
    record Number(Region region, BigDecimal number, boolean decimalAnnotated, @Nullable @Symbol NumberSymbol symbol) implements KExpr {}
    record Boolean(Region region, boolean value) implements KExpr {}
    record Literal(Region region, String name, @Nullable @Symbol LiteralSymbol symbol) implements KExpr {}
    record StaticPath(Region region, ObjectPath path, @Nullable ClassPointer importedPointer) implements KExpr {}
    record Self(Region region, @Nullable @Symbol Variable symbol) implements KExpr {}
    record SpecialCall(Region region, InvocationType invocationType) implements KExpr {}
    record StringExpr(Region region, String value, boolean isChar) implements KExpr {}
    record StringInterpolation(Region region, ImmutableList<StringComponent> components) implements KExpr {}
    record Match(Region region, KExpr value, List<MatchPattern> cases) implements KExpr {}
    record CreateObject(Region region, KType createType, List<NamedExpression> parameters) implements KExpr {}
    record Closure(Region region, List<NameAndOptType> args, @Nullable KType returnType, List<? extends KType> interfaces, KExpr body, @Nullable @Symbol ClosureSymbol symbol) implements KExpr {}
    record Unwrap(Region region, KExpr left, @Nullable @Symbol UnwrapSymbol symbol) implements KExpr {}
    record Throw(Region region, KExpr value) implements KExpr {}


}
