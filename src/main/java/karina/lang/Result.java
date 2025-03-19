package karina.lang;

import karina.lang.functions.Function0_1;

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
