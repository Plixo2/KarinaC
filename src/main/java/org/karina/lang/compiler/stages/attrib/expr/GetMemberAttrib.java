package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.symbols.MemberSymbol;

import java.util.HashMap;
import java.util.Map;

public class GetMemberAttrib extends AttribExpr{
    public static AttribExpr attribGetMember(
            @Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();

        var struct = ctx.getStruct(expr.left().region(), left.type());
        //casting is ok, ctx.getStruct already checks for this
        var classType = (KType.ClassType) left.type();

        MemberSymbol symbol;
        var fieldToGet =
                struct.fields().stream().filter(ref -> ref.name().equals(expr.name())).findFirst();
        var functionToGet =
                struct.functions().stream().filter(ref -> ref.name().equals(expr.name())).findFirst();

        if (classType.generics().size() != struct.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }

        if (fieldToGet.isPresent()) {
            var field = fieldToGet.get();

            Map<Generic, KType> mapped = new HashMap<>();
            for (var i = 0; i < classType.generics().size(); i++) {
                var generic = struct.generics().get(i);
                var type = classType.generics().get(i);
                mapped.put(generic, type);
            }
            var fieldType = replaceType(field.type(), mapped);

            symbol = new MemberSymbol.FieldSymbol(
                    fieldType,
                    field.path(),
                    field.name().value()
            );
        } else if (functionToGet.isPresent()) {
            var function = functionToGet.get();
            symbol = new MemberSymbol.VirtualFunctionSymbol(
                    expr.region(),
                    classType,
                    function.path()
            );
        } else {
            Log.attribError(new AttribError.UnknownField(expr.name().region(), expr.name().value()));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.GetMember(
                expr.region(),
                left,
                expr.name(),
                symbol
        ));
    }
}
