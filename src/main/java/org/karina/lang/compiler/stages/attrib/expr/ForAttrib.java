package org.karina.lang.compiler.stages.attrib.expr;

import com.google.errorprone.annotations.Var;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.IteratorTypeSymbol;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.util.function.Function;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class ForAttrib  {

    //TODO add support for forEach function
    // So that we can use this for destructuring, e.g. to use for instead Map.forEach(fn(k, v) { ... })
    public static AttributionExpr attribFor(@Nullable KType hint, AttributionContext ctx, KExpr.For expr) {

        KType varHint = null;

        var annotatedHint = expr.varPart().type();
        if (annotatedHint != null) {
            //varHint = KType.ITERABLE(annotatedHint);
            varHint = new KType.ArrayType(annotatedHint);
        }

        var iter = attribExpr(varHint, ctx, expr.iter()).expr();

        var typeOfLoop = getIteratorType(ctx, iter.type(), iter);
        iter = typeOfLoop.expr();

        if (annotatedHint != null) {
            if (!ctx.checking().canAssign(expr.region(), annotatedHint, typeOfLoop.varType(), true)) {
                Log.attribError(new AttribError.TypeMismatch(expr.region(), annotatedHint, typeOfLoop.varType()));
                throw new Log.KarinaException();
            }
        }

        var variable = new Variable(
                expr.varPart().name().region(),
                expr.varPart().name().value(),
                typeOfLoop.varType(),
                false, //effectively final
                false
        );

        IteratorTypeSymbol symbol;
        if (typeOfLoop.arrayType() != null) {
            symbol = new IteratorTypeSymbol.ForArray(
                    variable,
                    typeOfLoop.arrayType()
            );
        } else {
            symbol = new IteratorTypeSymbol.ForIterable(
                    variable
            );
        }

        var bodyCtx = ctx.setInLoop(true);

        if (!variable.name().equals("_")) {
            bodyCtx = bodyCtx.addVariable(variable);
        }
        var body = attribExpr(null, bodyCtx, expr.body()).expr();

        return of(ctx, new KExpr.For(
                expr.region(),
                expr.varPart(),
                iter,
                body,
                symbol
        ));
    }

    private static IteratorExpr getIteratorType(AttributionContext ctx, KType type, KExpr iter) {
        if (type instanceof KType.ArrayType arrayType) {
            return new IteratorExpr(
                iter,
                arrayType.elementType(),
                arrayType
            );
        } else {
            var iter_type = new KType.Resolvable();
            var iterable_interface = KType.ITERABLE(iter_type);
            var expr = ctx.makeAssignment(iter.region(), iterable_interface, iter);

            return new IteratorExpr(
                expr,
                iter_type,
                null
            );
        }
    }

    //When ArrayType is null, its a iterable
    private record IteratorExpr(KExpr expr, KType varType, @Nullable KType.ArrayType arrayType) { }



}
