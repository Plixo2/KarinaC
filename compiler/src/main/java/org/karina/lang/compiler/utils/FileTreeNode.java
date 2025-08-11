package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    static List<FileNode> flatten(FileTreeNode fileTree) {
        var files = new ArrayList<FileNode>(fileTree.leafs());
        for (var child : fileTree.children()) {
            files.addAll(flatten(child));
        }
        return files;
    }

    static DefaultFileTree copyTree(FileTreeNode tree) {
        return new DefaultFileTree(
                tree.path(),
                tree.name(),
                tree.children().stream()
                    .map(FileTreeNode::copyTree)
                    .toList(),
                tree.leafs().stream()
                    .map(FileTreeNode::copyFile)
                    .toList()
        );
    }
    private static FileNode copyFile(FileNode file) {
        return new DefaultFileTree.DefaultFileNode(
                file.path(),
                file.name(),
                new DefaultTextSource(file.content().resource(), file.content().content())
        );
    }





}
