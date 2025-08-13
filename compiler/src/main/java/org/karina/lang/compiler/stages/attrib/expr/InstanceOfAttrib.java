package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Types;

import java.util.ArrayList;
import java.util.Objects;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class InstanceOfAttrib  {
    public static AttributionExpr attribInstanceOf(@Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) {

        var leftAttrib = attribExpr(null, ctx, expr.left()).expr();
        var inner = attribInner(ctx, expr.region(), expr.isType());

        if (!Types.hasIdentity(leftAttrib.type())) {
            Log.error(ctx, new AttribError.NotSupportedType(
                    leftAttrib.region(),
                    leftAttrib.type()
            ));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.IsInstanceOf(
                expr.region(),
                leftAttrib,
                inner
        ));
    }

    public static KType attribInner(AttributionContext ctx, Region region, KType isType) {

        var isTypeAttrib = isType.unpack();

        //we replace non annotated generics with KType.ROOT, this is technically not necessary
        if (isType instanceof KType.ClassType classType) {
            var classModel = ctx.model().getClass(classType.pointer());
            var newGenerics = new ArrayList<KType>();
            for (var i = 0; i < classModel.generics().size(); i++) {
                newGenerics.add(KType.ROOT);
            }
            isTypeAttrib = classType.pointer().implement(newGenerics);
        } else {
            Log.error(ctx, new AttribError.NotAClass(
                    region,
                    isType
            ));
            throw new Log.KarinaException();
        }

        return isTypeAttrib;
    }


}
