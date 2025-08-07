package org.karina.lang.compiler.utils;

import java.util.List;

public interface FileTreeNode<T> {
    ObjectPath path();
    String name();
    List<? extends FileTreeNode<T>> children();
    List<? extends FileNode<T>> leafs();
    int leafCount();
}
