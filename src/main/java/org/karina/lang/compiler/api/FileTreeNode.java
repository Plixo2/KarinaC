package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

public interface FileTreeNode {
    ObjectPath path();
    String name();
    List<? extends FileTreeNode> children();
    List<? extends FileNode> leafs();
}
