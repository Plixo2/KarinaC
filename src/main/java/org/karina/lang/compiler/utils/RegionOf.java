package org.karina.lang.compiler.utils;

import java.util.Objects;

/**
 * Pair of a {@link Region} and a value.
 */
public record RegionOf<T>(Region region, T value) {

    public static <T> RegionOf<T> region(Region region, T value) {
        return new RegionOf<>(region, value);
    }

    @Override
    public boolean equals(Object object) {
       return object instanceof RegionOf<?>(var ignored, var otherValue)
               && Objects.equals(this.value, otherValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}
