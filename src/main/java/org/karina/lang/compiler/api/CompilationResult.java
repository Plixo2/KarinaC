package org.karina.lang.compiler.api;

public sealed interface CompilationResult<T> {
    record OK<T>(T result) implements CompilationResult<T>{}
    record Error<T>() implements CompilationResult<T>{}

    default boolean isOk() {
        return this instanceof OK;
    }
    default boolean isError() {
        return this instanceof Error;
    }

    default OK<T> asOk() {
        return (OK<T>) this;
    }

    static <T> CompilationResult<T> ok(T result) {
        return new OK<>(result);
    }
    static <T> CompilationResult<T> error() {
        return new Error<>();
    }
}
