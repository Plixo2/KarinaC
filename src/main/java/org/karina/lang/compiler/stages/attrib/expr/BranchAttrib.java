package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.utils.BranchPattern;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ElsePart;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;

public class BranchAttrib extends AttributionExpr {
    public static AttributionExpr attribBranch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Branch expr) {

        var boolType =  new KType.PrimitiveType(KType.KPrimitive.BOOL);
        var conditionHint =  expr.branchPattern() == null ? boolType : null;
        var condition = attribExpr(conditionHint, ctx, expr.condition()).expr();

        var thenContext = ctx;
        BranchPattern branchPattern;
        switch (expr.branchPattern()) {
            case BranchPattern.Cast cast -> {
                var isType = cast.type();

                //replace all generics with AnyClass
                if (isType instanceof KType.ClassType classType) {
                    var item = KTree.findAbsolutItem(ctx.root(), classType.path());
                    if (item instanceof KTree.KStruct typeItem) {
                        var newGenerics = new ArrayList<KType>();
                        for (var i = 0; i < typeItem.generics().size(); i++) {
                            newGenerics.add(new KType.Resolvable());
                        }
                        isType = new KType.ClassType(
                                ClassPointer.of(classType.path()),
                                newGenerics
                        );
                        //for inference only
                        var ignored = ctx.canAssign(expr.region(), condition.type(), isType, true);
                        //if not inferred, resolve to base case
                        for (var newGeneric : newGenerics) {
                            var type = (KType.Resolvable) newGeneric;
                            if (!type.isResolved()) {
                                type.tryResolve(expr.region(), new KType.AnyClass());
                            }
                        }
                    } else {
                        //should not happen
                        Log.temp(expr.region(), "Unknown Type to check");
                        throw new Log.KarinaException();
                    }
                }


                var newSymbol = new Variable(
                        cast.castedName().region(),
                        cast.castedName().value(),
                        isType,
                        false,
                        false
                );
                thenContext = thenContext.addVariable(newSymbol);
                branchPattern = new BranchPattern.Cast(
                        cast.region(),
                        isType,
                        cast.castedName(),
                        newSymbol
                );
            }
            case BranchPattern.Destruct destruct -> {
                Log.temp(destruct.region(), "Destruct not implemented");
                throw new Log.KarinaException();
            }
            case BranchPattern.JustType justType -> {
                Log.temp(justType.region(), "justType not implemented");
                throw new Log.KarinaException();
            }
            case null -> {
                branchPattern = null;
                ctx.assign(condition.region(), boolType, condition.type());
            }
        }

        var then = attribExpr(hint, thenContext, expr.thenArm()).expr();
        var elseArm = expr.elseArm();

        KType returnType;

        if (elseArm == null) {
            returnType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        } else {
            var elseHint = hint;
            if (hint == null) {
                elseHint = then.type();
            }
            var elseExpr = attribExpr(elseHint, ctx, elseArm.expr()).expr();
            elseArm = new ElsePart(elseExpr, null);
            returnType = ctx.getSuperType(then.region(), then.type(), elseExpr.type());
            if (returnType == null) {
                returnType = new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
        }

        return of(ctx, new KExpr.Branch(
                expr.region(),
                condition,
                then,
                elseArm,
                branchPattern,
                returnType
        ));

    }
}
