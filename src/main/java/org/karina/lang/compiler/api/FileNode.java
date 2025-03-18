package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

/**
 * @see org.karina.lang.compiler.api.DefaultFileTree.DefaultFileNode
 */
public interface FileNode<T> {
    ObjectPath path();
    String name();
    T content();
}
