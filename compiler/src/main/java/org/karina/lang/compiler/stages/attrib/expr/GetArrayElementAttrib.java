package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.util.Objects;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class GetArrayElementAttrib  {
    public static AttributionExpr attribGetArrayElement(
            @Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) {

        KType arrayHint;
        arrayHint = Objects.requireNonNullElse(hint, new KType.PrimitiveType.Resolvable(true, false));
        arrayHint = new KType.ArrayType(arrayHint);

        var left = attribExpr(arrayHint, ctx, expr.left()).expr();
        if (!(left.type() instanceof KType.ArrayType(KType elementType))) {
            Log.error(ctx, new AttribError.NotAArray(left.region(), arrayHint));
            throw new Log.KarinaException();
        }

        var intType = KType.INT;

        var index = attribExpr(intType, ctx, expr.index()).expr();

//        Log.record("Array index of", index.type());

        index = ctx.makeAssignment(index.region(), intType, index);

//        Log.record("Array index of 2", index.type());

        return of(ctx, new KExpr.GetArrayElement(
                expr.region(),
                left,
                index,
                elementType
        ));
    }

}
