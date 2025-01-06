package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.Objects;

public class GetArrayElementAttrib extends AttribExpr {
    public static AttribExpr attribGetArrayElement(
            @Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) {

        KType arrayHint;
        arrayHint = Objects.requireNonNullElse(
                hint,
                new KType.PrimitiveType.Resolvable(expr.region())
        );
        arrayHint = new KType.ArrayType(
                expr.left().region(),
                arrayHint
        );

        var left = attribExpr(arrayHint, ctx, expr.left()).expr();
        if (!(left.type() instanceof KType.ArrayType arrayType)) {
            Log.attribError(new AttribError.NotAArray(left.region(), arrayHint));
            throw new Log.KarinaException();
        }

        var index = attribExpr(new KType.PrimitiveType.IntType(expr.index().region()), ctx, expr.index()).expr();
        ctx.assign(index.region(), new KType.PrimitiveType.IntType(expr.index().region()), index.type());

        return of(ctx, new KExpr.GetArrayElement(
                expr.region(),
                left,
                index,
                arrayType.elementType()
        ));
    }

}
