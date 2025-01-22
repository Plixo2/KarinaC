package org.karina.lang.compiler.stages.imports;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.*;

public record ImportTable(
        JKModel model,
        Map<String, ImportEntry<ClassPointer>> classes,
        Map<String, ImportEntry<Generic>> generics,
        Map<String, ImportEntry<List<MethodPointer>>> staticMethods,
        Map<String, ImportEntry<FieldPointer>> staticFields
) {
    public ImportTable(JKModel model) {
        this(model, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public ImportTable {
        classes = Map.copyOf(classes);
        generics = Map.copyOf(generics);
        staticMethods = Map.copyOf(staticMethods);
        staticFields = Map.copyOf(staticFields);
    }



    public KType importType(Region region, KType kType) {
        switch (kType) {
            case KType.AnyClass anyClass -> {
                return new KType.ClassType(ClassPointer.ROOT, List.of());
            }
            case KType.ArrayType arrayType -> {
                return new KType.ArrayType(importType(region, arrayType.elementType()));
            }
            case KType.FunctionType functionType -> {
                var returnType = importType(region, functionType.returnType());
                var arguments = functionType.arguments().stream().map(ref -> importType(region, ref)).toList();
                var interfaces = functionType.interfaces().stream().map(ref -> importType(region, ref)).toList();
                return new KType.FunctionType(arguments, returnType, interfaces);
            }
            case KType.GenericLink genericLink -> {
                return genericLink;
            }
            case KType.PrimitiveType primitiveType -> {
                return primitiveType;
            }
            case KType.Resolvable resolvable -> {
                return resolvable;
            }
            case KType.ClassType classType -> {
                return importUnprocessedType(region, classType.pointer().path(), classType.generics());
            }
            case KType.UnprocessedType unprocessedType -> {
                return importUnprocessedType(region, unprocessedType.name().value(), unprocessedType.generics());
            }
        }
    }


    private KType importUnprocessedType(Region region, ObjectPath path, List<KType> generics) {

        var head = path.first();
        boolean hasPathDefined = !path.tail().isEmpty();
        ClassPointer classPointer = null;
        if (!hasPathDefined) {
            var generic = getGeneric(head);
            if (generic != null) {
                if (!generics.isEmpty()) {
                    Log.importError(new ImportError.GenericCountMismatch(
                            region,
                            head,
                            0,
                            generics.size()
                    ));
                    throw new Log.KarinaException();
                }
                return new KType.GenericLink(generic);
            }
            classPointer = this.getClass(head);
        }
        if (classPointer == null) {
            classPointer = this.model.getClassPointer(path);
        }

        if (classPointer == null) {
            var available = availableTypeNames();
            Log.importError(new ImportError.UnknownImportType(region, path.mkString("."), available));
            throw new Log.KarinaException();
        }
        var classModel = this.model.getClass(classPointer);

        var newGenerics = generics.stream().map(ref -> importType(region, ref)).toList();
        if (classModel.generics().size() != newGenerics.size()) {
            Log.importError(new ImportError.GenericCountMismatch(
                    region,
                    classModel.name(),
                    classModel.generics().size(),
                    newGenerics.size()
            ));
            throw new Log.KarinaException();
        }

        return new KType.ClassType(classPointer, newGenerics);

    }


    public Set<String> availableTypeNames() {
        var keys = new HashSet<>(this.classes.keySet());
        keys.addAll(this.generics.keySet());
        return keys;
    }


    private @Nullable Generic getGeneric(String name) {
        var entry = this.generics.get(name);
        if (entry == null) {
            return null;
        }
        return entry.reference();
    }

    private @Nullable ClassPointer getClass(String name) {
        if (this.classes.containsKey(name)) {
            return this.classes.get(name).reference();
        }
        return null;
    }

    public ImportTable addClass(Region declarationRegion, String name, ClassPointer reference, boolean declared) {
        var newClasses = new HashMap<>(this.classes);
        var declare = testDuplicate(newClasses, declarationRegion, name, declared);
        if (declare) {
            newClasses.put(name, new ImportEntry<>(declarationRegion, reference, declared));
        }
        return new ImportTable(this.model, newClasses, this.generics, this.staticMethods, this.staticFields);
    }

    public ImportTable addGeneric(Region declarationRegion, Generic generic) {
        var newGenerics = new HashMap<>(this.generics);
        testDuplicate(newGenerics, declarationRegion, generic.name(), true);
        newGenerics.put(generic.name(), new ImportEntry<>(declarationRegion, generic, true));
        return new ImportTable(this.model, this.classes, newGenerics, this.staticMethods, this.staticFields);
    }

    public @NotNull ImportTable addStaticMethod(Region declarationRegion, String name, List<MethodPointer> reference, boolean declared) {
        var newStaticMethods = new HashMap<>(this.staticMethods);
        var declare = testDuplicate(newStaticMethods, declarationRegion, name, declared);
        if (declare) {
            newStaticMethods.put(name, new ImportEntry<>(declarationRegion, reference, declared));
        }
        return new ImportTable(this.model, this.classes, this.generics, newStaticMethods, this.staticFields);
    }


    public @NotNull ImportTable addStaticField(Region declarationRegion, String name, FieldPointer reference, boolean declared) {
        var newStaticFields = new HashMap<>(this.staticFields);
        var declare = testDuplicate(newStaticFields, declarationRegion, name, declared);
        if (declare) {
            newStaticFields.put(name, new ImportEntry<>(declarationRegion, reference, declared));
        }
        return new ImportTable(this.model, this.classes, this.generics, this.staticMethods, newStaticFields);
    }

    /**
     * @return true if the entry should be added to the table
     */
    private <T> boolean testDuplicate(
            Map<String, ImportEntry<T>> objects, Region declarationRegion, String name,
            boolean declared
    ) {
        if (!objects.containsKey(name)) {
            return true;
        }
        var entry = objects.get(name);
        if (entry.wasDeclared() && declared) {
            Log.importError(
                    new ImportError.DuplicateItem(entry.definedRegion(), declarationRegion, name));
            throw new Log.KarinaException();
        }
        return declared || !entry.wasDeclared();
    }

    private record ImportEntry<T>(Region definedRegion, T reference, boolean wasDeclared) { }
}
