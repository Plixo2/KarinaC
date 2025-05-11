package org.karina.lang.compiler.stages.lower.special;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.stages.lower.LowerExpr;
import org.karina.lang.compiler.stages.lower.LoweringContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.BranchYieldSymbol;
import org.karina.lang.compiler.utils.symbols.CastSymbol;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;

import java.util.List;

///
///
/// There are 3 types of patterns:
/// 1. Cast - if v is Some some
/// 2. Destruct - if v is Some(v)
/// 3 . JustType - if v is Some
///
/// the 3. type is just used for the else pattern
/// ```
/// if v is Some some {
///
/// } else is None {
///
/// }
/// ```
///
/// TODO Destruct is not implement yet
///
///
/// When we have only a if pattern, we convert it to the following:
///
/// ```
/// let $pattern = v
/// if $pattern is Some {
///     // only when we have a cast.
///     let some = $pattern as Some;
///
/// } else {
///
/// }
/// ```
///
/// When a else pattern exist, we have to test if the branch yields a value or returns.
/// If so, we have proven that both cases are covert by the two types.
/// We just cast to the new type without checking. This will satisfy the verifier.
///
/// ```
/// let $pattern = v
/// if $pattern is Some {
///     let some = $pattern as Some;
/// } else {
///     let none = $pattern as None
/// }
/// ```
///
/// Otherwise we just need to create a extra branch
/// ```
/// let $pattern = v
/// if $pattern is Some {
///     let some = $pattern as Some;
/// } else if $pattern is None {
///
/// }
/// ```
///
/// Signature:
///     `Branch(Region region, KExpr condition, KExpr thenArm, @Nullable ElsePart elseArm, @Nullable BranchPattern branchPattern, @Nullable @Symbol BranchYieldSymbol symbol)`
public class LowerBranch {
    private final KExpr.Branch branch;

    public LowerBranch(KExpr.Branch branch) {
        this.branch = branch;
    }
    public KExpr lower(LoweringContext ctx) {
        var region = this.branch.region();
        var condition = LowerExpr.lower(ctx, this.branch.condition());
        var thenArmLower = LowerExpr.lower(ctx, this.branch.thenArm());

        var branchPattern = this.branch.branchPattern();
        if (branchPattern == null) {


            ElsePart elseArm = null;

            if (this.branch.elseArm() != null) {
                var elseArmLower = LowerExpr.lower(ctx, this.branch.elseArm().expr());
                // no else pattern exists when no primary pattern exist
                elseArm = new ElsePart(elseArmLower, null);
            }

            var symbol = this.branch.symbol();
            assert symbol != null;
            return new KExpr.Branch(region, condition, thenArmLower, elseArm, null, symbol);
        }


        var newVariableName = "$pattern" + ctx.syntheticCounter().incrementAndGet();

        var varType = condition.type();
        var variable = new Variable(
                region,
                newVariableName,
                varType,
                false,
                false,
                true
        );
        var assignment = new KExpr.VariableDefinition(
                region,
                new RegionOf<>(region, newVariableName),
                varType,
                condition,
                variable
        );
        var variableReference = new KExpr.Literal(
                region,
                newVariableName,
                new LiteralSymbol.VariableReference(region, variable)
        );

        var primaryCondition = new KExpr.IsInstanceOf(region, variableReference,  branchPattern.type());

        var thenBody = lowerPattern(variableReference, thenArmLower, branchPattern);

        var elsePart = getElsePart(variableReference, this.branch.elseArm(), ctx);


        var newBranch = new KExpr.Branch(
                region,
                primaryCondition,
                thenBody,
                elsePart,
                null,
                this.branch.symbol()
        );

        return new KExpr.Block(
                region,
                List.of(
                        assignment,
                        newBranch
                ),
                this.branch.type(),
                this.branch.symbol() instanceof BranchYieldSymbol.Returns
        );
    }

    /**
     *
     * @param variableExpr reference to the $pattern variable
     * @param body should be lowered before
     */
    private KExpr lowerPattern(KExpr variableExpr, KExpr body, BranchPattern pattern) {
        var region = variableExpr.region();

        return switch (pattern) {
            case BranchPattern.Cast cast -> {
                assert cast.symbol() != null;
                var variable = cast.symbol();
                var varDef = new KExpr.VariableDefinition(
                        region,
                        new RegionOf<>(region, variable.name()),
                        cast.type(),
                        new KExpr.Cast(
                                region,
                                variableExpr,
                                new CastTo.AutoCast(),
                                new CastSymbol.UpCast(
                                        variableExpr.type(),
                                        cast.type()
                                )
                        ),
                        variable
                );

                yield new KExpr.Block(
                        region,
                        List.of(varDef, body),
                        body.type(),
                        body.doesReturn()
                );
            }
            case BranchPattern.Destruct destruct -> {
                Log.temp(variableExpr.region(), "Destruct not implemented");
                throw new Log.KarinaException();
            }
            case BranchPattern.JustType justType -> {
                yield body;
            }
        };

    }


    private @Nullable ElsePart getElsePart(KExpr variableExpr, @Nullable ElsePart elsePart, LoweringContext ctx) {
        if (elsePart == null) {
            return null;
        }
        var body = LowerExpr.lower(ctx, elsePart.expr());
        var elsePattern = elsePart.elsePattern();
        // when no pattern, just return the default else body
        if (elsePattern == null) {
            return new ElsePart(body, null);
        }

        // new body with casted expression
        var patternApplied = lowerPattern(variableExpr, body, elsePattern);

        // tests when body cases are covered, so we can skip the check
        var shouldCast = !(this.branch.symbol() instanceof BranchYieldSymbol.None);
        if (shouldCast) {
            return new ElsePart(patternApplied, null);
        }

        //otherwise there are more than two cases and we have to add another if ... is check
        var secondaryCondition = new KExpr.IsInstanceOf(body.region(), variableExpr, elsePattern.type());

        // new branch, but without a elseArm, since the expression doesnt yield and doesnt return
        var elseBranch = new KExpr.Branch(
                body.region(),
                secondaryCondition,
                patternApplied,
                null,
                null,
                new BranchYieldSymbol.None()
        );

        return new ElsePart(elseBranch, null);
    }


    /*private KExpr createUnreachable(LoweringContext context, Region region) {
        var classModel = context.model().getClass(KType.MATCH_EXCEPTION.pointer());

        var classType = new KType.ClassType(classModel.pointer(), List.of());
        var superLiteral = new KExpr.SpecialCall(region, new InvocationType.NewInit(classType));
        var inits = classModel.getMethodCollectionShallow("<init>");
        if (inits.isEmpty()) {
            Log.temp(region, "Missing Constructor for MatchException");
            throw new Log.KarinaException();
        }
        var constructorPointer = inits.methods().getFirst();

        var symbol = new CallSymbol.CallSuper(
                constructorPointer, List.of(), classType,
                superLiteral.invocationType()
        );

        if (constructorPointer.erasedParameters().size() != 2) {
            Log.temp(region, "MatchException constructor has wrong number of parameters");
            throw new Log.KarinaException();
        }

        List<KExpr> args = List.of(
                new KExpr.StringExpr(
                        region,
                        "Unreachable",
                        false
                ),
                new KExpr.Literal(region, "null", new LiteralSymbol.Null(region))
        );
        return new KExpr.Call(region, superLiteral, List.of(), args, symbol);


    }
*/
}
