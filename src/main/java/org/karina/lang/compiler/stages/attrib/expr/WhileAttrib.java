package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class WhileAttrib  {

    public static AttributionExpr attribWhile(
            @Nullable KType hint,
            AttributionContext ctx,
            KExpr.While expr)
    {
        var boolType = KType.BOOL;
        var condition = attribExpr(boolType, ctx, expr.condition()).expr();
        condition = ctx.makeAssignment(expr.condition().region(), boolType, condition);

        var body = attribExpr(null, ctx.setInLoop(true), expr.body()).expr();

        return of(ctx, new KExpr.While(
                expr.region(),
                condition,
                body
        ));
    }
}
