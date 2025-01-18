package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

public interface FileTreeNode<T> {
    ObjectPath path();
    String name();
    List<? extends FileTreeNode<T>> children();
    List<? extends FileNode<T>> leafs();
}
