package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.LiteralSymbol;

import java.util.HashSet;

public class LiteralAttrib extends AttributionExpr {
    public static AttributionExpr attribLiteral(
            @Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) {

        LiteralSymbol symbol;
//        var function = ctx.table().getFunction(expr.name());
        var variable = ctx.variables().get(expr.name());
        var item = ctx.table().getClass(expr.name());
//        if (variable != null) {
//            variable.incrementUsageCount();
//            symbol = new LiteralSymbol.VariableReference(expr.region(), variable);
//        } else if (function != null) {
//            symbol = new LiteralSymbol.StaticFunction(expr.region(), function.classPointer().path());
//        } else if (item instanceof KTree.KStruct struct) {
//            symbol = new LiteralSymbol.StructReference(expr.region(), struct.path());
//        } else if (item instanceof KTree.KInterface struct) {
//            symbol = new LiteralSymbol.InterfaceReference(expr.region(), struct.path());
//        } else {
//            var variableName = ctx.variables().names();
//            var functionNames = ctx.table().availableFunctionNames();
//            var available = new HashSet<>(variableName);
//            available.addAll(functionNames);
//            Log.attribError(new AttribError.UnknownIdentifier(expr.region(), expr.name(), available));
//            throw new Log.KarinaException();
//        }
//        return of(ctx, new KExpr.Literal(
//                expr.region(),
//                expr.name(),
//                symbol
//        ));
throw new NullPointerException("");
    }
}
