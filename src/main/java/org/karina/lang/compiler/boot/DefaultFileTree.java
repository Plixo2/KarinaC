package org.karina.lang.compiler.boot;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.api.FileNode;
import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.TextSource;

import java.util.List;

public record DefaultFileTree(
        ObjectPath path,
        String name,
        List<DefaultFileTree> children,
        List<FileNode> leafs
) implements FileTreeNode {

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

    public record DefaultFileNode(ObjectPath path, String name, TextSource text) implements FileNode {}
}
