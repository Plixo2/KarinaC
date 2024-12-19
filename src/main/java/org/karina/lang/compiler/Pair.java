package org.karina.lang.compiler;

public record Pair<T, U>(U first, T second) {
    public static <T, U> Pair<T, U> of(U first, T second) {
        return new Pair<>(first, second);
    }
}
