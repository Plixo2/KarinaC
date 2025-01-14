package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.BranchPattern;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;

public class BranchAttrib extends AttribExpr {
    public static AttribExpr attribBranch(
            @Nullable KType hint, AttributionContext ctx, KExpr.Branch expr) {

        var boolType = new KType.PrimitiveType.BoolType(expr.condition().region());
        var conditionHint =  expr.branchPattern() == null ? boolType : null;
        var condition = attribExpr(conditionHint, ctx, expr.condition()).expr();

        var thenContext = ctx;
        BranchPattern branchPattern;
        switch (expr.branchPattern()) {
            case BranchPattern.Cast cast -> {
                var isType = cast.type();

                //replace all generics with AnyClass
                if (isType instanceof KType.ClassType classType) {
                    var item = KTree.findAbsolutItem(ctx.root(), classType.path().value());
                    if (item instanceof KTree.KStruct typeItem) {
                        var newGenerics = new ArrayList<KType>();
                        for (var i = 0; i < typeItem.generics().size(); i++) {
                            newGenerics.add(new KType.Resolvable(isType.region()));
                        }
                        isType = new KType.ClassType(
                                isType.region(),
                                classType.path(),
                                newGenerics
                        );
                        //for inference only
                        var ignored = ctx.canAssign(isType.region(), condition.type(), isType, true);
                        //if not inferred, resolve to base case
                        for (var newGeneric : newGenerics) {
                            var type = (KType.Resolvable) newGeneric;
                            if (!type.isResolved()) {
                                type.tryResolve(new KType.AnyClass(isType.region()));
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
            case null -> {
                branchPattern = null;
                ctx.assign(condition.region(), boolType, condition.type());
            }
        }

        var then = attribExpr(hint, thenContext, expr.thenArm()).expr();
        var elseArm = expr.elseArm();

        KType returnType;

        if (elseArm == null) {
            returnType = new KType.PrimitiveType.VoidType(expr.region());
        } else {
            var elseHint = hint;
            if (hint == null) {
                elseHint = then.type();
            }
            elseArm = attribExpr(elseHint, ctx, elseArm).expr();
            returnType = ctx.getSuperType(then.region(), then.type(), elseArm.type());
            if (returnType == null) {
                returnType = new KType.PrimitiveType.VoidType(expr.region());
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
