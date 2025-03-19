package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class InstanceOfAttrib  {
    public static AttributionExpr attribInstanceOf(@Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) {

        var leftAttrib = attribExpr(null, ctx, expr.left()).expr();
        var inner = attribInner(ctx, expr.region(), expr.isType());

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
            isTypeAttrib = new KType.ClassType(classType.pointer(), newGenerics);
        } else {
            Log.attribError(new AttribError.NotAClass(
                    region,
                    isType
            ));
            throw new Log.KarinaException();
        }

        return isTypeAttrib;
    }


}
