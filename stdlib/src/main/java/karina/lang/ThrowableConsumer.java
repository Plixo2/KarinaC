package karina.lang;

@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {
    void accept(T t) throws E;
}
