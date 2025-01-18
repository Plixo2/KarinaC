package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class WhileAttrib extends AttributionExpr {

    public static AttributionExpr attribWhile(
            @Nullable KType hint,
            AttributionContext ctx,
            KExpr.While expr)
    {
        var boolType = new KType.PrimitiveType.BoolType(expr.condition().region());
        var condition = attribExpr(boolType, ctx, expr.condition()).expr();
        ctx.assign(expr.condition().region(), boolType, condition.type());

        var body = attribExpr(null, ctx.setInLoop(true), expr.body()).expr();

        return of(ctx, new KExpr.While(
                expr.region(),
                condition,
                body
        ));
    }
}
