package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

public class ForAttrib extends AttribExpr {

    public static AttribExpr attribFor(@Nullable KType hint, AttributionContext ctx, KExpr.For expr) {

        var iterHint = new KType.ArrayType(expr.name().region(), new KType.Resolvable(expr.name().region()));

        var iter = attribExpr(iterHint, ctx, expr.iter()).expr();

        if (!(iter.type() instanceof KType.ArrayType arrayType)) {
            Log.attribError(new AttribError.NotAArray(iter.region(), iter.type()));
            throw new Log.KarinaException();
        }

        var variable =
                new Variable(
                        expr.name().region(),
                        expr.name().value(),
                        arrayType.elementType(),
                false,
                false
                );

        var bodyCtx = ctx.addVariable(variable);

        var body = attribExpr(null, bodyCtx, expr.body()).expr();

        return of(ctx, new KExpr.For(
                expr.region(),
                expr.name(),
                iter,
                body,
                variable
        ));
    }

}
