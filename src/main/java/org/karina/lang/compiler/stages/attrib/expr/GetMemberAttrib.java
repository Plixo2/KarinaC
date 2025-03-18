package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class GetMemberAttrib  {
    public static AttributionExpr attribGetMember(@Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();

        var name = expr.name().value();
        var staticFunc = attribStaticFunctionReference(expr.region(), ctx, left, name);
        if (staticFunc != null) {
            return staticFunc;
        }
        var attributionExpr = attribArrayLength(expr.region(), ctx, left, name);
        if (attributionExpr != null) {
            return attributionExpr;
        }

        if (!(left.type() instanceof KType.ClassType classType)) {
            Log.attribError(new AttribError.NotAStruct(expr.left().region(), left.type()));
            throw new Log.KarinaException();
        }
        var classModel = ctx.model().getClass(classType.pointer());

        var methodCollection = classModel.getMethodCollection(name).filter(ref -> {
            var modifiers = ctx.model().getMethod(ref).modifiers();
            return !Modifier.isStatic(modifiers) &&
                    ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
        });
        var fieldPointer = classModel.getField(name, ref -> {
            var modifiers = ref.modifiers();
            return !Modifier.isStatic(modifiers) &&
                    ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
        });

        if (classType.generics().size() != classModel.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }

        MemberSymbol symbol;
        if (!methodCollection.isEmpty()) {
            symbol = new MemberSymbol.VirtualFunctionSymbol(expr.region(), classType, methodCollection);
        } else if (fieldPointer != null) {
            var field = ctx.model().getField(fieldPointer);

            Map<Generic, KType> mapped = new HashMap<>();
            for (var i = 0; i < classType.generics().size(); i++) {
                var generic = classModel.generics().get(i);
                var type = classType.generics().get(i);
                mapped.put(generic, type);
            }
            var fieldType = Types.projectGenerics(field.type(), mapped);

            symbol = new MemberSymbol.FieldSymbol(fieldPointer, fieldType, classType);
        } else {
            Log.attribError(new AttribError.UnknownField(
                    expr.name().region(),
                    expr.name().value()
            ));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.GetMember(expr.region(), left, expr.name(), symbol));
    }

    private static @Nullable AttributionExpr attribArrayLength(Region region, AttributionContext ctx, KExpr left, String name) {
        if (left.type() instanceof KType.ArrayType arrayType) {
            if (name.equals("size")) {
                var symbol = new MemberSymbol.ArrayLength(region);
                return of(ctx, new KExpr.GetMember(region, left, RegionOf.region(region, name), symbol));
            } else {
                Log.attribError(new AttribError.UnknownField(region, name));
                throw new Log.KarinaException();
            }
        }
        return null;
    }

    private static @Nullable AttributionExpr attribStaticFunctionReference(Region region, AttributionContext ctx, KExpr left, String name) {

        if (!(left instanceof KExpr.Literal(var ignored, var ignored1, var literalSymbol))) {
            return null;
        }
        if (literalSymbol instanceof LiteralSymbol.StaticClassReference(Region regionInner, ClassPointer classPointer)) {
            //TODO search in super types for fields and functions
            var classModel = ctx.model().getClass(classPointer);
            var collection = classModel.getMethodCollection(name);
            collection = collection.filter(ref -> {
                var modifiers = ctx.model().getMethod(ref).modifiers();
                return Modifier.isStatic(modifiers) &&
                       ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
            });
            if (collection.isEmpty()) {
                var modelField = classModel.getField(name, ref -> {
                    var modifiers = ref.modifiers();
                    return Modifier.isStatic(modifiers)
                            && ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
                });
                if (modelField != null) {
                    var type = ctx.model().getField(modelField).type();
                    var symbol = new LiteralSymbol.StaticFieldReference(regionInner, modelField, type);
                    var newLiteral = new KExpr.Literal(region, name, symbol);
                    return of(ctx, newLiteral);
                }
                //TODO combine fields and function in one collection

                Log.temp(region, "No static function or field found");
                throw new Log.KarinaException();
            } else {
                var symbol = new LiteralSymbol.StaticMethodReference(region, collection);
                var newLiteral = new KExpr.Literal(region, name, symbol);
                return of(ctx, newLiteral);
            }
        } else {
            return null;
        }
    }


}
