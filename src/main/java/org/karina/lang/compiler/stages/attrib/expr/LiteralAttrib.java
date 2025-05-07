package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;

import java.util.HashSet;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class LiteralAttrib  {

    public static AttributionExpr attribLiteral(@Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) {

        LiteralSymbol symbol;
        var variable = ctx.variables().get(expr.name());
        var staticClass = ctx.table().getClass(expr.name());
        var staticMethodCollection = ctx.table().getStaticMethod(expr.name());
        var staticField = ctx.table().getStaticField(expr.name());

        if (expr.name().equals("_")) {
            Log.invalidName(expr.region(), expr.name());
        }

        if (variable != null) {
            variable.incrementUsageCount();
            symbol = new LiteralSymbol.VariableReference(expr.region(), variable);
        } else if (staticClass != null) {
            var classModel = ctx.model().getClass(staticClass);
            var referenceClass = Types.erasedClass(classModel);
            symbol = new LiteralSymbol.StaticClassReference(expr.region(), staticClass, referenceClass, false);
        } else if (staticMethodCollection != null) {
            symbol = new LiteralSymbol.StaticMethodReference(expr.region(), staticMethodCollection);
        } else if (staticField != null) {
            var type = ctx.model().getField(staticField).type();
            symbol = new LiteralSymbol.StaticFieldReference(expr.region(), staticField, type);
        } else {
            var available = new HashSet<>(ctx.variables().names());
            available.addAll(ctx.table().availableItemNames());
            Log.attribError(new AttribError.UnknownIdentifier(expr.region(), expr.name(), available));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Literal(
                expr.region(),
                expr.name(),
                symbol
        ));
    }
}
