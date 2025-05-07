package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.of;

public class StaticPathAttrib {
    public static AttributionExpr attribStaticPath(
            @Nullable KType hint, AttributionContext ctx, KExpr.StaticPath expr) {

        var classModel = ctx.model().getClass(expr.importedPointer());
        var referenceClass = Types.erasedClass(classModel);

        var reference = new KExpr.Literal(
                expr.region(),
                expr.path().last(),
                new LiteralSymbol.StaticClassReference(
                        expr.region(),
                        expr.importedPointer(),
                        referenceClass,
                        false
                )
        );
        return of(
                ctx,
                reference
        );
    }


}
