package org.karina.lang.compiler.stages.imports.table;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.*;

import java.util.*;
import java.util.function.BiConsumer;

/*
 * Import table for importing types.
 * This is the first importing step
 * This structure is immutable (without inner mutability).
 * Top level classes will append to this table and will get a new table in return.
 * This filled-in table will be passes to all items and subclasses, that will append to it.
 *
 * TODO create a mutable linked list for faster mutation and then create one mutable table at the end.
 *  Also separate the generics, or find another place
 *  this is very slow, should be replaced by faster lookup and insertion
 *  every insert will copy the whole table. So the ImportTable is created for each:
 *  import, class, subClass, method and generic.
 *  prelude alone has > 300 entries.
 */
public record FirstPassImportTable(
        Context c, Model model, ImmutableMap<String, ImportEntry<ClassPointer>> classes,
        ImmutableMap<String, ImportEntry<Generic>> generics,

        //list of UntypedMethodCollection, as we dont know the signature yet, see 'UntypedMethodCollection'
        ImmutableMap<String, ImportEntry<UntypedMethodCollection>> untypedStaticMethods,
        //for prelude methods, we know the signature
        ImmutableMap<String, ImportEntry<MethodCollection>> typedStaticMethods,

        ImmutableMap<String, ImportEntry<FieldPointer>> staticFields,
        @Nullable BiConsumer<KType.UnprocessedType, KType> listener
) implements ExtendableImportTable {

    public FirstPassImportTable(Context c, Model model) {
        this(
                c,
                model,
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                null
        );
    }

    public FirstPassImportTable(
            Context c, Model model, BiConsumer<KType.UnprocessedType, KType> listener) {
        this(
                c,
                model,
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                listener
        );
    }

    //<editor-fold defaultstate="collapsed" desc="Table Lookup">

    /**
     * Import a Type with default ImportGenericBehavior.
     */
    @Override
    public KType importType(Region region, KType kType) {
        return this.importType(region, kType, ImportGenericBehavior.DEFAULT);
    }

    /**
     * Main type import function.
     * Replaced all unknown imports recursively.
     */
    @Override
    public KType importType(Region region, KType kType, ImportGenericBehavior flags) {
        return switch (kType) {
            case KType.ArrayType arrayType ->
                    new KType.ArrayType(importType(region, arrayType.elementType()));
            case KType.FunctionType functionType -> importFunctionType(region, functionType);
            case KType.GenericLink genericLink -> genericLink;
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> resolvable;
            case KType.ClassType classType ->
                    importUnprocessedType(
                            region,
                            classType.pointer().path(),
                            classType.generics(),
                            flags
                    );
            case KType.UnprocessedType unprocessedType -> {

                var result = importUnprocessedType(
                        unprocessedType.name().region(),
                        unprocessedType.name().value(),
                        unprocessedType.generics(),
                        flags
                );
                if (this.listener != null) {
                    this.listener.accept(unprocessedType, result);
                }
                yield result;
            }
            case KType.VoidType _ -> KType.NONE;
        };
    }


    /**
     * Type lookup bases on a path.
     * @param flags behavior for generics, see {@link ImportGenericBehavior}
     * @return the ClassType or a Generic (path is one element and generics is empty)
     */
    KType importUnprocessedType(
            Region region, ObjectPath path, List<KType> generics, ImportGenericBehavior flags) {

        if (path.isEmpty()) {
            Log.temp(this, region, "Empty path, this should not happen");
            throw new Log.KarinaException();
        }

        //first check if the first element is imported, or a generic
        //<editor-fold defaultstate="collapsed" desc="Check if the path is a generic">
        var head = path.first();
        if (path.tail().isEmpty()) {
            //first check if it is a generic
            var generic = getGeneric(head);
            if (generic != null) {
                if (!generics.isEmpty()) {
                    Log.error(
                            this,
                            new ImportError.GenericCountMismatch(region, head, 0, generics.size())
                    );
                    throw new Log.KarinaException();
                }
                return new KType.GenericLink(generic);
            }
        }
        //</editor-fold>

        //Otherwise it must be a class
        var classPointer = getClassPointer(region, path);

        //Check the generics, based on the behavior of the 'flags'
        //<editor-fold defaultstate="collapsed" desc="Generic checks">
        var classModel = this.model.getClass(classPointer);

        var expectedSize = classModel.generics().size();
        var newGenerics = generics.stream().map(ref -> this.importType(region, ref)).toList();

        boolean checkGenericLength = flags == ImportGenericBehavior.INSTANCE_CHECK ||
                flags == ImportGenericBehavior.DEFAULT;
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
                            Log.temp(this, region, "Cannot use generics in instance check");
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
            Log.error(
                    this,
                    new ImportError.GenericCountMismatch(
                            region, classModel.name(), expectedSize, newGenerics.size())
            );
            throw new Log.KarinaException();
        }
        //</editor-fold>

        return classPointer.implement(newGenerics);

    }

    @Override
    public ClassPointer getClassPointer(Region region, ObjectPath path) {

        var classPointer = getClassPointerNullable(region, path);
        if (classPointer == null) {
            //error reporting. Collect all names and give them as suggestions
            logUnknownPointerError(region, path);
            throw new Log.KarinaException();
        }

        return classPointer;
    }

    @Override
    public @Nullable ClassPointer getClassPointerNullable(Region region, ObjectPath path) {

        var head = path.first();
        boolean hasPathDefined = !path.tail().isEmpty();
        ClassPointer classPointer;
        if (!hasPathDefined) {
            // find a class by its name in the import table, when when no subPath is defined
            classPointer = this.getClass(head);
        } else {
            // find the class by the name of the first element and find child classes by the rest of the path
            classPointer = this.getClassNested(path, region);
        }

        // when all fails, check for a qualified path.
        if (classPointer == null) {
            classPointer = this.model.getClassPointer(region, path);
        }

        return classPointer;
    }

    private @Nullable Generic getGeneric(String name) {
        var entry = this.generics.get(name);
        if (entry == null) {
            return null;
        }
        return entry.reference();
    }

    /**
     * find the class by the name of the first element and find child classes by the rest of the path
     */
    private @Nullable ClassPointer getClassNested(ObjectPath path, Region region) {
        var userClassPointer = ImportHelper.getUserClassPointer(this.model, region, path);
        if (userClassPointer != null) {
            return userClassPointer;
        }


        var first = path.first();
        var tail = path.tail();

        if (!this.classes.containsKey(first)) {
            return null;
        }
        var start = Objects.requireNonNull(this.classes.get(first)).reference();

        var referedClassModel = this.model.getClass(start);
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

    /**
     * find the class by its name in the import table
     */
    private @Nullable ClassPointer getClass(String name) {
        if (this.classes.containsKey(name)) {
            return Objects.requireNonNull(this.classes.get(name)).reference();
        }
        return null;
    }

    private KType.FunctionType importFunctionType(Region region, KType.FunctionType functionType) {

        // Import types
        var returnType = importType(region, functionType.returnType());
        var arguments =
                functionType.arguments().stream().map(ref -> importType(region, ref)).toList();
        var interfaces = new ArrayList<>(functionType.interfaces().stream().map(ref -> {
            var imported = importType(region, ref);
            if (!(imported instanceof KType.ClassType classType)) {
                Log.error(this, new AttribError.NotAClass(region, imported));
                throw new Log.KarinaException();
            }
            return classType;
        }).toList());

        Log.recordType(Log.LogTypes.CLOSURE, "Function type with interfaces", interfaces);

        return new KType.FunctionType(arguments, returnType, interfaces);
    }


    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Table Insertion">
    @Override
    public FirstPassImportTable addClass(
            Region declarationRegion, String name, ClassPointer pointer, boolean declaredExplicit,
            boolean prelude
    ) {
        var newClasses = new HashMap<>(this.classes);
        var declare = testDuplicate(newClasses, declarationRegion, name, declaredExplicit);
        if (declare) {
            newClasses.put(
                    name, new ImportEntry<>(declarationRegion, pointer, declaredExplicit, prelude));
        }
        return new FirstPassImportTable(
                this.c,
                this.model,
                ImmutableMap.copyOf(newClasses),
                this.generics,
                this.untypedStaticMethods,
                this.typedStaticMethods,
                this.staticFields,
                this.listener
        );
    }

    /*
     * Add a generic to the table.
     * they always overwrite existing generics
     */
    @Override
    public FirstPassImportTable addGeneric(Region declarationRegion, Generic generic) {
        var newGenerics = new HashMap<>(this.generics);
        newGenerics.put(generic.name(), new ImportEntry<>(declarationRegion, generic, true, false));
        return new FirstPassImportTable(
                this.c,
                this.model,
                this.classes,
                ImmutableMap.copyOf(newGenerics),
                this.untypedStaticMethods,
                this.typedStaticMethods,
                this.staticFields,
                this.listener
        );
    }

    @Override
    public StaticImportTable intoStaticTable(ClassPointer referenceSite, Model model) {
        var classes = ImmutableMap.<String, ClassPointer>builder();

        this.classes().forEach((name, ptr) -> {
            classes.put(name, ptr.reference());
        });

        var staticFields = ImmutableMap.<String, FieldPointer>builder();
        this.staticFields().forEach((name, ptr) -> {
            staticFields.put(name, ptr.reference());
        });


        var staticMethods = ImmutableMap.<String, MethodCollection>builder();
        this.typedStaticMethods().forEach((name, ptr) -> {
            staticMethods.put(name, ptr.reference());
        });
        this.untypedStaticMethods().forEach((name, ptr) -> {
            staticMethods.put(name, ptr.reference().toTypedStaticCollection(referenceSite, model));
        });

        return new StaticImportTable(classes.build(), staticMethods.build(), staticFields.build());
    }

    /**
     * TODO Replace with {@link MethodCollection}
     */
    @Override
    public FirstPassImportTable addStaticMethod(
            Region declarationRegion, String name, ClassPointer classPointer,
            boolean declaredExplicit, boolean prelude
    ) {
        var newStaticMethods = new HashMap<>(this.untypedStaticMethods);
        var declare = testDuplicate(newStaticMethods, declarationRegion, name, declaredExplicit);
        if (declare) {
            var collection = new UntypedMethodCollection(name, classPointer);
            newStaticMethods.put(
                    name,
                    new ImportEntry<>(declarationRegion, collection, declaredExplicit, prelude)
            );
        }
        var newTypedStaticMethods = new HashMap<>(this.typedStaticMethods);
        //remove any duplicates
        newTypedStaticMethods.remove(name);

        return new FirstPassImportTable(
                this.c,
                this.model,
                this.classes,
                this.generics,
                ImmutableMap.copyOf(newStaticMethods),
                ImmutableMap.copyOf(newTypedStaticMethods),
                this.staticFields,
                this.listener
        );
    }

    @Override
    public FirstPassImportTable addPreludeMethods(
            Region declarationRegion, String name,
            List<MethodPointer> methods
    ) {
        if (this.untypedStaticMethods.containsKey(name)) {
            return this;
        }
        var newTypedStaticMethods = new HashMap<>(this.typedStaticMethods);
        newTypedStaticMethods.remove(name); // remove any duplicates
        var typedCollection = new MethodCollection(name, methods);
        newTypedStaticMethods.put(
                name, new ImportEntry<>(declarationRegion, typedCollection, false, true));

        return new FirstPassImportTable(
                this.c,
                this.model,
                this.classes,
                this.generics,
                this.untypedStaticMethods,
                ImmutableMap.copyOf(newTypedStaticMethods),
                this.staticFields,
                this.listener
        );
    }

    @Override
    public boolean containsClass(String name) {
        return this.classes.containsKey(name);
    }

    @Override
    public FirstPassImportTable addStaticField(
            Region declarationRegion, String name,
            FieldPointer reference, boolean declaredExplicit, boolean prelude
    ) {
        var newStaticFields = new HashMap<>(this.staticFields);
        var declare = testDuplicate(newStaticFields, declarationRegion, name, declaredExplicit);
        if (declare) {
            newStaticFields.put(
                    name,
                    new ImportEntry<>(declarationRegion, reference, declaredExplicit, prelude)
            );
        }
        return new FirstPassImportTable(
                this.c,
                this.model,
                this.classes,
                this.generics,
                this.untypedStaticMethods,
                this.typedStaticMethods,
                ImmutableMap.copyOf(newStaticFields),
                this.listener
        );
    }


    /**
     * @return true if the entry should be added to the table
     */
    private <T> boolean testDuplicate(
            Map<String, ImportEntry<T>> objects, Region declarationRegion,
            String name, boolean declaredExplicit
    ) {
        if (!objects.containsKey(name)) {
            return true;
        }
        var entry = objects.get(name);
        if (entry.wasDeclaredExplicit() && declaredExplicit) {
            Log.error(
                    this,
                    new ImportError.DuplicateItem(entry.definedRegion(), declarationRegion, name)
            );
            throw new Log.KarinaException();
        }
        return declaredExplicit || !entry.wasDeclaredExplicit();
    }

    /**
     * Remove all generics from the import table.
     * Used in static methods, where generics are from the outer environment allowed.
     */
    @Override
    public FirstPassImportTable removeGenerics() {
        return new FirstPassImportTable(
                this.c,
                this.model,
                this.classes,
                ImmutableMap.of(),
                this.untypedStaticMethods,
                this.typedStaticMethods,
                this.staticFields,
                this.listener
        );
    }
    //</editor-fold>

    //<editor-fold desc="Debugging and Error Logging" collapsed="true">

    /**
     * When error occurs, log the error with the relevant information.
     * Dont forget to throw the exception after this call
     */
    @Override
    public void logUnknownPointerError(Region region, ObjectPath path) {
        var available = availableTypeNames();
        var availablePaths = availableTypePaths();
        Log.error(
                this.c, new ImportError.UnknownImportType(
                        region, path.mkString("::"), available,
                        availablePaths
                )
        );
    }

    private Set<String> availableTypeNames() {
        var keys = new HashSet<>(this.classes.keySet());
        keys.addAll(this.generics.keySet());
        return keys;
    }

    private Set<ObjectPath> availableTypePaths() {
        var keys = new HashSet<ObjectPath>();
        for (var objectPath : this.model.getBinaryClasses()) {
            keys.add(objectPath.path());
        }
        for (var objectPath : this.model.getUserClasses()) {
            keys.add(objectPath.path());
        }
        return keys;
    }


    @Override
    public Context intoContext() {
        return this.c;
    }

    @Override
    public FirstPassImportTable withNewContext(Context c) {
        return new FirstPassImportTable(
                c,
                this.model,
                this.classes,
                this.generics,
                this.untypedStaticMethods,
                this.typedStaticMethods,
                this.staticFields,
                this.listener
        );
    }
    /**
     * A given entry in the import table.
     *
     * @param definedRegion       where the entry was defined. Used when a collision happens.
     * @param reference           the object to import, see the argument of the {@link FirstPassImportTable} constructor.
     * @param wasDeclaredExplicit when true, this entry was declared explicitly, so it cannot be overridden.
     *                            when another entry is declared with the same name, it will be an error.
     *                            Otherwise the import was implicit (like 'import java::util::Arrays *'
     *                            and can be overridden.
     * @param prelude             when true, this entry can always be overridden by other entries
     * @param <T>                 type of import
     */
    public record ImportEntry<T>(
            Region definedRegion, T reference, boolean wasDeclaredExplicit, boolean prelude
    ) {
    }


}
