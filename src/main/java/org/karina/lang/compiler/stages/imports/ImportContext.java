package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.utils.SpanOf;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.SymbolTable;
import org.karina.lang.compiler.utils.TypeImport;

import java.util.*;

/**
 * Context for resolving types
 */
record ImportContext(KTree.KPackage root, SymbolTable table) {

    KType resolveType(KType type) {
        return resolveType(type, false, false);
    }

    KType resolveType(KType type, boolean mustInferGenerics, boolean canInfer) {
        switch (type) {
            case KType.UnprocessedType(var region, var name, var generics) -> {
                return resolveUnprocessed(region, name, generics, mustInferGenerics, canInfer);
            }
            case KType.ArrayType arrayType -> {
                return new KType.ArrayType(resolveType(arrayType.elementType()));
            }
            case KType.FunctionType(var arguments, var returnType, var interfaces
            ) -> {
                return new KType.FunctionType(
                        arguments.stream().map(this::resolveType).toList(),
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

    private KType resolveUnprocessed(Span region, SpanOf<ObjectPath> path, List<KType> generics, boolean mustInferGenerics, boolean canInfer) {

        KTree.KTypeItem referredItem;
        var name = path.value();
        var head = name.first();

        if (name.tail().isEmpty()) {
            var genericType = this.table.getGeneric(head);
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
                return new KType.GenericLink(genericType);
            }

            referredItem = this.table.getItem(head);
        } else {
            var foundItem = KTree.findRelativeItem(this.root, name);
            if (foundItem instanceof KTree.KTypeItem typeItem) {
                referredItem = typeItem;
            } else {
                referredItem = null;
            }
        }

        if (referredItem == null) {
            //#region Error
            var names = this.table.availableTypeNames();

            Log.importError(new ImportError.UnknownImportType(region, name.toString(), names, this.root));
            throw new Log.KarinaException();
            //#endregion
        }

        var resolvedGenerics = generics.stream().map(this::resolveType).toList();
        var checkGenerics = !resolvedGenerics.isEmpty()  || !(canInfer || mustInferGenerics);
        if (checkGenerics && resolvedGenerics.size() != referredItem.generics().size()) {
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
        if (mustInferGenerics && !generics.isEmpty()) {
            var areAllAny = resolvedGenerics.stream().allMatch(ref -> ref instanceof KType.AnyClass);
            if (!areAllAny) {
                Log.temp(region, "Generics must be inferred, they cannot be checked at runtime");
                throw new Log.KarinaException();
            }
        }

        var namedPosition = referredItem.path();
        return new KType.ClassType(namedPosition, resolvedGenerics);

    }

    public static SymbolTable.SymbolTableBuilder getImports(KTree.KPackage root, KTree.KUnit unit) {
        var builder = SymbolTable.builder();

        var locals = new ArrayList<>(unit.items().stream().map(ref -> SpanOf.span(ref.region(), ref)).toList());
        var non_locals = new ArrayList<SpanOf<KTree.KItem>>();
        var prelude = new ArrayList<SpanOf<KTree.KItem>>();
        for (var kImport : unit.kImports()) {
            var importType = kImport.importType();
            switch (importType) {
                case TypeImport.All(var region) -> {
                    var importedUnit = root.findUnit(kImport.path().value());
                    if (importedUnit == null) {
                        Log.importError(new ImportError.NoUnitFound(kImport.path(), root));
                        throw new Log.KarinaException();
                    }
                    non_locals.addAll(importedUnit.items().stream().map(ref -> SpanOf.span(region, ref)).toList());
                }
                case TypeImport.Single(SpanOf<String> name) -> {
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
                    non_locals.add(new SpanOf<>(name.region(), item));
                }
                case TypeImport.JavaAlias(SpanOf<String> alias) -> {
                    Log.importError(new ImportError.JavaNotSupported(alias.region()));
                    throw new Log.KarinaException();
                }
                case TypeImport.JavaClass(var region) -> {
                    Log.importError(new ImportError.JavaNotSupported(region));
                    throw new Log.KarinaException();
                }
            }
        }
        addItems(builder, SymbolTable.SymbolLocation.PRELUDE, prelude);
        addItems(builder, SymbolTable.SymbolLocation.NON_LOCAL, non_locals);
        addItems(builder, SymbolTable.SymbolLocation.LOCAL, locals);
        return builder;
    }

    private static void addItems(SymbolTable.SymbolTableBuilder builder, SymbolTable.SymbolLocation location, List<SpanOf<KTree.KItem>> items) {
        for (var item : items) {
            if (item.value() instanceof KTree.KFunction function) {
                var spanOf = SpanOf.span(item.region(), function);
                builder.addFunction(location, function.name().value(), spanOf);
            } else if (item.value() instanceof KTree.KTypeItem typeItem) {
                var spanOf = SpanOf.span(item.region(), typeItem);
                builder.addItem(location, typeItem.name().value(), spanOf);
            }
        }
    }

}
