package karina.lang.collections;

import java.util.Iterator;

public interface View<T> extends Iterator<T> {

    T[] toArray(Class<T> cls);

}
