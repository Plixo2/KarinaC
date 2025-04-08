package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.UnwrapSymbol;
import org.karina.lang.compiler.utils.Region;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.attribExpr;
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
