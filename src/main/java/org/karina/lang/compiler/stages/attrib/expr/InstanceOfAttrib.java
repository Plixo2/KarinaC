package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.util.ArrayList;

public class InstanceOfAttrib extends AttribExpr {
    public static AttribExpr attribInstanceOf(
            @Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();

        var isType = expr.isType();

        //we replace non annotated generics with AnyClass, this is technically not necessary
        if (isType instanceof KType.ClassType classType) {
            var item = KTree.findAbsolutItem(ctx.root(), classType.path().value());
            if (item instanceof KTree.KStruct typeItem) {
                var newGenerics = new ArrayList<KType>();
                for (var i = 0; i < typeItem.generics().size(); i++) {
                    newGenerics.add(new KType.AnyClass(isType.region()));
                }
                isType = new KType.ClassType(
                        isType.region(),
                        classType.path(),
                        newGenerics
                );
            } else {
                //should not happen
                Log.temp(expr.region(), "Type not found");
                throw new Log.KarinaException();
            }
        }


        return of(ctx, new KExpr.IsInstanceOf(
                expr.region(),
                left,
                isType
        ));
    }

}
