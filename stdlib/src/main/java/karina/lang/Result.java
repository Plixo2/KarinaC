package karina.lang;



import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//Build-in Result type, needed for '?' unwrapping
public sealed interface Result<V, E> permits Result.Ok, Result.Err {

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

    default boolean isOk() {
        return this instanceof Result.Ok;
    }

    default boolean isErr() {
        return this instanceof Result.Err;
    }


    /// @throws UnwrapException if the Option is None
    default V expect(String message) throws UnwrapException {
        return switch (this) {
            case Result.Ok<V, E>(var v) -> v;
            case Result.Err<V, E>(var e) -> {
                String suffix;
                if (!message.isEmpty()) {
                    suffix = ": " + message;
                } else {
                    suffix = "";
                }
                var asCause = Option.instanceOf(Throwable.class, e).nullable();
                throw new UnwrapException(
                        "Could not unwrap Result" + suffix,
                        asCause
                );
            }
        };
    }


    default <R> Result<R, E> map(Function<V, R> mapper) {
        return switch (this) {
            case Ok<V, E>(var v) -> ok(mapper.apply(v));
            case Err<V, E>(var e) -> err(e);
        };
    }

    default <R> Result<R, E> flatMap(Function<V, Result<R, E>> mapper) {
        return switch (this) {
            case Ok<V, E>(var v) -> mapper.apply(v);
            case Err<V, E>(var e) -> err(e);
        };
    }

    default <F> Result<V, F> mapErr(Function<E, F> mapper) {
        return switch (this) {
            case Ok<V, E>(var v) -> ok(v);
            case Err<V, E>(var e) -> err(mapper.apply(e));
        };
    }

    default Result<E, V> inverse() {
        return switch (this) {
            case Ok<V, E>(var v) -> err(v);
            case Err<V, E>(var e) -> ok(e);
        };
    }

    default V okOrElse(V defaultValue) {
        return switch (this) {
            case Ok<V, E>(var v) -> v;
            case Result.Err<V, E> e -> defaultValue;
        };
    }

    default V okOrElse(Function<E, V> defaultValueSupplier) {
        return switch (this) {
            case Ok<V, E>(var v) -> v;
            case Err<V, E>(var e) -> defaultValueSupplier.apply(e);
        };
    }

    default V okOrElse(Supplier<V> defaultValueSupplier) {
        return switch (this) {
            case Ok<V, E>(var v) -> v;
            case Result.Err<V, E> e -> defaultValueSupplier.get();
        };
    }

    default Option<E> asError() {
        return switch (this) {
            case Result.Ok<V, E> v -> Option.none();
            case Err<V, E>(var e) -> Option.some(e);
        };
    }

    default Option<V> asValue() {
        return switch (this) {
            case Ok<V, E>(var v) -> Option.some(v);
            case Result.Err<V, E>e  -> Option.none();
        };
    }

    default void ifOk(Consumer<V> consumer) {
        if (this instanceof Ok<V, E>(V value)) {
            consumer.accept(value);
        }
    }
    default void ifErr(Consumer<E> consumer) {
        if (this instanceof Err<V, E>(E error)) {
            consumer.accept(error);
        }
    }

    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }

    /// @throws ClassCastException if the exception is not of the expected type
    static <T, E extends Exception> Result<T, E> safeCallExpect(Class<E> cls, ThrowableSupplier<T, E> supplier) {
        try {
            return ok(supplier.get());
        } catch (Exception e) {
            // Let a ClassCastException occur if the exception is not of the expected type
            return err(cls.cast(e));
        }
    }

    static <T> Result<T, Exception> safeCall(Supplier<T> supplier) {
        try {
            return ok(supplier.get());
        } catch (Exception e) {
            return err(e);
        }
    }


    /// @throws ClassCastException if the exception is not of the expected type
    static <R extends AutoCloseable, E extends Exception, T> Result<T, E> safeCallWithResource(
            Class<E> cls,
            ThrowableSupplier<R, E> resource,
            ThrowableFunction<R, T, E> supplier
    ) {
        try (var res = resource.get()){
            return ok(supplier.apply(res));
        } catch (Exception e) {
            // Let a ClassCastException occur if the exception is not of the expected type
            return err(cls.cast(e));
        }
    }


    /* Implement later, when generic bounds are implemented

    default Result<V, UnwrapException> expect(String message) {
        return switch (this) {
            case Result.Ok<V, E>(var v) -> ok(v);
            case Result.Err<V, E>(var e) ->
                err(new UnwrapException("Could not unwrap Result: " + message));
        };
    }

    static <T, E extends Exception> T unwrap(Result<T, E> result) throws UnwrapException {
        return switch (result) {
            case Ok<T, E>(var v) -> v;
            case Result.Err<T, E>(var e) ->
                throw new UnwrapException("Could not unwrap Result", e);
        };
    }
    */
}
