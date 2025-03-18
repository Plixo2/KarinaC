package karina.lang;

public sealed interface Option<T> permits Option.Some, Option.None {

    final class Some<T> implements Option<T> {
        public final T value;

        public Some(T value) {
            this.value = value;
        }
    }

    final class None<T> implements Option<T> {
        public None() {
        }
    }

    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    static <T> Option<T> none() {
        return new None<>();
    }

}
