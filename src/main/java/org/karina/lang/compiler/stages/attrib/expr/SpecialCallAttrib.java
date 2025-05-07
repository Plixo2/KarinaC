package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
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
                //check if specialInvoke.superType() is a supertype or interface
                var methodModel = ctx.owningMethod();
                if (methodModel == null || Modifier.isStatic(methodModel.modifiers())) {
                    Log.attribError(new AttribError.UnqualifiedSelf(
                            expr.region(), expr.region()
                    ));
                    throw new Log.KarinaException();
                }
                if (!(specialInvoke.superType() instanceof KType.ClassType classType)) {
                    Log.attribError(new AttribError.NotAClass(
                            expr.region(), specialInvoke.superType()
                    ));
                    throw new Log.KarinaException();
                }

                if (!Types.isSuperTypeOrInterface(ctx.model(), ctx.owningClass(), classType.pointer())) {
                    Log.attribError(new AttribError.NotSupportedType(expr.region(), classType));
                    throw new Log.KarinaException();
                }
            }
        };

        return of(ctx, new KExpr.SpecialCall(
                expr.region(),
                expr.invocationType()
        ));
    }



}