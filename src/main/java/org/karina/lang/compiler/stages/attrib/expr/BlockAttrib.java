package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;

public class BlockAttrib extends AttributionExpr {
    public static AttributionExpr attribBlock(
            @Nullable KType hint, AttributionContext ctx, KExpr.Block expr) {

        var newExpressions = new ArrayList<KExpr>();
        var expressions = expr.expressions();
        var subCtx = ctx;
        for (var i = 0; i < expressions.size(); i++) {
            var subExpr = expressions.get(i);
            var isLast = i == expressions.size() - 1;
            var hintLine = isLast ? hint : null;
            var newExpr = attribExpr(hintLine, subCtx, subExpr);
            if (!isLast && doesReturn(newExpr.expr())) {
                Log.temp(expressions.get(i + 1).region(), "Unreachable code");
                throw new Log.KarinaException();
            }

            subCtx = newExpr.ctx();
            newExpressions.add(newExpr.expr());
        }
        KType returningType;
        if (newExpressions.isEmpty()) {
            returningType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        } else {
            returningType = newExpressions.getLast().type();
        }
        return of(ctx, new KExpr.Block(
                expr.region(),
                newExpressions,
                returningType
        ));

    }
}
