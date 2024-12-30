package org.karina.lang.compiler.errors;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Helper to determine if a collection contains duplicate objects.
 */
public final class Unique {

    private Unique() {}

    /**
     * @return the first duplicate object in the collection, or null if the collection contains no duplicates.
     */
    public static <T> @Nullable Duplicate<T, T> testUnique(Collection<T> collection) {
        return Unique.testUnique(collection, Function.identity());
    }

    /**
     * @param valueObject Function for determining the unique value of an object.
     * @return The first duplicate object in the collection, or null if the collection contains no duplicates.
     */
    public static <T, V> @Nullable Duplicate<T, V> testUnique(Collection<T> collection, Function<T, V> valueObject) {

        var objects = new HashMap<V, T>();
        for (var object : collection) {
            var value = valueObject.apply(object);
            if (objects.containsKey(value)) {
                return new Duplicate<>(objects.get(value), object, value);
            }
            objects.put(value, object);
        }
        return null;

    }

    public record Duplicate<T, V>(T first, T duplicate, V value) {}
}
