package org.karina.lang.compiler.stages.imports;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.*;

import java.util.*;

/*
 * Import table for a single file.
 * This structure is immutable (without inner mutability).
 * Top level classes will append to this table and will get a new table in return.
 * This filled-in table will be passes to all items and subclasses, that will append to it.
 */
public record ImportTable(
        Model model,
        ImmutableMap<String, ImportEntry<ClassPointer>> classes,
        ImmutableMap<String, ImportEntry<Generic>> generics,
        //list of UntypedMethodCollection, as we dont know the signature yet, see 'UntypedMethodCollection'
        ImmutableMap<String, ImportEntry<UntypedMethodCollection>> untypedStaticMethods,
        //for prelude methods, we know the signature
        ImmutableMap<String, ImportEntry<MethodCollection>> typedStaticMethods,
        ImmutableMap<String, ImportEntry<FieldPointer>> staticFields
) {
    public ImportTable(Model model) {
        this(model, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
    }

    public ImportTable {
//        classes = Map.copyOf(classes);
//        generics = Map.copyOf(generics);
//        staticMethods = Map.copyOf(staticMethods);
//        staticFields = Map.copyOf(staticFields);
    }

    public KType importType(Region region, KType kType) {
        return importType(region, kType, ImportGenericBehavior.DEFAULT);
    }

    public KType importType(Region region, KType kType, ImportGenericBehavior flags) {
        return switch (kType) {
            case KType.ArrayType arrayType -> {
                yield new KType.ArrayType(importType(region, arrayType.elementType()));
            }
            case KType.FunctionType functionType -> {
                var returnType = importType(region, functionType.returnType());
                var arguments = functionType.arguments().stream().map(ref -> importType(region, ref)).toList();
                var interfaces = new ArrayList<>(functionType.interfaces().stream().map(ref -> {
                    var imported = importType(region, ref);
                    if (!(imported instanceof KType.ClassType classType)) {
                        Log.attribError(new AttribError.NotAClass(region, imported));
                        throw new Log.KarinaException();
                    }
                    return classType;
                }).toList());

                var doesReturn = !returnType.isVoid();
                var defaultInterface = KType.FUNCTION_BASE(this.model, arguments.size(), doesReturn);
                if (defaultInterface == null) {
                    Log.temp(region, "Cannot find default interface for " + arguments.size() + " arguments and return type " + returnType);
                    throw new Log.KarinaException();
                }
                //TODO extract, duplicate in ClosureAttrib
                var alreadyAdded =
                        interfaces.stream().anyMatch(ref -> ref.pointer().equals(defaultInterface));
                if (!alreadyAdded) {
                    var totalGenerics = arguments.size() + (doesReturn ? 1 : 0);

                    var classModel = this.model.getClass(defaultInterface);
                    if (classModel.generics().size() != totalGenerics) {
                        Log.temp(region, "Expected " + totalGenerics + " generics, but got " +
                                classModel.generics().size()
                        );
                        throw new Log.KarinaException();
                    }

                    var generics = new ArrayList<KType>();
                    for (var i = 0; i < totalGenerics; i++) {
                        generics.add(new KType.Resolvable());
                    }
                    var classType = new KType.ClassType(defaultInterface, generics);

                    interfaces.add(classType);
                }

                yield new KType.FunctionType(arguments, returnType, interfaces);
            }
            case KType.GenericLink genericLink -> genericLink;
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> resolvable;
            case KType.ClassType classType -> {
                //revalidate class pointers
                yield importUnprocessedType(region, classType.pointer().path(), classType.generics(), flags);
            }
            case KType.UnprocessedType unprocessedType -> {
                yield importUnprocessedType(unprocessedType.region(), unprocessedType.name().value(), unprocessedType.generics(), flags);
            }
            case KType.VoidType _ -> KType.NONE;
        };
    }

    private KType importUnprocessedType(Region region, ObjectPath path, List<KType> generics, ImportGenericBehavior flags) {

        if (path.isEmpty()) {
            Log.temp(region, "Empty path, this should not happen");
            throw new Log.KarinaException();
        }

        //first check if the first element is imported, or a generic
        var head = path.first();
        if (path.tail().isEmpty()) {
            //first check if it is a generic
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
        }
        var classPointer = getClassPointer(region, path);


        var classModel = this.model.getClass(classPointer);

        var expectedSize = classModel.generics().size();
        var newGenerics = generics.stream().map(ref -> importType(region, ref)).toList();

        boolean checkGenericLength = flags == ImportGenericBehavior.INSTANCE_CHECK || flags == ImportGenericBehavior.DEFAULT;
        switch (flags) {
            case DEFAULT -> {
                //unused
            }
            case INSTANCE_CHECK -> {
                //we fill in all generics with KType.ROOT, as they cannot be checked at runtime
                if (newGenerics.isEmpty()) {
                    newGenerics = new ArrayList<>();
                    for (var i = 0; i < expectedSize; i++) {
                        newGenerics.add(KType.ROOT);
                    }
                } else {
                    for (var newGeneric : newGenerics) {
                        if (!(newGeneric.isRoot())) {
                            Log.temp(region, "Cannot use generics in instance check");
                            throw new Log.KarinaException();
                        }
                    }
                }
            }
            case OBJECT_CREATION -> {
                //if some are defined, we check for length, otherwise we infer the fieldType later
                if (!newGenerics.isEmpty()) {
                    checkGenericLength = true;
                }
            }
        }


        if (checkGenericLength && expectedSize != newGenerics.size()) {
            Log.importError(new ImportError.GenericCountMismatch(
                    region,
                    classModel.name(), expectedSize,
                    newGenerics.size()
            ));
            throw new Log.KarinaException();
        }

        return new KType.ClassType(classPointer, newGenerics);

    }

    public ClassPointer getClassPointer(Region region, ObjectPath path) {

        var head = path.first();
        boolean hasPathDefined = !path.tail().isEmpty();
        ClassPointer classPointer;
        if (!hasPathDefined) {
            classPointer = this.getClass(head);
        } else {
            classPointer = this.getClassNested(head, path.tail());
            if (classPointer == null) {
                classPointer = this.model.getClassPointer(region, path);
            }
        }

        if (classPointer == null) {
            //error reporting. Collect all names and give them as suggestions
            var available = availableTypeNames();
            Log.importError(new ImportError.UnknownImportType(region, path.mkString("."), available));
            throw new Log.KarinaException();
        }

        return classPointer;
    }

    public Set<String> availableTypeNames() {
        var keys = new HashSet<>(this.classes.keySet());
        for (var objectPath : this.model.getBinaryClasses()) {
            keys.add(objectPath.path().mkString("::"));
        }
        for (var objectPath : this.model.getUserClasses()) {
            keys.add(objectPath.path().mkString("::"));
        }

        keys.addAll(this.generics.keySet());
        return keys;
    }

    public Set<String> availableItemNames() {
        var keys = new HashSet<>(this.classes.keySet());
        keys.addAll(this.staticFields.keySet());
        keys.addAll(this.typedStaticMethods.keySet());
        keys.addAll(this.untypedStaticMethods.keySet());
        return keys;
    }

    private @Nullable Generic getGeneric(String name) {
        var entry = this.generics.get(name);
        if (entry == null) {
            return null;
        }
        return entry.reference();
    }

    private @Nullable ClassPointer getClassNested(String first, ObjectPath tail) {

        if (this.classes.containsKey(first)) {
            var topLevel = Objects.requireNonNull(this.classes.get(first)).reference();
            var referedClassModel = this.model.getClass(topLevel);
            for (var element : tail.elements()) {
                boolean found = false;
                for (var innerClass : referedClassModel.innerClasses()) {
                    if (innerClass.name().equals(element)) {
                        referedClassModel = innerClass;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return null;
                }
            }
            return referedClassModel.pointer();
        }
        return null;
    }

    public @Nullable ClassPointer getClass(String name) {
        if (this.classes.containsKey(name)) {
            return Objects.requireNonNull(this.classes.get(name)).reference();
        }
        return null;
    }


    public ImportTable addClass(Region declarationRegion, String name, ClassPointer pointer, boolean declaredExplicit, boolean prelude) {
        var newClasses = new HashMap<>(this.classes);
        var declare = testDuplicate(newClasses, declarationRegion, name, declaredExplicit);
        if (declare) {
            newClasses.put(name, new ImportEntry<>(declarationRegion, pointer, declaredExplicit, prelude));
        }
        return new ImportTable(this.model, ImmutableMap.copyOf(newClasses), this.generics, this.untypedStaticMethods, this.typedStaticMethods, this.staticFields);
    }

    /*
     * Add a generic to the table.
     * they always overwrite existing generics
     */
    public ImportTable addGeneric(Region declarationRegion, Generic generic) {
        var newGenerics = new HashMap<>(this.generics);
        testDuplicate(newGenerics, declarationRegion, generic.name(), true);
        newGenerics.put(generic.name(), new ImportEntry<>(declarationRegion, generic, true, false));
        return new ImportTable(this.model, this.classes, ImmutableMap.copyOf(newGenerics), this.untypedStaticMethods, this.typedStaticMethods, this.staticFields);
    }

    /**
     * TODO Replace with {@link MethodCollection}
     */
    public @NotNull ImportTable addStaticMethod(Region declarationRegion, String name, ClassPointer classPointer, boolean declaredExplicit, boolean prelude) {
        var newStaticMethods = new HashMap<>(this.untypedStaticMethods);
        var declare = testDuplicate(newStaticMethods, declarationRegion, name, declaredExplicit);
        if (declare) {
            var collection = new UntypedMethodCollection(name, classPointer);
            newStaticMethods.put(name, new ImportEntry<>(declarationRegion, collection, declaredExplicit, prelude));
        }
        var newTypedStaticMethods = new HashMap<>(this.typedStaticMethods);
        //remove any duplicates
        newTypedStaticMethods.remove(name);

        return new ImportTable(this.model, this.classes, this.generics, ImmutableMap.copyOf(newStaticMethods), ImmutableMap.copyOf(newTypedStaticMethods), this.staticFields);
    }

    public @NotNull ImportTable addPreludeMethods(Region declarationRegion, String name, List<MethodPointer> methods) {
        if (this.untypedStaticMethods.containsKey(name)) {
            return this;
        }
        var newTypedStaticMethods = new HashMap<>(this.typedStaticMethods);
        newTypedStaticMethods.remove(name); // remove any duplicates
        var typedCollection = new MethodCollection(name, methods);
        newTypedStaticMethods.put(name, new ImportEntry<>(declarationRegion, typedCollection, false, true));

        return new ImportTable(this.model, this.classes, this.generics, this.untypedStaticMethods, ImmutableMap.copyOf(newTypedStaticMethods), this.staticFields);
    }

    public @NotNull ImportTable addStaticField(Region declarationRegion, String name, FieldPointer reference, boolean declaredExplicit, boolean prelude) {
        var newStaticFields = new HashMap<>(this.staticFields);
        var declare = testDuplicate(newStaticFields, declarationRegion, name, declaredExplicit);
        if (declare) {
            newStaticFields.put(name, new ImportEntry<>(declarationRegion, reference, declaredExplicit, prelude));
        }
        return new ImportTable(this.model, this.classes, this.generics, this.untypedStaticMethods, this.typedStaticMethods, ImmutableMap.copyOf(newStaticFields));
    }

    /**
     * @return true if the entry should be added to the table
     */
    private <T> boolean testDuplicate(
            Map<String, ImportEntry<T>> objects, Region declarationRegion, String name,
            boolean declaredExplicit
    ) {
        if (!objects.containsKey(name)) {
            return true;
        }
        var entry = objects.get(name);
        if (entry.wasDeclaredExplicit() && declaredExplicit) {
            Log.importError(
                    new ImportError.DuplicateItem(entry.definedRegion(), declarationRegion, name)
            );
            throw new Log.KarinaException();
        }
        return declaredExplicit || !entry.wasDeclaredExplicit();
    }

    public ImportTable removeGenerics() {
        return new ImportTable(this.model, this.classes, ImmutableMap.of(), this.untypedStaticMethods, this.typedStaticMethods, this.staticFields);
    }

    public record ImportEntry<T>(Region definedRegion, T reference, boolean wasDeclaredExplicit, boolean prelude) { }


    /**
     * Used to determine how deal with generics when resolving types.
     * DEFAULT: import as is, check count of generics.
     * INSTANCE_CHECK: The type is not allowed to define generics other than KType.ROOT (java.lang.Object).
     *                 Replace potential generics with KType.ROOT, when not defined.
     *                 Also check the count of generics when defined.
     *                 Only used in instance checks (if .. is Object cast) and match expressions.
     * OBJECT_CREATION: Generics can be omitted, they can be inferred from the field types.
     *                  Check the count of generics, if defined.
     *                  Only used for object creation.
     */
    public enum ImportGenericBehavior {
        DEFAULT,
        INSTANCE_CHECK,
        OBJECT_CREATION;
    }


    public void logImport() {
        if (Log.LogTypes.IMPORTS.isVisible()) {
            Log.beginType(Log.LogTypes.IMPORTS, "generics");
            for (var entry : this.generics.entrySet()) {
                var entryValue = entry.getValue();
                if (entryValue.prelude()) continue;
                var name = entry.getKey();
                Log.record(name, entryValue.reference().name(), "explicit: ", entryValue.wasDeclaredExplicit());
            }
            Log.endType(Log.LogTypes.IMPORTS, "generics");

            Log.beginType(Log.LogTypes.IMPORTS, "classes");
            for (var entry : this.classes.entrySet()) {
                var entryValue = entry.getValue();
                if (entryValue.prelude()) continue;
                var name = entry.getKey();
                Log.record(name, entryValue.reference(), "explicit: ", entryValue.wasDeclaredExplicit());
            }
            Log.endType(Log.LogTypes.IMPORTS, "classes");

            Log.beginType(Log.LogTypes.IMPORTS, "untyped static methods");
            for (var entry : this.untypedStaticMethods.entrySet()) {
                var entryValue = entry.getValue();
                if (entryValue.prelude()) continue;
                var name = entry.getKey();
                Log.record(name, entryValue.reference().name(), "explicit: ", entryValue.wasDeclaredExplicit());
            }
            Log.endType(Log.LogTypes.IMPORTS, "untyped static methods");

            Log.beginType(Log.LogTypes.IMPORTS, "typed static methods");
            for (var entry : this.typedStaticMethods.entrySet()) {
                var entryValue = entry.getValue();
                if (entryValue.prelude()) continue;
                var name = entry.getKey();
                Log.record(name, entryValue.reference().name(), "explicit: ", entryValue.wasDeclaredExplicit());
            }
            Log.endType(Log.LogTypes.IMPORTS, "typed static methods");

            Log.beginType(Log.LogTypes.IMPORTS, "static fields");

            for (var entry : this.staticFields.entrySet()) {
                var entryValue = entry.getValue();
                if (entryValue.prelude()) continue;
                var name = entry.getKey();
                Log.record(name, entryValue.reference().name(), "explicit: ", entryValue.wasDeclaredExplicit());
            }

            Log.endType(Log.LogTypes.IMPORTS, "static fields");


        }
    }
}
