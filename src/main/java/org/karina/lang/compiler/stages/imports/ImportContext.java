package org.karina.lang.compiler.stages.imports;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Unique;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.SynatxObject;

import java.util.*;

/**
 * Context for resolving imports.
 */
record ImportContext(KTree.KPackage root, Map<String, SpanOf<KTree.KTypeItem>> imports, Map<String, KType.GenericType> scopeGenerics) {

    ImportContext {
        // Copy imports to prevent modification
        imports = new HashMap<>(imports);
        scopeGenerics = new HashMap<>(scopeGenerics);
    }

    public ImportContext insertGenerics(List<KType.GenericType> genericTypes) {
        var genericBuild = new HashMap<String, KType.GenericType>();
        for (var generic : genericTypes) {
            if (genericBuild.containsKey(generic.name())) {
                var first = genericBuild.get(generic.name()).region();
                var second = generic.region();
                Log.importError(new ImportError.DuplicateItem(first, second, generic.name()));
                throw new Log.KarinaException();
            }
            genericBuild.put(generic.name(), generic);
        }
        return insertGenerics(genericBuild);
    }

    private ImportContext insertGenerics(Map<String, KType.GenericType> generics) {

        var newGenerics = new HashMap<>(this.scopeGenerics);
        generics.forEach(
                (k, v) -> {
                    if (newGenerics.containsKey(k)) {
                        var oldRegion = newGenerics.get(k).region();
                        var newRegion = v.region();
                        Log.importError(new ImportError.DuplicateItem(
                                oldRegion, newRegion, k
                        ));
                        throw new Log.KarinaException();
                    } else if (this.imports.containsKey(k)) {
                        var oldRegion = this.imports.get(k).region();
                        var newRegion = v.region();
                        Log.importError(new ImportError.DuplicateItem(
                                oldRegion, newRegion, k
                        ));
                        throw new Log.KarinaException();
                    }
                    newGenerics.put(k, v);
                }
        );
        return new ImportContext(this.root, this.imports, newGenerics);

    }
    public KType resolveType(KType type) {
        return resolveType(type, false);
    }

    public KType resolveType(KType type, boolean canInferGenerics) {
        switch (type) {
            case KType.UnprocessedType(var region, var name, var generics) -> {
                return resolveUnprocessed(region, name, generics, canInferGenerics);
            }
            case KType.ArrayType arrayType -> {
                return new KType.ArrayType(
                        arrayType.region(), resolveType(arrayType.elementType()));
            }
            case KType.FunctionType(
                    var region, var arguments, var returnType, var interfaces
            ) -> {
                return new KType.FunctionType(
                        region, arguments.stream().map(this::resolveType).toList(),
                        returnType == null ? null : resolveType(returnType),
                        interfaces.stream().map(this::resolveType).toList()
                );
            }
            case KType.PrimitiveType primitiveType -> {
                return primitiveType;
            }
            default -> {
                return type;
            }
        }
    }


    private KType resolveUnprocessed(Span region, SpanOf<ObjectPath> path, List<KType> generics, boolean canInferGenerics) {

        KTree.KTypeItem referredItem;
        var name = path.value();
        var head = name.first();
        if (name.tail().isEmpty()) {
            var genericType = getGenericType(head);
            if (genericType != null) {
                if (!generics.isEmpty()) {
                    Log.importError(new ImportError.GenericCountMismatch(
                            region,
                            head,
                            0,
                            generics.size()
                    ));
                    throw new Log.KarinaException();
                }
                return new KType.GenericLink(region, genericType);
            }
            referredItem = getImportedType(head);
        } else {
            var foundItem = this.root.findItem(name);
            if (foundItem instanceof KTree.KTypeItem typeItem) {
                referredItem = typeItem;
            } else {
                referredItem = null;
            }
        }
        if (referredItem == null) {
            //#region Error
            var names = new HashSet<>(this.imports.keySet());
            names.addAll(this.scopeGenerics.keySet());

            Log.importError(new ImportError.UnknownImportType(region, name.last(), names, this.root));
            throw new Log.KarinaException();
            //#endregion
        }

        var resolvedGenerics = generics.stream().map(this::resolveType).toList();
        var genericDefined = !resolvedGenerics.isEmpty();
        if ((genericDefined || !canInferGenerics) && resolvedGenerics.size() != referredItem.generics().size()) {
            Log.importError(
                    new ImportError.GenericCountMismatch(
                        region,
                        referredItem.name().value(),
                        referredItem.generics().size(),
                        resolvedGenerics.size()
                    )
            );
            throw new Log.KarinaException();
        }
        var namedPosition = SpanOf.span(path.region(), referredItem.path());
        return new KType.ClassType(region, namedPosition, resolvedGenerics);

    }

