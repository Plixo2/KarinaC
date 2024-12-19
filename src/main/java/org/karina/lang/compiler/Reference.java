package org.karina.lang.compiler;

public class Reference<T> {
    public T value;

    public Reference(T value) {
        this.value = value;
    }

    public Reference() {
        this.value = null;
    }

    public void set(T value) {
        this.value = value;
    }

    public static <T> Reference<T> of(T value) {
        return new Reference<>(value);
    }

    public static <T> Reference<T> of() {
        return new Reference<>();
    }
}
