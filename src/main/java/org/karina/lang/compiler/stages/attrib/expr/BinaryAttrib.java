package org.karina.lang.compiler.stages.attrib.expr;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.BinOperatorSymbol;

public class BinaryAttrib extends AttributionExpr {
    public static AttributionExpr attribBinary(
            @Nullable KType hint, AttributionContext ctx, KExpr.Binary expr) {
        var left = attribExpr(hint, ctx, expr.left()).expr();
        var right = attribExpr(left.type(), ctx, expr.right()).expr();

        var leftType = left.type();
        var rightType = right.type();

        //for resolvable types
        if (!(leftType instanceof KType.PrimitiveType)) {
            ctx.assign(left.region(), rightType, leftType);
            leftType = leftType.unpack();
        }
        if (!(rightType instanceof KType.PrimitiveType)) {
            ctx.assign(right.region(), leftType, rightType);
            rightType = rightType.unpack();
        }

        if (!(leftType instanceof KType.PrimitiveType leftPrimitive)) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), leftType));
            throw new Log.KarinaException();
        }
        if (!(rightType instanceof KType.PrimitiveType rightPrimitive)) {
            Log.attribError(new AttribError.NotSupportedType(right.region(), rightType));
            throw new Log.KarinaException();
        }
        if (leftPrimitive.primitive() != rightPrimitive.primitive()) {
            Log.attribError(new AttribError.TypeMismatch(expr.region(), leftType, rightType));
            throw new Log.KarinaException();
        }
        val op = switch (leftPrimitive.primitive()) {
            case INT -> BinOperatorSymbol.IntOP.fromOperator(expr.operator());
            case FLOAT -> BinOperatorSymbol.FloatOP.fromOperator(expr.operator());
            case BOOL -> BinOperatorSymbol.BoolOP.fromOperator(expr.operator());
            case DOUBLE -> BinOperatorSymbol.DoubleOP.fromOperator(expr.operator());
            case LONG -> BinOperatorSymbol.LongOP.fromOperator(expr.operator());
            case VOID -> {
                Log.attribError(new AttribError.NotSupportedType(left.region(), leftType));
                throw new Log.KarinaException();
            }
            case CHAR, SHORT, BYTE -> {
                Log.temp(expr.operator().region(), "Binary for " + leftPrimitive.primitive() + " not implemented");
                throw new Log.KarinaException();
            }
        };
        if (op == null) {
            Log.attribError(new AttribError.NotSupportedType(expr.operator().region(), leftType));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Binary(
                expr.region(),
                left,
                expr.operator(),
                right,
                op
        ));
    }
}
