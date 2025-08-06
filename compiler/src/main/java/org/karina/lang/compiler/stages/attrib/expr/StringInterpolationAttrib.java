package org.karina.lang.compiler.stages.attrib.expr;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.StringComponent;

import java.util.HashSet;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.attribExpr;
import static org.karina.lang.compiler.stages.attrib.AttributionExpr.of;

public class StringInterpolationAttrib {

    public static AttributionExpr attribStringExpr(
            @Nullable KType hint, AttributionContext ctx, KExpr.StringInterpolation expr) {

        if (Log.LogTypes.STRING_INTERPOLATION.isVisible()) {
            Log.begin("String Interpolation");
            for (var component : expr.components()) {
                switch (component) {
                    case StringComponent.StringLiteralComponent stringLiteralComponent -> {
                        Log.record("Literal: '" + stringLiteralComponent.value() + "'");
                    }
                    case StringComponent.ExpressionComponent expressionComponent -> {
                        Log.record("Expression: '" + expressionComponent.getClass() + "'");
                    }
                }
            }
            Log.end("String Interpolation");
        }

        ImmutableList<StringComponent> newComponents;
        try (var fork = ctx.intoContext().<StringComponent>fork()){
            for (var component : expr.components()) {
                fork.collect(subC -> switch (component) {
                    case StringComponent.StringLiteralComponent str -> str;
                    case StringComponent.ExpressionComponent(var region, var stringExpr) -> {
                        var newStringExpr = attribExpr(null, ctx.withNewContext(subC), stringExpr).expr();
                        if (newStringExpr.type().isVoid()) {
                            Log.error(ctx, new AttribError.NotSupportedType(region, KType.NONE));
                            throw new Log.KarinaException();
                        }

                        yield new StringComponent.ExpressionComponent(
                                region,
                                newStringExpr
                        );
                    }
                });
            }
            newComponents = ImmutableList.copyOf(fork.dispatch());
        }

        return of(ctx, new KExpr.StringInterpolation(
                expr.region(),
                newComponents
        ));
    }


}
