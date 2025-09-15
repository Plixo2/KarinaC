package org.karina.lang.compiler.stages.attrib;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.expr.*;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.utils.logging.Logging;


@Getter
@Accessors(fluent = true)
public final class AttributionExpr {

    KExpr expr;
    AttributionContext ctx;

    private AttributionExpr() {}

    public static AttributionExpr of(AttributionContext ctx, KExpr expr) {
        var attribExpr = new AttributionExpr();
        attribExpr.expr = expr;
        attribExpr.ctx = ctx;
        return attribExpr;
    }

    /**
     * Only call this method once per expression, since {@link KType.Resolvable#tryResolve}
     * and {@link Variable#incrementUsageCount} are mutable.
     * @param hint Optional hint. When provided, and this function returns a expression with that type,
     *             is HAS to be valid. When the expressions is of another type, it still can be valid.
     *             This is checked by the caller of this function and will not be enforced otherwise.
     *             The hint is only used for type inference.
     * @return A typed attributed expression. Now every expression (and all parts of it) should have the 'symbol' field set.
     *         These fields are annotated with @Nullable @Symbol, so they should never be null.
     *         Calling now {@link KExpr#type} and {@link KExpr#doesReturn} is valid.
     */
    public static AttributionExpr attribExpr(@Nullable KType hint, AttributionContext ctx, KExpr expr) {
        if (hint != null) {
            hint = hint.unpack();
        }
        if (ctx.log(Logging.Expression.class)) {
            ctx.tag("Expr", expr.getClass().getSimpleName());
        }
        return switch (expr) {
            case KExpr.Assignment assignment -> AssignmentAttrib.attribAssignment(hint, ctx, assignment);
            case KExpr.Binary binary -> BinaryAttrib.attribBinary(hint, ctx, binary);
            case KExpr.Block block -> BlockAttrib.attribBlock(hint, ctx, block);
            case KExpr.Boolean aBoolean -> BooleanAttrib.attribBoolean(hint, ctx, aBoolean);
            case KExpr.Branch branch -> BranchAttrib.attribBranch(hint, ctx, branch);
            case KExpr.Break aBreak -> BreakAttrib.attribBreak(hint, ctx, aBreak);
            case KExpr.Call call -> CallAttrib.attribCall(hint, ctx, call);
            case KExpr.Cast cast -> CastAttrib.attribCast(hint, ctx, cast);
            case KExpr.Closure closure -> ClosureAttrib.attribClosure(hint, ctx, closure);
            case KExpr.Continue aContinue -> ContinueAttrib.attribContinue(hint, ctx, aContinue);
            case KExpr.CreateArray createArray -> CreateArrayAttrib.attribCreateArray(hint, ctx, createArray);
            case KExpr.CreateObject createObject -> CreateObjectAttrib.attribCreateObject(hint, ctx, createObject);
            case KExpr.For aFor -> ForAttrib.attribFor(hint, ctx, aFor);
            case KExpr.GetArrayElement getArrayElement -> GetArrayElementAttrib.attribGetArrayElement(hint, ctx, getArrayElement);
            case KExpr.GetMember getMember -> GetMemberAttrib.attribGetMember(hint, ctx, getMember);
            case KExpr.IsInstanceOf isInstanceOf -> InstanceOfAttrib.attribInstanceOf(hint, ctx, isInstanceOf);
            case KExpr.Literal literal -> LiteralAttrib.attribLiteral(hint, ctx, literal);
            case KExpr.Match match -> MatchAttrib.attribMatch(hint, ctx, match);
            case KExpr.Number number -> NumberAttrib.attribNumber(hint, ctx, number);
            case KExpr.Return aReturn -> ReturnAttrib.attribReturn(hint, ctx, aReturn);
            case KExpr.Self self -> SelfAttrib.attribSelf(hint, ctx, self);
            case KExpr.StringExpr stringExpr -> StringAttrib.attribStringExpr(hint, ctx, stringExpr);
            case KExpr.StringInterpolation stringExpr -> StringInterpolationAttrib.attribStringExpr(hint, ctx, stringExpr);
            case KExpr.Unary unary -> UnaryAttrib.attribUnary(hint, ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> VariableDefinitionAttrib.attribVariableDefinition(hint, ctx, variableDefinition);
            case KExpr.UsingVariableDefinition usingVariableDefinition -> UsingVariableDefinitionAttrib.attribUsingVariableDefinition(hint, ctx, usingVariableDefinition);
            case KExpr.While aWhile -> WhileAttrib.attribWhile(hint, ctx, aWhile);
            case KExpr.Throw aThrow -> ThrowAttrib.attribThrow(hint, ctx, aThrow);
            case KExpr.Unwrap unwrap -> UnwrapAttrib.attribUnwrap(hint, ctx, unwrap);
            case KExpr.SpecialCall aSuper -> SpecialCallAttrib.attribSpecialCall(hint, ctx, aSuper);
            case KExpr.StaticPath staticPath -> StaticPathAttrib.attribStaticPath(hint, ctx, staticPath);
        };
    }

}
