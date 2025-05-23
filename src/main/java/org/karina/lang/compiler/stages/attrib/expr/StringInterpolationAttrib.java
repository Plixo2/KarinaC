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
                        Log.record("Variable: '" + expressionComponent.name() + "'");
                    }
                }
            }
            Log.end("String Interpolation");
        }

        var newComponents = ImmutableList.<StringComponent>builder();
        for (var component : expr.components()) {
            switch (component) {
                case StringComponent.StringLiteralComponent stringLiteralComponent -> {
                    newComponents.add(stringLiteralComponent);
                }
                case StringComponent.ExpressionComponent(var region, var name, _) -> {

                    var variable = ctx.variables().get(name);
                    if (variable == null) {
                        var available = new HashSet<>(ctx.variables().names());
                        Log.attribError(new AttribError.UnknownIdentifier(region, name, available));
                        throw new Log.KarinaException();
                    }
                    variable.incrementUsageCount();
                    var literal = new KExpr.Literal(
                            region,
                            variable.name(),
                            new LiteralSymbol.VariableReference(region, variable)
                    );
                    //cannot be void, since a variable can never be void
                    newComponents.add(
                            new StringComponent.ExpressionComponent(
                                    region,
                                    name,
                                    literal
                            )
                    );

                }
            }
        }

        return of(ctx, new KExpr.StringInterpolation(
                expr.region(),
                newComponents.build()
        ));
    }


}
