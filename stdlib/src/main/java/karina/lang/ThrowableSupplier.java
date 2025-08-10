package karina.lang;

@FunctionalInterface
public interface ThrowableSupplier<R, E extends Throwable> {
    R get() throws E;
}
