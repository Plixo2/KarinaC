package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.model.Model;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.SymbolTable;
import org.karina.lang.compiler.utils.TypeImport;

import java.util.*;

/**
 * Context for resolving types
 */
public record ImportContext(Model model, SymbolTable table) {

    public static ImportContext empty(Model model) {
        return new ImportContext(model, SymbolTable.empty());
    }

    public KType resolveType(KType type) {
        return resolveType(type, false, false);
    }

    public KType resolveType(KType type, boolean mustInferGenerics, boolean canInfer) {
        switch (type) {
            case KType.UnprocessedType(var region, var name, var generics) -> {
                return resolveUnprocessed(region, name, generics, mustInferGenerics, canInfer);
            }
            case KType.ArrayType arrayType -> {
                return new KType.ArrayType(resolveType(arrayType.elementType()));
            }
            case KType.FunctionType(var arguments, var returnType, var interfaces) -> {
                return new KType.FunctionType(
                        arguments.stream().map(this::resolveType).toList(),
                        resolveType(returnType),
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

    private KType resolveUnprocessed(Region region, RegionOf<ObjectPath> path, List<KType> generics, boolean mustInferGenerics, boolean canInfer) {

        ClassPointer clsPointer;
        var name = path.value();
        var head = name.first();
        var isPath = !name.tail().isEmpty();

        if (isPath) {
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
            clsPointer = this.table.getClass(head);
        } else {
            clsPointer = this.model.getClassPointer(name);
        }

        if (clsPointer == null) {
            var names = this.table.availableTypeNames();

            Log.importError(new ImportError.UnknownImportType(region, name.toString(), names));
            throw new Log.KarinaException();
        }
        var cls = this.model.getClass(clsPointer);

        var resolvedGenerics = generics.stream().map(this::resolveType).toList();
        var checkGenerics = !resolvedGenerics.isEmpty()  || !(canInfer || mustInferGenerics);
        if (checkGenerics && resolvedGenerics.size() != cls.generics().size()) {
            Log.importError(
                    new ImportError.GenericCountMismatch(
                        region,
                            cls.name(),
                            cls.generics().size(),
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

        return new KType.ClassType(clsPointer, resolvedGenerics);
    }

    public static SymbolTable.SymbolTableBuilder getBaseSymbolTableBuilder(Model model, KTree.KUnit unit) {
        throw new NullPointerException("");
        //        var builder = SymbolTable.builder();
//
//        var locals = new ArrayList<>(unit.items().stream().map(ref -> RegionOf.region(ref.region(), ref)).toList());
//        var non_locals = new ArrayList<RegionOf<MethodPointer>>();
//        var prelude = new ArrayList<RegionOf<KTree.KItem>>();
//        for (var kImport : unit.kImports()) {
//            var importType = kImport.importType();
//            switch (importType) {
//                case TypeImport.All all -> {
//                    var classPointer = model.getClassPointer(kImport.path());
//                    if (classPointer == null) {
//                        Log.importError(new ImportError.NoUnitFound(
//                                kImport.region(),
//                                kImport.path()
//                        ));
//                        throw new Log.KarinaException();
//                    }
//                    var modelClass = model.getClass(classPointer);
//                    for (var innerClass : modelClass.innerClasses()) {
//                        //import when static
//                    }
//                    for (var method : modelClass.methods()) {
//                        //import when static
//                    }
//                }
//                case TypeImport.Base base -> {
//                    var classPointer = model.getClassPointer(kImport.path());
//                    if (classPointer == null) {
//                        Log.importError(new ImportError.NoUnitFound(
//                                kImport.region(),
//                                kImport.path()
//                        ));
//                        throw new Log.KarinaException();
//                    }
//                }
//                case TypeImport.Names names -> {
//                    var classPointer = model.getClassPointer(kImport.path());
//                    if (classPointer == null) {
//                        Log.importError(new ImportError.NoUnitFound(
//                                kImport.region(),
//                                kImport.path()
//                        ));
//                        throw new Log.KarinaException();
//                    }
//                    var modelClass = model.getClass(classPointer);
//                    for (var innerClass : modelClass.innerClasses()) {
//                        //import when static and matches names
//                    }
//                    for (var method : modelClass.methods()) {
//                        //import when static and matches names
//                    }
//                    //throw error when none found
//                }
//            }
//        }
////        addItems(builder, SymbolTable.SymbolLocation.PRELUDE, prelude);
////        addItems(builder, SymbolTable.SymbolLocation.NON_LOCAL, non_locals);
////        addItems(builder, SymbolTable.SymbolLocation.LOCAL, locals);
//        return builder;
    }


    private static void addItems(SymbolTable.SymbolTableBuilder builder, SymbolTable.SymbolLocation location, List<RegionOf<KTree.KItem>> items) {
//        for (var item : items) {
//            if (item.value() instanceof KTree.KFunction function) {
//                var spanOf = SpanOf.span(item.region(), function);
//                builder.addFunction(location, function.name().value(), spanOf);
//            } else if (item.value() instanceof KTree.KTypeItem typeItem) {
//                var spanOf = SpanOf.span(item.region(), typeItem);
//                builder.addItem(location, typeItem.name().value(), spanOf);
//            }
//        }
    }

}
