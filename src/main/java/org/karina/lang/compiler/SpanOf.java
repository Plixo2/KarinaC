package org.karina.lang.compiler;

import java.util.Objects;

/**
 * Pair of a {@link Span} and a value.
 */
public record SpanOf<T>(Span region, T value) {

    public static <T> SpanOf<T> span(Span span, T value) {
        return new SpanOf<>(span, value);
    }

    @Override
    public boolean equals(Object object) {
       return object instanceof SpanOf<?>(var ignored, var otherValue)
               && Objects.equals(this.value, otherValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
}
