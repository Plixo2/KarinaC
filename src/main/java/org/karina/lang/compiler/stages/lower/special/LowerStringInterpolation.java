package org.karina.lang.compiler.stages.lower.special;

import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.lower.LowerExpr;
import org.karina.lang.compiler.stages.lower.LoweringContext;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.StringComponent;
import org.karina.lang.compiler.utils.symbols.CallSymbol;

import java.util.List;

///
/// Converts a String interpolation like 'Hello $name' into calls to the StringInterpolation class.
/// 'Hello $name !' is converted to:
/// ```
/// StringInterpolation.create().appendLiteral("Hello ").appendExpression(name).appendLiteral(" !").toString()
/// ```
///
/// Signature:
///     `StringInterpolation(Region region, ImmutableList<StringComponent> components)`
public class LowerStringInterpolation {
    private final KExpr.StringInterpolation interpolation;

    public LowerStringInterpolation(KExpr.StringInterpolation interpolation) {
        this.interpolation = interpolation;
    }
    public KExpr lower(LoweringContext ctx) {
        var region = this.interpolation.region();


        KExpr left = new KExpr.Call(
                region,
                new KExpr.Boolean(region, false), //dummy, not used
                List.of(),
                List.of(),
                new CallSymbol.CallStatic(
                        MethodPointer.of(
                                region,
                                KType.STRING_INTERPOLATION.pointer(),
                                "create",
                                Signature.emptyArgs(KType.STRING_INTERPOLATION)
                        ),
                        List.of(),
                        KType.STRING_INTERPOLATION,
                        false
                )
        );
        for (var component : this.interpolation.components()) {
            switch (component) {
                case StringComponent.ExpressionComponent expressionComponent -> {
                    assert expressionComponent.expression() != null;
                    var callSignature = getSignature(expressionComponent.expression());
                    left = new KExpr.Call(
                            region,
                            left,
                            List.of(),
                            List.of(expressionComponent.expression()),
                            new CallSymbol.CallVirtual(
                                    MethodPointer.of(
                                            region,
                                            KType.STRING_INTERPOLATION.pointer(),
                                            "appendExpression",
                                            callSignature,
                                            KType.STRING_INTERPOLATION
                                    ),
                                    List.of(),
                                    KType.STRING_INTERPOLATION,
                                    false
                            )
                    );
                }
                case StringComponent.StringLiteralComponent stringLiteralComponent -> {
                    var stringLiteral = new KExpr.StringExpr(
                            region,
                            stringLiteralComponent.value().replace("\\$", "$"),
                            false
                    );
                    left = new KExpr.Call(
                            region,
                            left,
                            List.of(),
                            List.of(stringLiteral),
                            new CallSymbol.CallVirtual(
                                    MethodPointer.of(
                                            region,
                                            KType.STRING_INTERPOLATION.pointer(),
                                            "appendLiteral",
                                            List.of(KType.STRING),
                                            KType.STRING_INTERPOLATION
                                    ),
                                    List.of(),
                                    KType.STRING_INTERPOLATION,
                                    false
                            )
                    );
                }
            }
        }

        var toStringCall = new KExpr.Call(
                region,
                left,
                List.of(),
                List.of(),
                new CallSymbol.CallVirtual(
                        MethodPointer.of(
                                region,
                                KType.STRING_INTERPOLATION.pointer(),
                                "toString",
                                Signature.emptyArgs(KType.STRING)
                        ),
                        List.of(),
                        KType.STRING,
                        false
                )
        );

        return LowerExpr.lower(ctx, toStringCall);
    }

    private List<KType> getSignature(KExpr expr) {
        var type = expr.type();


        //expr cannot be void, since a variable can never be void
        if (type.isPrimitive()) {
            return List.of(type);
        } else {
            return List.of(KType.ROOT);
        }
    }
}
