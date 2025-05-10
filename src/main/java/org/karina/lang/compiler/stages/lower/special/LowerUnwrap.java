package org.karina.lang.compiler.stages.lower.special;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
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
/// value is Option::Some some { some.value } else { return Option::None {} }
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
    public KExpr lower(LoweringContext ctx) {

        var region = this.unwrap.region();
        var left = LowerExpr.lower(ctx, this.unwrap.left());
        var symbol = this.unwrap.symbol();
        assert symbol != null;

        switch (symbol) {
            case UnwrapSymbol.UnwrapOptional unwrapOptional -> {
//                var condition = new KExpr.IsInstanceOf(region, left, KType.KARINA_OPTION_SOME(KType.ROOT));

                var someType = KType.KARINA_OPTION_SOME(unwrapOptional.inner());
                var variable = new Variable(
                        region,
                        "$some",
                        someType,
                        false,
                        false,
                        true
                );
                var pattern = new BranchPattern.Cast(
                        region,
                        someType,
                        RegionOf.region(region, "$some"),
                        variable
                );
                var variableExpr = new KExpr.Literal(region, "$some", new LiteralSymbol.VariableReference(region, variable));
                var fieldPointer = FieldPointer.of(region, KType.KARINA_OPTION_SOME(KType.ROOT).pointer(), "value");
                var getValueSymbol = new MemberSymbol.FieldSymbol(
                        fieldPointer,
                        unwrapOptional.inner(),
                        someType
                );
                var getValue = new KExpr.GetMember(
                        region,
                        variableExpr,
                        RegionOf.region(region, "value"),
                        false,
                        getValueSymbol
                );

                KExpr unused = new KExpr.Boolean(region, false);
                var initSignature = new Signature(ImmutableList.of(), KType.NONE);
                var pointer = KType.KARINA_OPTION_NONE(KType.ROOT).pointer();
                var initPointer = MethodPointer.of(
                        region, pointer, "<init>",
                        initSignature
                );
                var elseCallSymbol = new CallSymbol.CallSuper(
                        initPointer,
                        List.of(KType.ROOT),
                        KType.ROOT,
                        new InvocationType.NewInit(KType.KARINA_OPTION_NONE(KType.ROOT))
                );
                var elseExprNewType = new KExpr.Call(region, unused, ImmutableList.of(), ImmutableList.of(), elseCallSymbol);
                var elseExprReturn = new KExpr.Return(region, elseExprNewType,  KType.ROOT);
                var elseArm = new ElsePart(elseExprReturn, null);

                var yielding = new BranchYieldSymbol.YieldValue(
                        unwrapOptional.inner()
                );
                var branch = new KExpr.Branch(region, left, getValue, elseArm, pattern, yielding);
                return LowerExpr.lower(ctx, branch);
            }
            case UnwrapSymbol.UnwrapResult unwrapResult -> {
                Log.temp(region, "Not implemented yet");
                throw new Log.KarinaException();
            }
        }
    }
}
