package karina.lang;

import karina.lang.internal.functions.Function1_1;

public final class Values {

    public static <T> T Null() {
        return null;
    }

    public static <T> T Null(Class<T> ignoredType) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(int size, Function1_1<Integer, T> initializer) {
        var array = (T[]) new Object[size];
        for (var i = 0; i < size; i++) {
            array[i] = initializer.apply(i);
        }
        return array;
    }

    public static <T> T[] newArray(Class<T> ignoredCls, int size, Function1_1<Integer, T> initializer) {
        return newArray(size, initializer);
    }



}
