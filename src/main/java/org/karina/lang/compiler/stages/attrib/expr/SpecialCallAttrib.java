package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.InvocationType;

import java.lang.reflect.Modifier;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class SpecialCallAttrib {

    public static AttributionExpr attribSpecialCall(@Nullable KType hint, AttributionContext ctx, KExpr.SpecialCall expr) {


        switch (expr.invocationType()) {
            case InvocationType.NewInit newInit -> {

            }
            case InvocationType.SpecialInvoke specialInvoke -> {
                //we only can invoke special methods (super methods) in a non-static context
                var isStatic = true;
                if (ctx.owningMethod() != null) {
                    isStatic = Modifier.isStatic(ctx.owningMethod().modifiers());
                }

//                if (ctx.selfVariable() == null) {
//                    Log.attribError(new AttribError.UnqualifiedSelf(
//                            expr.region(), expr.region()
//                    ));
//                    throw new Log.KarinaException();
//                }
//                ctx.selfVariable().incrementUsageCount();

                if (isStatic) {
                    Log.attribError(new AttribError.UnqualifiedSelf(
                            expr.region(), expr.region()
                    ));
                    throw new Log.KarinaException();
                }
            }
            case InvocationType.Unknown unknown -> {}
        };

        return of(ctx, new KExpr.SpecialCall(
                expr.region(),
                expr.invocationType()
        ));
    }

}