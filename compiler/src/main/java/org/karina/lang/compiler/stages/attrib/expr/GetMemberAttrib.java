package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.*;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
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
        var staticFunc = attribStaticFunctionReference(expr, ctx, left, name, expr.isNextACall());
        if (staticFunc != null) {
            return staticFunc;
        }
        var arrayLength = attribArrayLength(expr.region(), ctx, left, name);
        if (arrayLength != null) {
            return arrayLength;
        }

        return switch (attribFieldOrMethodOnClass(ctx, expr, left, name)) {
            case MemberResult.Expr(var kExpr) -> {
                yield  of(ctx, kExpr);
            }
            case MemberResult.Symbol(var symbol) -> {
                yield of(ctx, new KExpr.GetMember(expr.region(), left, expr.name(), false, symbol));
            }
        };

    }

    private static MemberResult attribFieldOrMethodOnClass(
            AttributionContext ctx, KExpr.GetMember expr, KExpr left, String name
    ) {
        var leftType = left.type();
        if (!(leftType instanceof KType.ClassType classType)) {
            if (leftType.isVoid()) {
                Log.error(
                        ctx, new AttribError.NotSupportedType(
                        expr.left().region(),
                        KType.NONE
                ));
                throw new Log.KarinaException();
            } else {
                var extensions = attribExtensionMethods(ctx, left, expr.region(), name);
                if (extensions != null) {
                    return new MemberResult.Expr(extensions);
                } else {
                    var nameToReport = name;
                    if (name.equals("<unknown>")) {
                        nameToReport = null;
                    }
                    reportUnknownExtension(expr.name().region(), ctx, leftType, nameToReport, left);
                    throw new Log.KarinaException();
                }
            }
        }
        if (name.equals("<unknown>")) {
            assert ctx.intoContext().infos().allowMissingFields();
            //only a valid name when this was enabled

            reportUnknownMemberError(
                    expr.name().region(),
                    ctx,
                    classType.pointer(),
                    null,
                    left,
                    false
            );
            throw new Log.KarinaException();
        }

        if (expr.isNextACall()) {
            var methodSymbol = attribMethodOnClass(expr.region(), ctx, name, classType);
            if (methodSymbol != null) {
                return new MemberResult.Symbol(methodSymbol);
            }
        }
        var fieldSymbol = attribFieldOnClass(ctx, name, classType);


        if (fieldSymbol != null) {
            return new MemberResult.Symbol(fieldSymbol);
        }

        var extensions = attribExtensionMethods(ctx, left, expr.region(), name);
        if (extensions != null) {
            return new MemberResult.Expr(extensions);
        }

        reportUnknownMemberError(
                expr.name().region(),
                ctx,
                classType.pointer(),
                name,
                left,
                false
        );
        throw new Log.KarinaException();
    }
    sealed interface MemberResult {
        record Symbol(MemberSymbol symbol) implements MemberResult {}
        record Expr(KExpr expr) implements MemberResult {}
    }

    private static @Nullable KExpr attribExtensionMethods(AttributionContext ctx, KExpr left, Region region, String name) {
        var staticMethodCollection = ctx.table().getStaticMethod(name);
        if (staticMethodCollection == null) {
            return null;
        }
        staticMethodCollection = staticMethodCollection.filter(ref -> {
            var extension = ctx.model().getMethod(ref).modifiers();
            return MethodModel.isExtension(extension);
        });
        if (staticMethodCollection.isEmpty()) {
            return null;
        }

        var symbol = new LiteralSymbol.StaticMethodReference(region, staticMethodCollection, left);

        return new KExpr.Literal(
                region,
                name,
                symbol
        );
    }

    private static @Nullable MemberSymbol attribFieldOnClass(AttributionContext ctx, String name, KType.ClassType classType) {
        var owningClass = ctx.model().getClass(ctx.owningClass());
        var classModel = ctx.model().getClass(classType.pointer());
        var fieldPointer = getFieldDeep(ctx, owningClass, classType.pointer(), name);
        if (fieldPointer == null) {
            return null;
        }
        var field = ctx.model().getField(fieldPointer);

        Map<Generic, KType> mapped = new HashMap<>();
        for (var i = 0; i < classType.generics().size(); i++) {
            var generic = classModel.generics().get(i);
            var type = classType.generics().get(i);
            mapped.put(generic, type);
        }
        var fieldType = Types.projectGenerics(field.type(), mapped);

        return new MemberSymbol.FieldSymbol(fieldPointer, fieldType, classType);
    }

    private static @Nullable MemberSymbol attribMethodOnClass(Region region, AttributionContext ctx, String name, KType.ClassType classType) {
        var owningClass = ctx.model().getClass(ctx.owningClass());
        var methods = new ArrayList<UpstreamMethodPointer>();
        putMethodCollectionDeep(ctx, owningClass, ctx.model(), classType, name, methods, new HashSet<>());

        if (methods.isEmpty()) {
            return null;
        }
        var pointers = new ArrayList<MethodPointer>();
        for (var method : methods) {
            pointers.add(method.pointer());
        }

        var collection = new MethodCollection(name, pointers);
        return new MemberSymbol.VirtualFunctionSymbol(region, classType, collection);
    }

    private static @Nullable AttributionExpr attribArrayLength(Region region, AttributionContext ctx, KExpr left, String name) {
        if (left.type() instanceof KType.ArrayType arrayType) {
            if (name.equals("length")) {
                var symbol = new MemberSymbol.ArrayLength(region);
                return of(ctx, new KExpr.GetMember(region, left, RegionOf.region(region, name), false, symbol));
            } else if (name.equals("class")) {
                var classType = KType.CLASS_TYPE(arrayType);
                var symbol = new LiteralSymbol.StaticClassReference(region, classType.pointer(), arrayType, true);
                var newLiteral = new KExpr.Literal(region, name, symbol);
                return of(ctx, newLiteral);
            } else {
                return null;
            }
        }
        return null;
    }

    private static @Nullable AttributionExpr attribStaticFunctionReference(
            KExpr.GetMember expr,
            AttributionContext ctx,
            KExpr left,
            String name,
            boolean isNextACall
    ) {

        if (!(left instanceof KExpr.Literal(var ignored, var ignored1, LiteralSymbol.StaticClassReference(
                Region regionInner, ClassPointer classPointer, var classType, var implicitGetClass
        )))) {
            return null;
        }

        if (name.equals("<unknown>")) {
            assert ctx.intoContext().infos().allowMissingFields();

            reportUnknownMemberError(
                    expr.name().region(),
                    ctx,
                    classPointer,
                    null,
                    left,
                    true
            );
            throw new Log.KarinaException();
        }
        var region = expr.region();


        if (name.equals("class")) {
            var symbol = new LiteralSymbol.StaticClassReference(regionInner, classPointer, classType, true);
            var newLiteral = new KExpr.Literal(region, name, symbol);
            return of(ctx, newLiteral);
        } else if (implicitGetClass) {
            return null;
        }
        var owningClass = ctx.model().getClass(ctx.owningClass());

        //TODO search in super types for fields and functions
        var classModel = ctx.model().getClass(classPointer);


        if (isNextACall) {
            var collection = classModel.getMethodCollectionShallow(name).filter(ref -> {
                var modifiers = ctx.model().getMethod(ref).modifiers();
                return Modifier.isStatic(modifiers) && ctx.protection().isMethodAccessible(owningClass, ref);
            });
            if (!collection.isEmpty()) {
                var symbol = new LiteralSymbol.StaticMethodReference(region, collection, null);
                var newLiteral = new KExpr.Literal(region, name, symbol);
                return of(ctx, newLiteral);
            }
        }

        var modelField = classModel.getField(name, ref -> {
            var modifiers = ref.modifiers();
            return Modifier.isStatic(modifiers)
                    && ctx.protection().isFieldAccessible(owningClass, ref);
        });

        if (modelField != null) {
            var type = ctx.model().getField(modelField).type();
            var symbol = new LiteralSymbol.StaticFieldReference(regionInner, modelField, type);
            var newLiteral = new KExpr.Literal(region, name, symbol);
            return of(ctx, newLiteral);
        }

        reportUnknownMemberError(expr.name().region(), ctx, classPointer, name, left, true);
        throw new Log.KarinaException();

    }

    private static @Nullable FieldPointer getFieldDeep(
            AttributionContext ctx,
            ClassModel owningClass,
            ClassPointer classPointer,
            String name
    ) {
        var classModel = ctx.model().getClass(classPointer);

        for (var fieldModel : classModel.fields()) {
            var modifiers = fieldModel.modifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!fieldModel.name().equals(name)) {
                continue;
            }
            if (!ctx.protection().isFieldAccessible(owningClass, fieldModel)) {
                continue;
            }
            return fieldModel.pointer();
        }

        //check for super classes
        var superType = classModel.superClass();
        if (superType != null) {
            return getFieldDeep(ctx, owningClass, superType.pointer(), name);
        }
        return null;
    }

    private static void putMethodCollectionDeep(
            AttributionContext ctx,
            ClassModel owningClass,
            Model model,
            KType.ClassType classType,
            String name,
            ArrayList<UpstreamMethodPointer> collection,
            Set<ClassPointer> visited
    ) {

        if (visited.contains(classType.pointer())) {
            return;
        }

        var classModel = model.getClass(classType.pointer());

        outer: for (var method : classModel.methods()) {
            var modifiers = method.modifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!method.name().equals(name)) {
                continue;
            }
            if (!ctx.protection().isMethodAccessible(owningClass, method)) {
                continue;
            }
            for (var pointer : collection) {
                var signature = pointer.pointer().erasedParameters();
                if (Types.signatureEquals(signature, method.pointer().erasedParameters())) {
                    continue outer;
                }
            }
            var mappedUpstreamType = new UpstreamMethodPointer(method.pointer(), classType);
            collection.add(mappedUpstreamType);
        }

        //check for super classes and interfaces
        var superType = Types.getSuperType(ctx, model, classType);
        if (superType != null) {
            putMethodCollectionDeep(ctx, owningClass, model, superType, name, collection, visited);
        }

        var interfaces = Types.getInterfaces(ctx, model, classType);
        for (var interfaceType : interfaces) {
            putMethodCollectionDeep(ctx, owningClass, model, interfaceType, name, collection, visited);
        }

    }


    //# region error reporting

    private static void reportUnknownMemberError(
            Region region,
            AttributionContext ctx,
            ClassPointer classType,
            @Nullable String name,
            KExpr left,
            boolean staticOnly
    ) {
        var classModel = ctx.model().getClass(classType);

        var related = new ArrayList<RegionOf<String>>();
        var otherProtected = new ArrayList<RegionOf<String>>();

        var methods = new HashSet<MethodModel>();
        var nonAccessibleMethods = new HashSet<MethodModel>();
        var nonAccessibleMethodsDiffName = new HashSet<MethodModel>();
        putAllMethods(ctx, classType, new HashSet<>(), methods, name, nonAccessibleMethods, nonAccessibleMethodsDiffName, staticOnly);
        for (var method : nonAccessibleMethods) {
            var toString = Modifier.toString(method.modifiers()) + " fn " + method.name();
            related.add(new RegionOf<>(method.region(), toString));
        }
        for (var method : nonAccessibleMethodsDiffName) {
            var toString = Modifier.toString(method.modifiers()) + " fn " + method.name();
            otherProtected.add(new RegionOf<>(method.region(), toString));
        }

        var fields = new HashSet<FieldModel>();
        var nonAccessibleFields = new HashSet<FieldModel>();
        var nonAccessibleFieldDiffName = new HashSet<FieldModel>();
        putAllFields(ctx, classType, fields, name, nonAccessibleFields, nonAccessibleFieldDiffName, staticOnly);
        for (var field : nonAccessibleFields) {
            var toString = Modifier.toString(field.modifiers()) + " " + field.name() + ": " + field.type();
            related.add(new RegionOf<>(field.region(), toString));
        }
        for (var field : nonAccessibleFieldDiffName) {
            var toString = Modifier.toString(field.modifiers()) + " " + field.name() + ": " + field.type();
            otherProtected.add(new RegionOf<>(field.region(), toString));
        }
        var extensionMethods = ctx.table().getAllExtensionMethods(ctx.model());

        try {
            var subContext = ctx.intoContext().uncheckedSubContext();
            var subCtx = ctx.withNewContext(subContext);

            for (var extensionMethod : extensionMethods) {
                if (extensionMethod.signature().parameters().isEmpty()) {
                    continue;
                }
                var firstParameter = extensionMethod.signature().parameters().getFirst();
                var mapped = new HashMap<Generic, KType>();
                for (var generic : extensionMethod.generics()) {
                    var type = KType.Resolvable.newInstanceFromGeneric(generic);
                    mapped.put(generic, type);
                }
                var firstType = Types.projectGenerics(firstParameter, mapped);
                if (subCtx.getConversion(region, firstType, left, true, false) != null) {
                    methods.add(extensionMethod);
                }
            }

        } catch(Log.KarinaException e) {
            // ignore, already in error state
        }
        var classes = new HashSet<ClassModel>();
        if (staticOnly) {
            classes.addAll(classModel.innerClasses());
        }

        Log.error(ctx, new AttribError.UnknownMember(
                region,
                classModel.name(),
                name,
                methods,
                fields,
                classes,
                related,
                otherProtected
        ));

    }

    private static void reportUnknownExtension(
            Region region,
            AttributionContext ctx,
            KType leftType,
            @Nullable String name,
            KExpr left
    ) {

        var methods = new HashSet<MethodModel>();

        var extensionMethods = ctx.table().getAllExtensionMethods(ctx.model());

        try {
            var subContext = ctx.intoContext().uncheckedSubContext();
            var subCtx = ctx.withNewContext(subContext);

            for (var extensionMethod : extensionMethods) {
                if (extensionMethod.signature().parameters().isEmpty()) {
                    continue;
                }
                var firstParameter = extensionMethod.signature().parameters().getFirst();
                var mapped = new HashMap<Generic, KType>();
                for (var generic : extensionMethod.generics()) {
                    var type = KType.Resolvable.newInstanceFromGeneric(generic);
                    mapped.put(generic, type);
                }
                var firstType = Types.projectGenerics(firstParameter, mapped);
                if (subCtx.getConversion(region, firstType, left, true, false) != null) {
                    methods.add(extensionMethod);
                }
            }

        } catch(Log.KarinaException e) {
            // ignore, already in error state
        }

        var classes = new HashSet<ClassModel>();
        var related = new ArrayList<RegionOf<String>>();
        var otherProtected = new ArrayList<RegionOf<String>>();
        var fields = new HashSet<FieldModel>();

        Log.error(ctx, new AttribError.UnknownMember(
                region,
                leftType.toString(),
                name,
                methods,
                fields,
                classes,
                related,
                otherProtected
        ));

    }

    private static void putAllFields(
            AttributionContext ctx,
            ClassPointer classPointer,
            Set<FieldModel> collection,
            @Nullable String name,
            Set<FieldModel> nonAccessible,
            Set<FieldModel> nonAccessibleDifferentName,
            boolean staticOnly
    ) {

        var owningClass = ctx.model().getClass(ctx.owningClass());
        var classModel = ctx.model().getClass(classPointer);

        for (var fieldModel : classModel.fields()) {
            var modifiers = fieldModel.modifiers();
            if (staticOnly != Modifier.isStatic(modifiers)) {
                continue;
            }

            if (!ctx.protection().isFieldAccessible(owningClass, fieldModel)) {
                if (fieldModel.name().equals(name)) {
                    nonAccessible.add(fieldModel);
                } else {
                    nonAccessibleDifferentName.add(fieldModel);
                }
                continue;
            }
            collection.add(fieldModel);
        }
        if (staticOnly) {
            return;
        }

        //check for super classes and interfaces
        var superType = classModel.superClass();
        if (superType != null) {
            putAllFields(ctx, superType.pointer(), collection, name, nonAccessible, nonAccessibleDifferentName, false);
        }

    }

    private static void putAllMethods(
            AttributionContext ctx,
            ClassPointer classPointer,
            Set<ClassPointer> visited,
            Set<MethodModel> collection,
            @Nullable String name,
            Set<MethodModel> nonAccessible,
            Set<MethodModel> nonAccessibleDifferentName,
            boolean staticOnly
    ) {
        if (visited.contains(classPointer)) {
            return;
        }

        var owningClass = ctx.model().getClass(ctx.owningClass());
        var classModel = ctx.model().getClass(classPointer);


        for (var method : classModel.methods()) {
            var modifiers = method.modifiers();
            if (staticOnly != Modifier.isStatic(modifiers)) {
                continue;
            }
            if (method.name().startsWith("<")) {
                continue;
            }

            if (!ctx.protection().isMethodAccessible(owningClass, method)) {
                if (method.name().equals(name)) {
                    nonAccessible.add(method);
                } else if (!method.classPointer().isRoot()) {
                    nonAccessibleDifferentName.add(method);
                }
                continue;
            }
            collection.add(method);
        }

        if (staticOnly) {
            return;
        }

        var superType = classModel.superClass();
        if (superType != null) {
            putAllMethods(ctx, superType.pointer(), visited, collection, name, nonAccessible, nonAccessibleDifferentName, false);
        }

        var interfaces = classModel.interfaces();
        for (var interfaceType : interfaces) {
            putAllMethods(ctx, interfaceType.pointer(), visited, collection, name, nonAccessible, nonAccessibleDifferentName, false);
        }
    }

    //# endregion

}
