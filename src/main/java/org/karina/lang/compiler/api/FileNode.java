package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

public interface FileNode<T> {
    ObjectPath path();
    String name();
    T content();
}
