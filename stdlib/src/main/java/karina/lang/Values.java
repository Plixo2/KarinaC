package karina.lang;


import java.lang.reflect.Array;
import java.util.function.IntFunction;

public final class Values {

    public static <T> T Null() {
        return null;
    }

    public static <T> T Null(Class<T> ignoredType) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> aClass, int size, IntFunction<T> initializer) {
        T[] array = (T[]) Array.newInstance(aClass, size);
        for (int i = 0; i < size; i++) {
            array[i] = initializer.apply(i);
        }
        return array;
    }


}
