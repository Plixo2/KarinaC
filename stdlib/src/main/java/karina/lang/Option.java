package karina.lang;



import java.lang.reflect.Array;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//Build-in Option type, needed for '?' unwrapping and '?' type annotations
public sealed interface Option<T> permits Option.Some, Option.None {

    record Some<T>(T value) implements Option<T> {

        public Some(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Option::Some{" + "value=" + this.value + '}';
        }
    }

    record None<T>() implements Option<T> {

        @Override
        public String toString() {
            return "Option::None{}";
        }
    }


    default boolean isSome() {
        return this instanceof Option.Some;
    }

    default boolean isNone() {
        return this instanceof Option.None;
    }

    default <E> Result<T, E> okOr(E error) {
        return switch (this) {
            case Option.Some<T>(var v) -> Result.ok(v);
            case Option.None<T> n -> Result.err(error);
        };
    }

    default <E> Result<T, E> okOrGet(Supplier<E> error) {
        return switch (this) {
            case Option.Some<T>(var v) -> Result.ok(v);
            case Option.None<T> n -> Result.err(error.get());
        };
    }

    default T orElse(T other) {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> other;
        };
    }

    default Option<T> or(Option<T> other) {
        return switch (this) {
            case Option.Some<T> v -> v;
            case Option.None<T> n -> other;
        };
    }

    default T orElseGet(Supplier<T> supplier) {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> supplier.get();
        };
    }

    default <V> Option<V> map(Function<T, V> function) {
        return switch (this) {
            case Option.Some<T>(var v) -> Option.some(function.apply(v));
            case Option.None<T> n -> Option.none();
        };
    }

    default <V> V mapOrElse(Function<T, V> mapFunction, V defaultValue) {
        return switch (this) {
            case Option.Some<T>(var v) -> mapFunction.apply(v);
            case Option.None<T> n -> defaultValue;
        };
    }

    default <V> Option<V> flatMap(Function<T, Option<V>> function) {
        return switch (this) {
            case Option.Some<T>(var v) -> function.apply(v);
            case Option.None<T> n -> Option.none();
        };
    }

    default <V> V flatMapOrElse(Function<T, V> function, V defaultValue) {
        return switch (this) {
            case Option.Some<T>(var v) -> function.apply(v);
            case Option.None<T> n -> defaultValue;
        };
    }

    default void ifSome(Consumer<T> consumer) {
        if (this instanceof Some<T>(T value)) {
            consumer.accept(value);
        }
    }

    default void ifNone(Runnable runnable) {
        if (this instanceof None<T>) {
            runnable.run();
        }
    }


    default T nullable() {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> null;
        };
    }


    /// @throws UnwrapException if the Option is None
    default T expect(Throwable throwable) throws UnwrapException {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> {
                throw new UnwrapException(
                        "Could not unwrap Option",
                        throwable
                );
            }
        };
    }

    /// @throws UnwrapException if the Option is None
    default T expect(String message) throws UnwrapException {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> {
                String suffix;
                if (!message.isEmpty()) {
                    suffix = ": " + message;
                } else {
                    suffix = "";
                }
                throw new UnwrapException(
                        "Could not unwrap Option" + suffix
                );
            }
        };
    }

    /// @throws UnwrapException if the Option is None
    default T expect(String message, Throwable throwable) throws UnwrapException {
        return switch (this) {
            case Option.Some<T>(var v) -> v;
            case Option.None<T> n -> {
                String suffix;
                if (!message.isEmpty()) {
                    suffix = ": " + message;
                } else {
                    suffix = "";
                }
                throw new UnwrapException(
                        "Could not unwrap Option" + suffix,
                        throwable
                );
            }
        };
    }

    static <T> Option<T> instanceOf(Class<T> clazz, Object value) {
        return clazz.isInstance(value) ? some(clazz.cast(value)) : none();
    }

    static <T> Option<T> fromOptional(Optional<T> optional) {
        return optional.map(Option::some).orElseGet(Option::none);
    }

    static <T> Option<T> fromNullable(T value) {
        return value == null ? none() : some(value);
    }

    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    static <T> Option<T> none() {
        return new None<>();
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T[]> unwrapArray(Class<T> cls, Option<T>[] array) {
        T[] newArray = (T[]) Array.newInstance(cls, array.length);
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof Some(var v)) {
                newArray[i] = v;
            } else {
                return Option.none();
            }
        }
        return Option.some(newArray);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T>[] newArray(Class<T> ignoredCls, int size) {
        Option<T>[] array = (Option<T>[]) Array.newInstance(Option.class, size);
        for (int i = 0; i < size; i++) {
            array[i] = none();
        }
        return array;
    }

    static <T> Option<T>[] newArray(int size) {
        return newArray(null, size);
    }

}
