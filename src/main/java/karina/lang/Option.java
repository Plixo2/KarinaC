package karina.lang;

import karina.lang.internal.functions.Function0_1;
import karina.lang.internal.functions.Function1_1;

import java.util.Optional;
import java.util.function.Supplier;

//Build-in Option type, needed for ? unwrapping and ? type annotations
public sealed interface Option<T> permits Option.Some, Option.None {

    final class Some<T> implements Option<T> {
        public final T value;

        public Some(T value) {
            this.value = value;
        }
    }

    final class None<T> implements Option<T> {
    }


    default boolean isSome() {
        return this instanceof Option.Some;
    }

    default boolean isNone() {
        return this instanceof Option.None;
    }

    default <E> Result<T, E> okOr(E error) {
        return switch (this) {
            case Option.Some<T> v -> Result.ok(v.value);
            case Option.None<T> n -> Result.err(error);
        };
    }

    default <E> Result<T, E> okOrGet(Function0_1<E> error) {
        return switch (this) {
            case Option.Some<T> v -> Result.ok(v.value);
            case Option.None<T> n -> Result.err(error.get());
        };
    }

    default T orElse(T other) {
        return switch (this) {
            case Option.Some<T> v -> v.value;
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
            case Option.Some<T> v -> v.value;
            case Option.None<T> n -> supplier.get();
        };
    }

    default <V> Option<V> map(Function1_1<T, V> function) {
        return switch (this) {
            case Option.Some<T> v -> Option.some(function.apply(v.value));
            case Option.None<T> n -> Option.none();
        };
    }

    default <V> V mapOrElse(Function1_1<T, V> mapFunction, V defaultValue) {
        return switch (this) {
            case Option.Some<T> v -> mapFunction.apply(v.value);
            case Option.None<T> n -> defaultValue;
        };
    }

    default <V> Option<V> flatMap(Function1_1<T, Option<V>> function) {
        return switch (this) {
            case Option.Some<T> v -> function.apply(v.value);
            case Option.None<T> n -> Option.none();
        };
    }

    default <V> V flatMapOrElse(Function1_1<T, V> function, V defaultValue) {
        return switch (this) {
            case Option.Some<T> v -> function.apply(v.value);
            case Option.None<T> n -> defaultValue;
        };
    }


    default T nullable() {
        return switch (this) {
            case Option.Some<T> v -> v.value;
            case Option.None<T> n -> null;
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

    static <T> Option<T>[] newArray(Class<T> ignoredCls, int size) {
        return Values.newArray(size, _ -> none());
    }

    static <T> Option<T>[] newArray(int size) {
        return Values.newArray(size, _ -> none());
    }

}
