package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.AssignmentSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;

public class AssignmentAttrib extends AttributionExpr {

    public static AttributionExpr attribAssignment(@Nullable KType hint, AttributionContext ctx, KExpr.Assignment expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var right = attribExpr(left.type(), ctx, expr.right()).expr();

        AssignmentSymbol symbol;

        if (left instanceof KExpr.Literal(
                var ignored, var ignored2, LiteralSymbol.VariableReference(var ignored3, Variable variable))
        ) {
            symbol = new AssignmentSymbol.LocalVariable(variable);
        } else if (left instanceof KExpr.GetMember(
                var ignored, var object, var ignored3, MemberSymbol.FieldSymbol(KType type, ObjectPath fieldPath, var name, var owner))
        ) {
            symbol = new AssignmentSymbol.Field(object, fieldPath, name,  owner, type);
        } else if (left instanceof KExpr.GetArrayElement getArrayElement) {
            symbol = new AssignmentSymbol.ArrayElement(getArrayElement.left(), getArrayElement.index(),
                    getArrayElement.elementType());
        } else {
            Log.temp(left.region(), "Unknown assignment symbol");
            throw new Log.KarinaException();
        }
        ctx.assign(right.region(), left.type(), right.type());

        return of(ctx, new KExpr.Assignment(
                expr.region(),
                left,
                right,
                symbol
        ));
    }

}
