package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;

import java.util.*;


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
            var methodPointers = Objects.requireNonNull(this.staticMethods.get(name));
            if (!methodPointers.isEmpty()) {
                return methodPointers;
            }
        }
        return null;
    }

    public List<MethodModel> getAllExtensionMethods(Model model) {
        var methods = new ArrayList<MethodModel>();
        for (var collection : this.staticMethods.values()) {
            for (var methodPointer : collection) {
                var method = model.getMethod(methodPointer);
                var extension = method.modifiers();
                if (MethodModel.isExtension(extension)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    public @Nullable FieldPointer getStaticField(String name) {
        if (this.staticFields.containsKey(name)) {
            return Objects.requireNonNull(this.staticFields.get(name));
        } else {
            return null;
        }
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
