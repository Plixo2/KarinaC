package org.karina.lang.compiler.utils;

public record Pair<U, T>(U first, T second) {
    public static <U, T> Pair<U, T> of(U first, T second) {
        return new Pair<>(first, second);
    }
}
