package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.UnaryOperator;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.symbols.UnaryOperatorSymbol;

public class UnaryAttrib extends AttribExpr {
    public static AttribExpr attribUnary(
            @Nullable KType hint, AttributionContext ctx, KExpr.Unary expr) {

        if (expr.operator().value() == UnaryOperator.NOT) {
            hint = new KType.PrimitiveType.BoolType(expr.region());
        }

        var value = attribExpr(hint, ctx, expr.value()).expr();

        if (expr.operator().value() == UnaryOperator.NOT) {
            ctx.assign(expr.region(), new KType.PrimitiveType.BoolType(expr.region()), value.type());
        } else if (!(value.type() instanceof KType.PrimitiveType) && hint != null) {
            //we try to fill in extra information when using resolvable.
            ctx.assign(expr.region(), hint, value.type());
        }

        if (!(value.type() instanceof KType.PrimitiveType primitive)) {
            Log.attribError(new AttribError.NotSupportedType(value.region(), value.type()));
            throw new Log.KarinaException();
        }
        var symbol = UnaryOperatorSymbol.fromOperator(primitive.primitive(), expr.operator());
        if (symbol == null) {
            Log.attribError(new AttribError.NotSupportedType(value.region(), value.type()));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Unary(
                expr.region(),
                expr.operator(),
                value,
                symbol
        ));
    }
}
