package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.attribExpr;
import static org.karina.lang.compiler.stages.attrib.AttributionExpr.of;

public class UsingVariableDefinitionAttrib {
    public static AttributionExpr attribUsingVariableDefinition(
            @Nullable KType hint, AttributionContext ctx, KExpr.UsingVariableDefinition expr) {

        var valueExpr = attribExpr(expr.varHint(), ctx, expr.value()).expr();

        var varType = expr.varHint();
        if (varType == null) {
            varType = valueExpr.type();
        } else {
            valueExpr = ctx.makeAssignment(valueExpr.region(), varType, valueExpr);
        }

        if (varType.isVoid()) {
            Log.error(ctx, new AttribError.NotSupportedType(expr.region(), varType));
            throw new Log.KarinaException();
        }

        valueExpr = ctx.makeAssignment(valueExpr.region(), KType.AUTO_CLOSEABLE, valueExpr);

        //we drop the variable if it is a placeholder
        if (expr.name().value().equals("_")) {
            return of(ctx, valueExpr);
        }

        var symbol = new Variable(
                expr.name().region(),
                expr.name().value(),
                varType,
                true,
                false
        );

        var addedCtx = ctx.addVariable(symbol);
        var block = attribExpr(hint, addedCtx, expr.block()).expr();

        return of(ctx, new KExpr.UsingVariableDefinition(
                expr.region(),
                expr.name(),
                expr.varHint(),
                valueExpr,
                block,
                symbol
        ));

    }
}
