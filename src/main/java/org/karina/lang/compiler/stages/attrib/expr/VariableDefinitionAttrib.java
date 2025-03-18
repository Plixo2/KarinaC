package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class VariableDefinitionAttrib  {
    public static AttributionExpr attribVariableDefinition(
            @Nullable KType hint, AttributionContext ctx, KExpr.VariableDefinition expr) {

        var valueExpr = attribExpr(expr.hint(), ctx, expr.value()).expr();

        var varTypeHint = expr.hint();
        if (varTypeHint == null) {
            varTypeHint = valueExpr.type();
            if (varTypeHint.isVoid()) {
                Log.attribError(new AttribError.NotSupportedType(expr.region(), varTypeHint));
                throw new Log.KarinaException();
            }

        } else {
            valueExpr = ctx.makeAssignment(valueExpr.region(), varTypeHint, valueExpr);
        }
        Log.recordType(Log.LogTypes.VARIABLE,"varTypeHint = " + varTypeHint + " of " + expr.name().value());

        if (valueExpr.type().isVoid()) {
            Log.attribError(new AttribError.NotSupportedType(expr.region(), valueExpr.type()));
            throw new Log.KarinaException();
        }

        //we drop the variable if it is a placeholder
        if (expr.name().value().equals("_")) {
            return of(ctx, valueExpr);
        }

        var symbol = new Variable(
                expr.name().region(),
                expr.name().value(),
                valueExpr.type(),
                true,
                false
        );

        var addedCtx = ctx.addVariable(symbol);
        return of(addedCtx, new KExpr.VariableDefinition(
                expr.region(),
                expr.name(),
                expr.hint(),
                valueExpr,
                symbol
        ));

    }
}
