package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;

public class ExprAttribution {

    static AttribExpr attribExpr(@Nullable KType hint, AttributionContext ctx, KExpr expr) {
        return switch (expr) {
            case KExpr.Assignment assignment -> attribAssignment(hint, ctx, assignment);
            case KExpr.Binary binary -> attribBinary(hint, ctx, binary);
            case KExpr.Block block -> attribBlock(hint, ctx, block);
            case KExpr.Boolean aBoolean -> attribBoolean(hint, ctx, aBoolean);
            case KExpr.Branch branch -> attribBranch(hint, ctx, branch);
            case KExpr.Break aBreak -> attribBreak(hint, ctx, aBreak);
            case KExpr.Call call -> attribCall(hint, ctx, call);
            case KExpr.Cast cast -> attribCast(hint, ctx, cast);
            case KExpr.Closure closure -> attribClosure(hint, ctx, closure);
            case KExpr.Continue aContinue -> attribContinue(hint, ctx, aContinue);
            case KExpr.CreateArray createArray -> attribCreateArray(hint, ctx, createArray);
            case KExpr.CreateObject createObject -> attribCreateObject(hint, ctx, createObject);
            case KExpr.For aFor -> attribFor(hint, ctx, aFor);
            case KExpr.GetArrayElement getArrayElement -> attribGetArrayElement(hint, ctx, getArrayElement);
            case KExpr.GetMember getMember -> attribGetMember(hint, ctx, getMember);
            case KExpr.InstanceOf instanceOf -> attribInstanceOf(hint, ctx, instanceOf);
            case KExpr.IsInstance isInstance -> attribIsInstance(hint, ctx, isInstance);
            case KExpr.Literal literal -> attribLiteral(hint, ctx, literal);
            case KExpr.Match match -> attribMatch(hint, ctx, match);
            case KExpr.Number number -> attribNumber(hint, ctx, number);
            case KExpr.Return aReturn -> attribReturn(hint, ctx, aReturn);
            case KExpr.Self self -> attribSelf(hint, ctx, self);
            case KExpr.StringExpr stringExpr -> attribStringExpr(hint, ctx, stringExpr);
            case KExpr.Unary unary -> attribUnary(hint, ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> attribVariableDefinition(hint, ctx, variableDefinition);
            case KExpr.While aWhile -> attribWhile(hint, ctx, aWhile);
        };
    }

    private static AttribExpr attribAssignment(@Nullable KType hint, AttributionContext ctx, KExpr.Assignment expr) { return of(ctx, expr);}
    private static AttribExpr attribBinary(@Nullable KType hint, AttributionContext ctx, KExpr.Binary expr) { return of(ctx, expr);}
    private static AttribExpr attribBlock(@Nullable KType hint, AttributionContext ctx, KExpr.Block expr) { return of(ctx, expr);}
    private static AttribExpr attribBoolean(@Nullable KType hint, AttributionContext ctx, KExpr.Boolean expr) { return of(ctx, expr);}
    private static AttribExpr attribBranch(@Nullable KType hint, AttributionContext ctx, KExpr.Branch expr) { return of(ctx, expr);}
    private static AttribExpr attribBreak(@Nullable KType hint, AttributionContext ctx, KExpr.Break expr) { return of(ctx, expr);}
    private static AttribExpr attribCall(@Nullable KType hint, AttributionContext ctx, KExpr.Call expr) { return of(ctx, expr); }
    private static AttribExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) { return of(ctx, expr);}
    private static AttribExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) { return of(ctx, expr);}
    private static AttribExpr attribContinue(@Nullable KType hint, AttributionContext ctx, KExpr.Continue expr) { return of(ctx, expr);}
    private static AttribExpr attribCreateArray(@Nullable KType hint, AttributionContext ctx, KExpr.CreateArray expr) { return of(ctx, expr);}
    private static AttribExpr attribCreateObject(@Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) { return of(ctx, expr);}
    private static AttribExpr attribFor(@Nullable KType hint, AttributionContext ctx, KExpr.For expr) { return of(ctx, expr);}
    private static AttribExpr attribGetArrayElement(@Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) { return of(ctx, expr);}
    private static AttribExpr attribGetMember(@Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) { return of(ctx, expr);}
    private static AttribExpr attribInstanceOf(@Nullable KType hint, AttributionContext ctx, KExpr.InstanceOf expr) { return of(ctx, expr);}
    private static AttribExpr attribIsInstance(@Nullable KType hint, AttributionContext ctx, KExpr.IsInstance expr) { return of(ctx, expr);}
    private static AttribExpr attribLiteral(@Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) { return of(ctx, expr);}
    private static AttribExpr attribMatch(@Nullable KType hint, AttributionContext ctx, KExpr.Match expr) { return of(ctx, expr);}
    private static AttribExpr attribNumber(@Nullable KType hint, AttributionContext ctx, KExpr.Number expr) { return of(ctx, expr);}
    private static AttribExpr attribReturn(@Nullable KType hint, AttributionContext ctx, KExpr.Return expr) { return of(ctx, expr);}
    private static AttribExpr attribSelf(@Nullable KType hint, AttributionContext ctx, KExpr.Self expr) {
        if (ctx.selfType() == null) {
            Log.attribError(new AttribError.UnqualifiedSelf(
                    expr.region(), ctx.methodRegion()
            ));
            throw new Log.KarinaException();
        }
        return of(ctx, new KExpr.Self(
                expr.region(),
                ctx.selfType()
        ));
    }
    private static AttribExpr attribStringExpr(@Nullable KType hint, AttributionContext ctx, KExpr.StringExpr expr) { return of(ctx, expr);}
    private static AttribExpr attribUnary(@Nullable KType hint, AttributionContext ctx, KExpr.Unary expr) { return of(ctx, expr);}
    private static AttribExpr attribVariableDefinition(@Nullable KType hint, AttributionContext ctx, KExpr.VariableDefinition expr) { return of(ctx, expr);}
    private static AttribExpr attribWhile(@Nullable KType hint, AttributionContext ctx, KExpr.While expr) { return of(ctx, expr);}


    public record AttribExpr(KExpr expr, AttributionContext ctx) {}
    private static KExpr expr(AttribExpr expr) {
        return expr.expr;
    }
    private static AttributionContext ctx(AttribExpr expr) {
        return expr.ctx;
    }
    private static AttribExpr of(AttributionContext ctx, KExpr expr) {
        return new AttribExpr(expr, ctx);
    }


}
