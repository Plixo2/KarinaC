package org.karina.lang.compiler.stages.attrib.expr;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.BinaryOperator;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.BinOperatorSymbol;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;


import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class BinaryAttrib {
    public static AttributionExpr attribBinary(
            @Nullable KType hint, AttributionContext ctx, KExpr.Binary expr) {
        var left = attribExpr(hint, ctx, expr.left()).expr();
        var right = attribExpr(left.type(), ctx, expr.right()).expr();
        BinOperatorSymbol op;

        var result = objectTest(ctx, expr.region(), expr.operator(), left, right);

        if (result == null) {
            //not null, throws exception inside
            result = numberTest(ctx, expr.region(), expr.operator(), left, right);
        }

        op = result.op();
        left = result.left();
        right = result.right();

        if (op == null) {
            Log.attribError(new AttribError.NotSupportedOperator(expr.operator(), left.type(), right.type()));
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

    //TODO: add support for simple operator overloading and all primitive types
    private static @NotNull BinaryAttrib.BinResult numberTest(
            AttributionContext ctx,
            Region region,
            RegionOf<BinaryOperator> operator,
            KExpr left,
            KExpr right
    ) {
        var leftType = left.type();
        var rightType = right.type();

        //for resolvable types, and implicit conversions
        if (!(leftType instanceof KType.PrimitiveType)) {
            left = ctx.makeAssignment(left.region(), rightType, left);
            leftType = left.type();
        }
        if (!(rightType instanceof KType.PrimitiveType)) {
            right = ctx.makeAssignment(right.region(), leftType, right);
            rightType = right.type();
        }

        if (!(leftType instanceof KType.PrimitiveType(KType.KPrimitive primitive))) {
            Log.attribError(new AttribError.NotSupportedOperator(operator, leftType, rightType));
            throw new Log.KarinaException();
        }
        if (!(rightType instanceof KType.PrimitiveType(KType.KPrimitive primitive1))) {
            Log.attribError(new AttribError.NotSupportedOperator(operator, leftType, rightType));
            throw new Log.KarinaException();
        }
        if (primitive != primitive1) {
            Log.attribError(new AttribError.TypeMismatch(region, leftType, rightType));
            throw new Log.KarinaException();
        }
        val op = switch (primitive) {
            case INT -> BinOperatorSymbol.IntOP.fromOperator(operator);
            case FLOAT -> BinOperatorSymbol.FloatOP.fromOperator(operator);
            case BOOL -> BinOperatorSymbol.BoolOP.fromOperator(operator);
            case DOUBLE -> BinOperatorSymbol.DoubleOP.fromOperator(operator);
            case LONG -> BinOperatorSymbol.LongOP.fromOperator(operator);
            case CHAR, SHORT, BYTE -> {
                Log.temp(operator.region(), "Binary for " + primitive + " not implemented");
                throw new Log.KarinaException();
            }
        };
        return new BinResult(left, right, op);
    }


    private static @Nullable BinResult objectTest(
            AttributionContext ctx,
            Region region,
            RegionOf<BinaryOperator> operator,
            KExpr left,
            KExpr right
    ) {

        if (!hasIdentity(left.type()) || !hasIdentity(right.type())) {
            return null;
        }

        var op = BinOperatorSymbol.ObjectEquals.fromOperator(operator);
        if (op == null) {
            return null;
        }


        //TODO replace with check if the type can be
        // assiged to java.lang.Object, when implemented in TypeChecker


        left = ctx.makeAssignment(region, KType.ROOT, left);
        right = ctx.makeAssignment(region, KType.ROOT, right);

        return new BinResult(left, right, op);
    }

    private static boolean hasIdentity(KType type) {
        return switch (type) {
            case KType.ArrayType _,
                 KType.ClassType _,
                 KType.FunctionType _,
                 KType.Resolvable _,
                 KType.GenericLink _ -> true;

            case KType.PrimitiveType _,
                 KType.VoidType _ -> false;
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
        };
    }


    private record BinResult(KExpr left, KExpr right, BinOperatorSymbol op) {}
}
