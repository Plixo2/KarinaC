package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class VariableDefinitionAttrib extends AttribExpr {
    public static AttribExpr attribVariableDefinition(
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
