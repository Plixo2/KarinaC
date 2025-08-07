package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FileTreeNode {
    @Nullable ObjectPath path(); // Not used in the compiler
    String name();  // Not used in the compiler
    List<? extends FileTreeNode> children();
    List<? extends FileNode> leafs();
    default int leafCount() {
        var count = this.leafs().size();
        for (var child : this.children()) {
            count += child.leafCount();
        }
        return count;
    }
}
