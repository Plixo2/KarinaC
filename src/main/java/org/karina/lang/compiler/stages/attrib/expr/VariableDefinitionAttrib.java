package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class VariableDefinitionAttrib extends AttributionExpr {
    public static AttributionExpr attribVariableDefinition(
            @Nullable KType hint, AttributionContext ctx, KExpr.VariableDefinition expr) {

        var valueExpr = attribExpr(expr.hint(), ctx, expr.value()).expr();

        var varTypeHint = expr.hint();
        if (varTypeHint == null) {
            varTypeHint = valueExpr.type();
        } else {
            ctx.assign(valueExpr.region(), varTypeHint, valueExpr.type());
        }

        var symbol = new Variable(
                expr.name().region(),
                expr.name().value(),
                varTypeHint,
                true,
                false
        );

        return of(ctx.addVariable(symbol), new KExpr.VariableDefinition(
                expr.region(),
                expr.name(),
                expr.hint(),
                valueExpr,
                symbol
        ));

    }
}
