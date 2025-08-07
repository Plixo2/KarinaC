package karina.lang;


import java.util.function.Function;
import java.util.function.Supplier;

//Build-in Result type, needed for ? unwrapping
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    record Ok<T,E>(T value) implements Result<T, E> {
        @Override
        public String toString() {
            return "Return::Ok{" + "value=" + this.value + '}';
        }
    }

    record Err<T, E>(E error) implements Result<T, E> {
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

    default <R> Result<R, E> map(Function<T, R> mapper) {
        return switch (this) {
            case Result.Ok<T, E> v -> ok(mapper.apply(v.value));
            case Result.Err<T, E> v -> err(v.error);
        };
    }

    default <R> Result<R, E> flatMap(Function<T, Result<R, E>> mapper) {
        return switch (this) {
            case Result.Ok<T, E> v -> mapper.apply(v.value);
            case Result.Err<T, E> v -> err(v.error);
        };
    }

    default <F> Result<T, F> mapErr(Function<E, F> mapper) {
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

    static <T> Result<T, Throwable> safeCall(Supplier<T> supplier) {
        try {
            return ok(supplier.get());
        } catch (Throwable e) {
            return err(e);
        }
    }

    /**
     * Matches against any class given
     *
     */
    @Deprecated
    private static <T, E> Result<T, E> safeCallExpect(Supplier<T> supplier, Class<E>[] errors) {
        try {
            return ok(supplier.get());
        } catch (Throwable e) {
            for (var errorClass : errors) {
                if (errorClass.isInstance(e)) {
                    return err(errorClass.cast(e));
                }
            }

            throw new RuntimeException(
                    "Unexpected Error",
                    e
            );
        }
    }

}