    private @Nullable KTree.KTypeItem getImportedType(String simpleName) {
        var kItem = this.imports.get(simpleName);
        if (kItem == null) {
            return null;
        }
        return kItem.value();
    }

    private @Nullable KType.GenericType getGenericType(String simpleName) {
        return this.scopeGenerics.get(simpleName);
    }


    public static FunctionsAndContext getImports(KTree.KPackage root, KTree.KUnit unit) {

        var importedFunctions = new ArrayList<SpanOf<ObjectPath>>();

        var importedLocations = new HashMap<String, SpanOf<KTree.KTypeItem>>();
        for (var kImport : unit.kImports()) {
            var importType = kImport.importType();
            switch (importType) {
                case SynatxObject.TypeImport.All(var region) -> {
                    var importedUnit = root.findUnit(kImport.path().value());
                    if (importedUnit == null) {
                        Log.importError(new ImportError.NoUnitFound(kImport.path(), root));
                        throw new Log.KarinaException();
                    }
                    for (var item : importedUnit.items()) {
                        var maybeFunction = checkAndAddImport(importedLocations, item, region);
                        if (maybeFunction != null) {
                            importedFunctions.add(maybeFunction);
                        }
                    }
                }
                case SynatxObject.TypeImport.Single(SpanOf<String> name) -> {
                    var importedUnit = root.findUnit(kImport.path().value());
                    if (importedUnit == null) {
                        Log.importError(new ImportError.NoUnitFound(kImport.path(), root));
                        throw new Log.KarinaException();
                    }
                    var item = importedUnit.findItem(name.value());
                    if (item == null) {
                        Log.importError(new ImportError.NoItemFound(
                                name.region(), name.value(),
                                importedUnit, root
                        ));
                        throw new Log.KarinaException();
                    }
                    var maybeFunction = checkAndAddImport(importedLocations, item, name.region());
                    if (maybeFunction != null) {
                        importedFunctions.add(maybeFunction);
                    }
                }
                case SynatxObject.TypeImport.JavaAlias(SpanOf<String> alias) -> {
                    Log.importError(new ImportError.JavaNotSupported(alias.region()));
                    throw new Log.KarinaException();
                }
                case SynatxObject.TypeImport.JavaClass(var region) -> {
                    Log.importError(new ImportError.JavaNotSupported(region));
                    throw new Log.KarinaException();
                }
            }
        }


        for (var item : unit.items()) {
            var maybeFunction = checkAndAddImport(importedLocations, item, item.name().region());
            if (maybeFunction != null) {
                importedFunctions.add(maybeFunction);
            }
        }
        checkFunctionImportCollisions(importedFunctions);
        var context = new ImportContext(root, importedLocations, new HashMap<>());
        return new FunctionsAndContext(importedFunctions, context);
    }

    private static void checkFunctionImportCollisions(List<SpanOf<ObjectPath>> path) {
        var unique = Unique.testUnique(path, (objPath) -> {
            if (objPath.value().size() < 2) {
                Log.temp(objPath.region(), "Imported function is not correctly resolved");
                throw new Log.KarinaException();
            }
            var functionName = objPath.value().last();
            var unitName = objPath.value().elements().get(objPath.value().size() - 2);
            return unitName + "." + functionName;
        });
        if (unique instanceof Unique.Duplicate(var first, var duplicate, var value)) {
            Log.importError(new ImportError.DuplicateItem(
                    first.region(),
                    duplicate.region(),
                    value
            ));
            throw new Log.KarinaException();
        }
    }

    //returns ObjectPath, if the item is a function, otherwise null
    private static @Nullable SpanOf<ObjectPath> checkAndAddImport(
            HashMap<String, SpanOf<KTree.KTypeItem>> importedLocation,
            KTree.KItem item,
            Span region
    ) {
        if (item instanceof KTree.KFunction function) {
            return SpanOf.span(region, function.path());
        } else if (item instanceof KTree.KTypeItem typeItem) {
            var itemName = typeItem.name().value();
            if (importedLocation.containsKey(itemName)) {
                var firstDef = importedLocation.get(itemName).region();
                Log.importError(new ImportError.DuplicateItem(firstDef, region, itemName));
                throw new Log.KarinaException();
            } else {
                importedLocation.put(itemName, SpanOf.span(region, typeItem));
            }
            return null;
        } else {
            Log.invalidState(item.region(), item.getClass(), "import-stage");
            throw new Log.KarinaException();
        }
    }

    record FunctionsAndContext(List<SpanOf<ObjectPath>> functions, ImportContext context){}
}
