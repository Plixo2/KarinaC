package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.stages.imports.table.UserImportTable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Separate from ImportTable, as we dont need the generics,
 * And also have create MethodCollections after importing, since we cannot store signatures in methodPointers,
 * since the types in them are not known in the import stage.
 * @param staticMethods
 * @param staticFields
 */
public record StaticImportTable(
        ImmutableMap<String, ClassPointer> classes,
        ImmutableMap<String, MethodCollection> staticMethods,
        ImmutableMap<String, FieldPointer> staticFields
) {
    public @Nullable ClassPointer getClass(String name) {
        if (this.classes.containsKey(name)) {
            return Objects.requireNonNull(this.classes.get(name));
        }
        return null;
    }

    public @Nullable MethodCollection getStaticMethod(String name) {
        if (this.staticMethods.containsKey(name)) {
            return Objects.requireNonNull(this.staticMethods.get(name));
        }
        return null;
    }

    public @Nullable FieldPointer getStaticField(String name) {
        if (this.staticFields.containsKey(name)) {
            return Objects.requireNonNull(this.staticFields.get(name));
        }
        return null;
    }

    public static StaticImportTable fromImportTable(ClassPointer referenceSite, Model model, UserImportTable importTable) {
        var classes = ImmutableMap.<String, ClassPointer>builder();

        importTable.classes().forEach((name, ptr) -> {
            classes.put(name, ptr.reference());
        });

        var staticFields = ImmutableMap.<String, FieldPointer>builder();
        importTable.staticFields().forEach((name, ptr) -> {
            staticFields.put(name, ptr.reference());
        });


        var staticMethods = ImmutableMap.<String, MethodCollection>builder();
        importTable.typedStaticMethods().forEach((name, ptr) -> {
            staticMethods.put(name, ptr.reference());
        });
        importTable.untypedStaticMethods().forEach((name, ptr) -> {
            staticMethods.put(name, ptr.reference().toTypedStaticCollection(referenceSite, model));
        });

        return new StaticImportTable(classes.build(), staticMethods.build(), staticFields.build());
    }

    public Set<String> availableItemNames() {
        var keys = new HashSet<>(this.classes.keySet());
        keys.addAll(this.staticFields.keySet());
        keys.addAll(this.staticMethods.keySet());
        return keys;
    }

    public final static StaticImportTable EMPTY = new StaticImportTable(
            ImmutableMap.of(),
            ImmutableMap.of(),
            ImmutableMap.of()
    );

}
