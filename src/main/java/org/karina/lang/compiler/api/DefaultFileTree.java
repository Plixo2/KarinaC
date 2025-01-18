package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

public record DefaultFileTree(
        ObjectPath path,
        String name,
        List<DefaultFileTree> children,
        List<FileNode<TextSource>> leafs
) implements FileTreeNode<TextSource> {

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int indent) {

        var sb = new StringBuilder();
        sb.append("  ".repeat(indent));
        sb.append(this.path);
        sb.append("\n");
        for (var child : this.children) {
            sb.append(child.toString(indent + 1));
        }
        for (var leaf : this.leafs) {
            sb.append("  ".repeat(indent + 1));
            sb.append(leaf.path());
            sb.append("\n");
        }
        return sb.toString();

    }

    public record DefaultFileNode(ObjectPath path, String name, TextSource content) implements FileNode<TextSource> {}
}
