package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.BranchYieldSymbol;
import org.karina.lang.compiler.utils.BranchPattern;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ElsePart;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class BranchAttrib  {
    public static AttributionExpr attribBranch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Branch expr) {

        var boolType = new KType.PrimitiveType(KType.KPrimitive.BOOL);
        var conditionHint = expr.branchPattern() == null ? boolType : null;
        var condition = attribExpr(conditionHint, ctx, expr.condition()).expr();

        var thenContext = ctx;
        BranchPattern trueBranchPattern;
        if (expr.branchPattern() == null) {
            condition = ctx.makeAssignment(condition.region(), boolType, condition);
            trueBranchPattern = null;
        } else {
            var result = evalBranchPattern(ctx, expr.branchPattern(), condition.type());
            thenContext = result.thenContext();
            trueBranchPattern = result.attribPattern();
        }

        var then = attribExpr(hint, thenContext, expr.thenArm()).expr();
        var elseArm = expr.elseArm();

        BranchYieldSymbol yieldSymbol;

        if (elseArm == null) {
            yieldSymbol = new BranchYieldSymbol.None();
            Log.recordType(Log.LogTypes.BRANCH, "Branch yielding no value, no else arm");
        } else {
            var elseHint = hint;
            if (hint == null) {
                elseHint = then.type();
            }
            AttributionContext elseContext;
            BranchPattern elsePattern;
            if (elseArm.elsePattern() == null) {
                elseContext = ctx;
                elsePattern = null;
            } else {
                if (trueBranchPattern == null) {
                    Log.temp(elseArm.elsePattern().region(), "Branch pattern required for else branch, this should be checked before");
                    throw new Log.KarinaException();
                }

                var result = evalBranchPattern(ctx, elseArm.elsePattern(), condition.type());
                elseContext = result.thenContext();
                elsePattern = result.attribPattern();
            }

            var elseExpr = attribExpr(elseHint, elseContext, elseArm.expr()).expr();
            elseArm = new ElsePart(elseExpr, elsePattern);

            var bothCasesCovered = true;

            //test if both cases are covered by the pattern, so we can yield values and return
            // ww dont want to add a fancy way to chain more if-else statements, that is the purpose of the match statement
            if (elsePattern != null) {
                bothCasesCovered = false;
                if (condition.type() instanceof KType.ClassType classType) {
                    //todo add extra case, when emitting the bytecode
                    // to check if both cases fail, even tho it should be impossible,
                    // but the bytecode cannot be verified.
                    var classModel = ctx.model().getClass(classType.pointer());

                    var permittedSubclasses = new HashSet<>(classModel.permittedSubclasses());

                    Log.recordType(Log.LogTypes.BRANCH,"permittedSubclasses of if branch = " + permittedSubclasses);

                    if (permittedSubclasses.size() == 2) {
                        if (trueBranchPattern.type() instanceof KType.ClassType innerTrueCase) {
                            permittedSubclasses.remove(innerTrueCase.pointer());
                        }
                        if (elsePattern.type() instanceof KType.ClassType innerFalseCase) {
                            permittedSubclasses.remove(innerFalseCase.pointer());
                        }
                        if (permittedSubclasses.isEmpty() && Modifier.isAbstract(classModel.modifiers())) {
                            bothCasesCovered = true;
                        }
                    }
                }
            }


            var thenReturns = then.doesReturn();
            var elseReturns = elseExpr.doesReturn();
            if (!bothCasesCovered) {
                yieldSymbol = new BranchYieldSymbol.None();
            } else {
                if (thenReturns && elseReturns) {
                    yieldSymbol = new BranchYieldSymbol.Returns();
                } else if (thenReturns) {
                    yieldSymbol = getSymbolFromType(elseExpr.type());
                } else if (elseReturns) {
                    yieldSymbol = getSymbolFromType(then.type());
                } else {
                    var yieldingType = elseContext.checking().superType(
                            then.region(),
                            then.type(),
                            elseExpr.type()
                    );
                    yieldSymbol = getSymbolFromType(yieldingType);
                }
            }

            Function<Boolean, String> toStr = b -> b ? "returns" : "does not return";
            Log.recordType(Log.LogTypes.BRANCH, "Then branch: " + toStr.apply(thenReturns) + ", yielding value of type: " + then.type());
            Log.recordType(Log.LogTypes.BRANCH, "Else branch: " + toStr.apply(elseReturns) + ", yielding value of type: " + elseExpr.type());
            Log.recordType(Log.LogTypes.BRANCH, "Both cases covered: " + bothCasesCovered + ", Yield symbol: " + yieldSymbol);

        }


        return of(
                ctx, new KExpr.Branch(
                        expr.region(), condition, then, elseArm, trueBranchPattern,
                        yieldSymbol
                )
        );

    }

    private static BranchYieldSymbol getSymbolFromType(@Nullable KType type) {
        if (type == null) {
            return new BranchYieldSymbol.None();
        } else {
            if (type.isVoid()) {
                return new BranchYieldSymbol.None();
            } else {
                return new BranchYieldSymbol.YieldValue(type);
            }
        }
    }

    private static @NotNull BranchPatternResult evalBranchPattern(
            AttributionContext ctx,
            BranchPattern pattern, KType inferHint
    ) {

        AttributionContext thenContext;
        BranchPattern branchPattern;
        switch (pattern) {
            case BranchPattern.Cast cast -> {
                var isType = cast.type();

                //replace all generics with KType.ROOT
                if (isType instanceof KType.ClassType classType) {
                    var classModel = ctx.model().getClass(classType.pointer());
                    var newGenerics = new ArrayList<KType>();
                    for (var i = 0; i < classModel.generics().size(); i++) {
                        newGenerics.add(new KType.Resolvable());
                    }
                    isType = new KType.ClassType(classType.pointer(), newGenerics);
                    //for inference only
                    var _ = ctx.checking().canAssign(cast.region(), inferHint, isType, true);
                    //if not inferred, resolve to base case
                    for (var newGeneric : newGenerics) {
                        var type = (KType.Resolvable) newGeneric;
                        if (!type.isResolved()) {
                            type.tryResolve(cast.region(), KType.ROOT);
                        }
                    }
                } else {
                    Log.attribError(new AttribError.NotAStruct(cast.region(), isType));
                    throw new Log.KarinaException();
                }


                var name = cast.castedName().value();

                Variable newSymbol;
                if (name.equals("_")) {
                    newSymbol = null;
                    thenContext = ctx;
                } else {
                    newSymbol = new Variable(
                            cast.castedName().region(),
                            name,
                            isType,
                            false,
                            false);
                    thenContext = ctx.addVariable(newSymbol);
                }
                branchPattern = new BranchPattern.Cast(cast.region(), isType, cast.castedName(), newSymbol);
            }
            case BranchPattern.Destruct destruct -> {
                Log.temp(destruct.region(), "Destruct not implemented");
                throw new Log.KarinaException();
            }
            case BranchPattern.JustType justType -> {
                var kType = InstanceOfAttrib.attribInner(ctx, justType.region(), justType.type());
                thenContext = ctx;
                branchPattern = new BranchPattern.JustType(justType.region(), kType);
            }
        }
        return new BranchPatternResult(thenContext, branchPattern);
    }

    private record BranchPatternResult(
            AttributionContext thenContext, BranchPattern attribPattern
    ) {
    }
}
