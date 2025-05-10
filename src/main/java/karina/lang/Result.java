package karina.lang;

import karina.lang.internal.functions.Function0_1;
import karina.lang.internal.functions.Function1_1;
import lombok.Getter;
import lombok.experimental.Accessors;

//Build-in Result type, needed for ? unwrapping
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    final class Ok<T,E> implements Result<T, E> {
        @Getter
        @Accessors(fluent = true)
        private final T value;

        public Ok(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Return::Ok{" + "value=" + this.value + '}';
        }
    }

    final class Err<T, E> implements Result<T, E> {
        @Getter
        @Accessors(fluent = true)
        private final E error;

        public Err(E error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "Result::Err{" + "error=" + this.error + '}';
        }
    }

    default T expect(Option<String> message) {
        return expect(message.orElse(""));
    }

    default T expect(String message) {
        return switch (this) {
            case Result.Ok<T, E> v -> v.value;
            case Result.Err<T, E> v -> {
                var includeMessage = message != null && !message.isEmpty();
                String suffix;
                if (includeMessage) {
                    suffix = ": " + message;
                } else {
                    suffix = "";
                }
                var asCause = Option.instanceOf(Throwable.class, v.error).nullable();
                throw new RuntimeException(
                        "Could not unwrap Result" + suffix,
                        asCause
                );
            }
        };
    }

    default <R> Result<R, E> map(Function1_1<T, R> mapper) {
        return switch (this) {
            case Result.Ok<T, E> v -> ok(mapper.apply(v.value));
            case Result.Err<T, E> v -> err(v.error);
        };
    }

    default <R> Result<R, E> flatMap(Function1_1<T, Result<R, E>> mapper) {
        return switch (this) {
            case Result.Ok<T, E> v -> mapper.apply(v.value);
            case Result.Err<T, E> v -> err(v.error);
        };
    }

    default <F> Result<T, F> mapErr(Function1_1<E, F> mapper) {
        return switch (this) {
            case Result.Ok<T, E> v -> ok(v.value);
            case Result.Err<T, E> v -> err(mapper.apply(v.error));
        };
    }

    default Result<E, T> inverse() {
        return switch (this) {
            case Result.Ok<T, E> v -> err(v.value);
            case Result.Err<T, E> v -> ok(v.error);
        };
    }

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }

    static <T> Result<T, Throwable> safeCall(Function0_1<T> supplier) {
        try {
            return ok(supplier.get());
        } catch (Throwable e) {
            return err(e);
        }
    }



}
