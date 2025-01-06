package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.symbols.LiteralSymbol;

import java.util.HashSet;

public class LiteralAttrib extends AttribExpr {
    public static AttribExpr attribLiteral(
            @Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) {

        LiteralSymbol symbol;
        if (ctx.variables().contains(expr.name())) {
            var variable = ctx.variables().get(expr.name());
            assert variable != null;
            variable.incrementUsageCount();
            symbol = new LiteralSymbol.VariableReference(expr.region(), variable);
        } else {
            var function = ctx.table().getFunction(expr.name());
            if (function != null) {
                symbol = new LiteralSymbol.StaticFunction(expr.region(), function.path());
            } else {
                var variableName = ctx.variables().names();
                var functionNames = ctx.table().availableFunctionNames();
                var available = new HashSet<>(variableName);
                available.addAll(functionNames);
                Log.attribError(new AttribError.UnknownIdentifier(expr.region(), expr.name(), available));
                throw new Log.KarinaException();
            }
        }
        return of(ctx, new KExpr.Literal(
                expr.region(),
                expr.name(),
                symbol
        ));

    }
}
