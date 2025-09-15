package org.karina.lang.compiler.utils;

import java.util.List;

/**
 * Directory tree structure
 * @param path full path to the directory, relative to the root (not used in the compiler)
 * @param name simple name of the directory (not used in the compiler)
 * @param children child directories
 * @param leafs files in the directory
 */
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


    /**
     * File node including the string
     * @param path full path to the file, relative to the root
     * @param name simple name of the file
     * @param content file content
     */
    public record DefaultFileNode(ObjectPath path, String name, TextSource content) implements FileNode { }
}
