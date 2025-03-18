package karina.lang;

import java.util.function.Supplier;

public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    final class Ok<T,E> implements Result<T, E> {
        public final T value;

        public Ok(T value) {
            this.value = value;
        }
    }

    final class Err<T, E> implements Result<T, E> {
        public final E error;

        public Err(E error) {
            this.error = error;
        }
    }

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }


    static <E> Result<E, Throwable> tryFunction(Supplier<E> supplier) {
        try {
            return ok(supplier.get());
        } catch (Throwable e) {
            return err(e);
        }
    }
}
