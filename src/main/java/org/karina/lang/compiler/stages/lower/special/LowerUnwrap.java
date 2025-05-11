package org.karina.lang.compiler.stages.lower.special;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.lower.LowerExpr;
import org.karina.lang.compiler.stages.lower.LoweringContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.*;

import java.util.List;

///
/// Convert a Option and Result type into a expression that returns when the value is None or Err
///
/// For Option, `value?` is equals to:
/// ```
/// {
///     let $some = value
///     if $some is Option::Some { ($some as Option::Some ).value() } else { return $some as Option::None }
/// }
/// ```
///
/// For Result, `value?` is equals to:
/// ```
/// {
///     let $opt = value
///     if $opt is Result::OK { ($opt as Result::OK ).value() } else { return ($opt as Result::Err) }
/// }
/// ```
///
///
/// Signature:
///     `Unwrap(Region region, KExpr left, @Nullable @Symbol UnwrapSymbol symbol)`
public class LowerUnwrap {
    private final KExpr.Unwrap unwrap;

    public LowerUnwrap(KExpr.Unwrap unwrap) {
        this.unwrap = unwrap;
    }


    private KExpr createExpression(
            Region region,
            LoweringContext ctx,
            KType yieldType,
            KType baseType,
            KType.ClassType leftType,
            KType.ClassType rightType,
            KExpr left,
            String variableBase
    ) {

        var newVariableName = variableBase + ctx.syntheticCounter().incrementAndGet();


        var variable = new Variable(
                region,
                newVariableName,
                baseType,
                false,
                false,
                true
        );
        var assignment = new KExpr.VariableDefinition(
                region,
                new RegionOf<>(region, newVariableName),
                baseType,
                left,
                variable
        );

        var variableReference = new KExpr.Literal(
                region,
                newVariableName,
                new LiteralSymbol.VariableReference(region, variable)
        );

        var condition = new KExpr.IsInstanceOf(region, variableReference, leftType);

        var valueMethodPointer = MethodPointer.of(
                region,
                leftType.pointer(),
                "value",
                Signature.emptyArgs(KType.ROOT)
        );


        var thenArm = new KExpr.Call(
                region,
                new KExpr.Cast(
                        region,
                        variableReference,
                        new CastTo.AutoCast(), //doesnt matter
                        new CastSymbol.UpCast(
                                baseType,
                                leftType
                        )
                ),
                List.of(),
                List.of(),
                new CallSymbol.CallVirtual(
                        valueMethodPointer,
                        List.of(),
                        yieldType,
                        false
                )
        );

        KExpr elseReturning = new KExpr.Cast(
                region,
                variableReference,
                new CastTo.AutoCast(), //doesnt matter
                new CastSymbol.UpCast(
                        baseType,
                        rightType
                )
        );

        var elseArm = new KExpr.Return(
                region,
                elseReturning,
                rightType
        );

        KExpr branch = new KExpr.Branch(
                region,
                condition,
                thenArm,
                new ElsePart(elseArm, null),
                null,
                new BranchYieldSymbol.YieldValue(yieldType)
        );

        return new KExpr.Block(
                region,
                List.of(assignment, branch),
                yieldType,
                false
        );
    }


    public KExpr lower(LoweringContext ctx) {

        var region = this.unwrap.region();
        var left = LowerExpr.lower(ctx, this.unwrap.left());
        var symbol = this.unwrap.symbol();
        assert symbol != null;

        return switch (symbol) {
            case UnwrapSymbol.UnwrapOptional(var inner) -> {
                yield createExpression(
                        region,
                        ctx,
                        inner,
                        KType.KARINA_OPTION(inner),
                        KType.KARINA_OPTION_SOME(KType.ROOT),
                        KType.KARINA_OPTION_NONE(KType.ROOT),
                        left,
                        "$option"
                );
            }
            case UnwrapSymbol.UnwrapResult(var ok, var error) -> {
                yield createExpression(
                        region,
                        ctx,
                        ok,
                        KType.KARINA_RESULT(ok, error),
                        KType.KARINA_RESULT_OK(KType.ROOT, KType.ROOT),
                        KType.KARINA_RESULT_ERR(KType.ROOT, KType.ROOT),
                        left,
                        "$result"
                );
            }
        };
    }
}
