package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.Objects;

public class GetArrayElementAttrib extends AttributionExpr {
    public static AttributionExpr attribGetArrayElement(
            @Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) {

        KType arrayHint;
        arrayHint = Objects.requireNonNullElse(
                hint,
                new KType.PrimitiveType.Resolvable()
        );
        arrayHint = new KType.ArrayType(
                arrayHint
        );

        var left = attribExpr(arrayHint, ctx, expr.left()).expr();
        if (!(left.type() instanceof KType.ArrayType arrayType)) {
            Log.attribError(new AttribError.NotAArray(left.region(), arrayHint));
            throw new Log.KarinaException();
        }

        var index = attribExpr(new KType.PrimitiveType(KType.KPrimitive.INT), ctx, expr.index()).expr();
        ctx.assign(index.region(), new KType.PrimitiveType(KType.KPrimitive.INT), index.type());

        return of(ctx, new KExpr.GetArrayElement(
                expr.region(),
                left,
                index,
                arrayType.elementType()
        ));
    }

}
