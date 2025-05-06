package karina.lang;

import karina.lang.internal.functions.Function1_1;

import java.lang.reflect.Array;

public final class Values {

    public static <T> T Null() {
        return null;
    }

    public static <T> T Null(Class<T> ignoredType) {
        return null;
    }

    public static <T> T[] newArray(Class<T> aClass, int size, Function1_1<Integer, T> initializer) {
        T[] array = (T[]) Array.newInstance(aClass, size);
        for (int i = 0; i < size; i++) {
            array[i] = initializer.apply(i);
        }
        return array;
    }



}
