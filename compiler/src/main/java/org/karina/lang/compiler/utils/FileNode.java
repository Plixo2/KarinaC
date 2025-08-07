package org.karina.lang.compiler.utils;

/**
 * @see DefaultFileTree.DefaultFileNode
 */
public interface FileNode<T> {
    ObjectPath path();
    String name();
    T content();
}
