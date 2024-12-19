package org.karina.lang.compiler;

import java.util.List;

public interface FileTreeNode {
    ObjectPath path();
    String name();
    List<? extends FileTreeNode> children();
    List<? extends FileNode> leafs();



    interface FileNode {
        ObjectPath path();
        String name();
        TextSource text();
    }
}
