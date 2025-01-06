package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.BranchPattern;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

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
                var newSymbol = new Variable(
                        cast.castedName().region(),
                        cast.castedName().value(),
                        cast.type(),
                        false,
                        false
                );
                thenContext = thenContext.addVariable(newSymbol);
                branchPattern = new BranchPattern.Cast(
                        cast.region(),
                        cast.type(),
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
