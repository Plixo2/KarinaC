package org.karina.lang.lsp.lib;

import org.karina.lang.lsp.KarinaLanguageServer;

import java.util.function.Function;

sealed public interface IOResult<T> {


    /// Sends a message with the error and returns null.
    default T orMessageAndNull(KarinaLanguageServer kls) {
        return switch (this) {
            case Success<T> v -> v.value();
            case Error<T> v -> {
                kls.errorMessage(v.exception());
                yield null;
            }
        };
    }

    default <R> IOResult<R> map(Function<T, R> mapper) {
        return switch (this) {
            case Success<T> v -> new Success<>(mapper.apply(v.value()));
            case Error<T> v -> new Error<>(v.exception());
        };
    }

    default <R> IOResult<R> flatMap(Function<T, IOResult<R>> mapper) {
        return switch (this) {
            case Success<T> v -> mapper.apply(v.value());
            case Error<T> v -> new Error<>(v.exception());
        };
    }

    record Success<T>(T value) implements IOResult<T> {
    }

    record Error<T>(Exception exception) implements IOResult<T> {
    }
}
