package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.symbols.MemberSymbol;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class GetMemberAttrib  {
    public static AttributionExpr attribGetMember(@Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();

        var name = expr.name().value();
        var staticFunc = attribStaticFunctionReference(expr.region(), ctx, left, name, expr.isNextACall());
        if (staticFunc != null) {
            return staticFunc;
        }
        var attributionExpr = attribArrayLength(expr.region(), ctx, left, name);
        if (attributionExpr != null) {
            return attributionExpr;
        }

        if (!(left.type() instanceof KType.ClassType classType)) {
            if (left.type().isVoid()) {
                Log.error(ctx, new AttribError.NotSupportedType(
                        expr.left().region(),
                        KType.NONE
                ));
            } else {
                Log.error(ctx, new AttribError.NotAClass(expr.left().region(), left.type()));
            }
            throw new Log.KarinaException();
        }
        var classModel = ctx.model().getClass(classType.pointer());

        var methods = new ArrayList<UpstreamMethodPointer>();
        putMethodCollectionDeep(ctx, ctx.owningClass(), ctx.protection(), ctx.model(), classType, name, methods, new HashSet<>(), true);
        for (var method : methods) {
            Log.recordType(Log.LogTypes.CALLS, "available pointer", method);
        }
        var fieldPointer = getFieldDeep(ctx, classType.pointer(), name);

        if (classType.generics().size() != classModel.generics().size()) {
            Log.temp(ctx, expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }

        if (!expr.isNextACall()) {
            if (fieldPointer != null) {
                methods = new ArrayList<>();
            }
        }

        MemberSymbol symbol;
        if (!methods.isEmpty()) {
            var pointers = new ArrayList<MethodPointer>();
            for (var method : methods) {
                //classType = method.mappedUpstreamType();
                pointers.add(method.pointer());
            }

            var collection = new MethodCollection(name, pointers);
            symbol = new MemberSymbol.VirtualFunctionSymbol(expr.region(), classType, collection);
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
            reportUnknownMemberError(
                    expr.name().region(),
                    ctx,
                    classType.pointer(),
                    name,
                    false
            );
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.GetMember(expr.region(), left, expr.name(), false, symbol));
    }

    private static @Nullable AttributionExpr attribArrayLength(Region region, AttributionContext ctx, KExpr left, String name) {
        if (left.type() instanceof KType.ArrayType arrayType) {
            if (name.equals("size")) {
                var symbol = new MemberSymbol.ArrayLength(region);
                return of(ctx, new KExpr.GetMember(region, left, RegionOf.region(region, name), false, symbol));
            } else if (name.equals("class")){
                var classType = KType.CLASS_TYPE(arrayType);
                var symbol = new LiteralSymbol.StaticClassReference(region, classType.pointer(), arrayType, true);
                var newLiteral = new KExpr.Literal(region, name, symbol);
                return of(ctx, newLiteral);
            }else {
                Log.error(ctx, new AttribError.UnknownMember(
                        region,
                        arrayType.toString(),
                        name,
                        Set.of(),
                        Set.of("size"),
                        null
                ));
                throw new Log.KarinaException();
            }
        }
        return null;
    }

    private static @Nullable AttributionExpr attribStaticFunctionReference(
            Region region,
            AttributionContext ctx,
            KExpr left,
            String name,
            boolean functionPriority
    ) {

        if (!(left instanceof KExpr.Literal(var ignored, var ignored1, var literalSymbol))) {
            return null;
        }
        if (literalSymbol instanceof LiteralSymbol.StaticClassReference(
                Region regionInner, ClassPointer classPointer, var classType, var implicitGetClass
        )) {
            if (name.equals("class")) {
                var symbol = new LiteralSymbol.StaticClassReference(regionInner, classPointer, classType, true);
                var newLiteral = new KExpr.Literal(region, name, symbol);
                return of(ctx, newLiteral);
            } else if (implicitGetClass) {
                return null;
            }

            //TODO search in super types for fields and functions
            var classModel = ctx.model().getClass(classPointer);
            var collection = classModel.getMethodCollectionShallow(name).filter(ref -> {
                var modifiers = ctx.model().getMethod(ref).modifiers();
                return Modifier.isStatic(modifiers) && ctx.protection()
                                                          .canReference(
                                                                  ctx.owningClass(),
                                                                  ref.classPointer(), modifiers
                                                          );
            });
            var modelField = classModel.getField(name, ref -> {
                        var modifiers = ref.modifiers();
                        return Modifier.isStatic(modifiers)
                                && ctx.protection() .canReference(ctx.owningClass(), ref.classPointer(), modifiers);
            });
            //order of priority, if we want a function or a field
            if (functionPriority) {

                if (!collection.isEmpty()) {
                    var symbol = new LiteralSymbol.StaticMethodReference(region, collection);
                    var newLiteral = new KExpr.Literal(region, name, symbol);
                    return of(ctx, newLiteral);
                } else if (modelField != null) {
                    var type = ctx.model().getField(modelField).type();
                    var symbol = new LiteralSymbol.StaticFieldReference(regionInner, modelField, type);
                    var newLiteral = new KExpr.Literal(region, name, symbol);
                    return of(ctx, newLiteral);
                }

            } else {

                if (modelField != null) {
                    var type = ctx.model().getField(modelField).type();
                    var symbol = new LiteralSymbol.StaticFieldReference(regionInner, modelField, type);
                    var newLiteral = new KExpr.Literal(region, name, symbol);
                    return of(ctx, newLiteral);
                } else if (!collection.isEmpty()) {
                    var symbol = new LiteralSymbol.StaticMethodReference(region, collection);
                    var newLiteral = new KExpr.Literal(region, name, symbol);
                    return of(ctx, newLiteral);
                }

            }

            reportUnknownMemberError(region, ctx, classPointer, name, true);
            throw new Log.KarinaException();

        } else {
            return null;
        }
    }

    private static @Nullable FieldPointer getFieldDeep(
            AttributionContext ctx,
            ClassPointer classPointer,
            String name
    ) {

        var referenceSite = ctx.owningClass();
        var classModel = ctx.model().getClass(classPointer);

        for (var fieldModel : classModel.fields()) {
            var modifiers = fieldModel.modifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!fieldModel.name().equals(name)) {
                continue;
            }
            if (!ctx.protection().canReference(referenceSite, fieldModel.classPointer(), modifiers)) {
                continue;
            }
            return fieldModel.pointer();
        }

        //check for super classes and interfaces
        var superType = classModel.superClass();
        if (superType != null) {
            return getFieldDeep(ctx, superType.pointer(), name);
        }
        return null;
    }

    private static void putMethodCollectionDeep(
            AttributionContext ctx,
            @Nullable ClassPointer referenceSite,
            @Nullable ProtectionChecking protectionChecking,
            Model model,
            KType.ClassType classType,
            @Nullable String name,
            ArrayList<UpstreamMethodPointer> collection,
            Set<ClassPointer> visited,
            boolean start
    ) {

        var logName = "collect in class " + classType;
        Log.beginType(Log.LogTypes.MEMBER, logName);
        Log.recordType(Log.LogTypes.MEMBER, "name", Objects.requireNonNullElse(name, "<all>"), "start", start);
        if (visited.contains(classType.pointer())) {
            return;
        }

        var classModel = model.getClass(classType.pointer());

        outer: for (var method : classModel.methods()) {
            var modifiers = method.modifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            if (name != null) {
                if (!method.name().equals(name)) {
                    continue;
                }
            }
            if (protectionChecking != null && referenceSite != null) {
                if (!protectionChecking.canReference(referenceSite, method.classPointer(), modifiers)) {
                    Log.recordType(Log.LogTypes.MEMBER, "Cannot access ", method.pointer());
                    continue;
                }
            }
            for (var pointer : collection) {
                var signature = pointer.pointer().erasedParameters();
                if (Types.signatureEquals(signature, method.pointer().erasedParameters())) {
                    Log.recordType(Log.LogTypes.MEMBER, "Skipping member method ", method.pointer());
                    continue outer;
                }
            }
            var mappedUpstreamType = new UpstreamMethodPointer(method.pointer(), classType);
            collection.add(mappedUpstreamType);
        }
        for (var upstreamMethodPointer : collection) {
            Log.recordType(Log.LogTypes.MEMBER, "found direct", upstreamMethodPointer.mappedUpstreamType());
        }

        //check for super classes and interfaces
        var superType = Types.getSuperType(ctx, model, classType);
        if (superType != null) {
            putMethodCollectionDeep(ctx, referenceSite, protectionChecking, model, superType, name, collection, visited, false);
        }

        var interfaces = Types.getInterfaces(ctx, model, classType);
        for (var interfaceType : interfaces) {
            putMethodCollectionDeep(ctx, referenceSite, protectionChecking, model, interfaceType, name, collection, visited, false);
        }
        for (var upstreamMethodPointer : collection) {
            Log.recordType(Log.LogTypes.MEMBER, "found overall", upstreamMethodPointer.mappedUpstreamType());
        }

        Log.endType(Log.LogTypes.MEMBER, logName);
    }


    //# region error reporting

    private static void reportUnknownMemberError(
            Region region,
            AttributionContext ctx,
            ClassPointer classType,
            String name,
            boolean staticOnly
    ) {
        var classModel = ctx.model().getClass(classType);

        String nonAccessibleStr = null;

        var methodNames = new HashSet<String>();
        var nonAccessibleMethods = new HashSet<MethodModel>();
        putAllMethods(ctx, classType, new HashSet<>(), methodNames, name, nonAccessibleMethods, staticOnly);
        if (!nonAccessibleMethods.isEmpty()) {
            var method = nonAccessibleMethods.iterator().next();
            nonAccessibleStr = Modifier.toString(method.modifiers()) + " fn " + method.name();
        }

        var fieldNames = new HashSet<String>();
        var nonAccessibleFields = new HashSet<FieldModel>();
        putAllFields(ctx, classType, fieldNames, name, nonAccessibleFields, staticOnly);
        if (!nonAccessibleFields.isEmpty() && nonAccessibleStr == null) {
            var field = nonAccessibleFields.iterator().next();
            nonAccessibleStr = Modifier.toString(field.modifiers()) + field.name() + ": " + field.type();
        }

        Log.error(ctx, new AttribError.UnknownMember(
                region,
                classModel.name(),
                name,
                methodNames,
                fieldNames,
                nonAccessibleStr
        ));

    }

    private static void putAllFields(
            AttributionContext ctx,
            ClassPointer classPointer,
            Set<String> nameCollection,
            String name,
            Set<FieldModel> nonAccessible,
            boolean staticOnly
    ) {

        var referenceSite = ctx.owningClass();
        var classModel = ctx.model().getClass(classPointer);

        for (var fieldModel : classModel.fields()) {
            var modifiers = fieldModel.modifiers();
            if (staticOnly != Modifier.isStatic(modifiers)) {
                continue;
            }

            if (!ctx.protection().canReference(referenceSite, fieldModel.classPointer(), modifiers)) {
                if (fieldModel.name().equals(name)) {
                    nonAccessible.add(fieldModel);
                }
                continue;
            }
            nameCollection.add(fieldModel.name());
        }
        if (staticOnly) {
            return;
        }

        //check for super classes and interfaces
        var superType = classModel.superClass();
        if (superType != null) {
            putAllFields(ctx, superType.pointer(), nameCollection, name, nonAccessible, false);
        }

    }

    private static void putAllMethods(
            AttributionContext ctx,
            ClassPointer classPointer,
            Set<ClassPointer> visited,
            Set<String> nameCollection,
            String name,
            Set<MethodModel> nonAccessible,
            boolean staticOnly
    ) {
        if (visited.contains(classPointer)) {
            return;
        }

        var referenceSite = ctx.owningClass();
        var classModel = ctx.model().getClass(classPointer);


        for (var method : classModel.methods()) {
            var modifiers = method.modifiers();
            if (staticOnly != Modifier.isStatic(modifiers)) {
                continue;
            }

            if (!ctx.protection().canReference(referenceSite, method.classPointer(), modifiers)) {
                if (method.name().equals(name)) {
                    nonAccessible.add(method);
                }
                continue;
            }
            nameCollection.add(method.name());
        }

        if (staticOnly) {
            return;
        }

        var superType = classModel.superClass();
        if (superType != null) {
            putAllMethods(ctx, superType.pointer(), visited, nameCollection, name, nonAccessible, false);
        }

        var interfaces = classModel.interfaces();
        for (var interfaceType : interfaces) {
            putAllMethods(ctx, interfaceType.pointer(), visited, nameCollection, name, nonAccessible, false);
        }
    }

    //# endregion

}
