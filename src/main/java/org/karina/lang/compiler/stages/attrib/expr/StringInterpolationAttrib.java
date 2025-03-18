package org.karina.lang.compiler.stages.attrib.expr;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.StringComponent;
import org.karina.lang.compiler.utils.Variable;

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
                    case StringComponent.VariableComponent variableComponent -> {
                        Log.record("Variable: '" + variableComponent.name() + "'");
                    }
                }
            }
            Log.end("String Interpolation");
        }

        var newComponents = ImmutableList.<StringComponent>builder();
//
        for (var component : expr.components()) {
            switch (component) {
                case StringComponent.StringLiteralComponent stringLiteralComponent -> {
                    newComponents.add(stringLiteralComponent);
                }
                case StringComponent.VariableComponent(var region, var name, _) -> {

                    var variable = ctx.variables().get(name);
                    if (variable == null) {
                        var available = new HashSet<>(ctx.variables().names());
                        Log.attribError(new AttribError.UnknownIdentifier(region, name, available));
                        throw new Log.KarinaException();
                    }
                    newComponents.add(
                            new StringComponent.VariableComponent(
                                    region,
                                    name,
                                    variable
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
