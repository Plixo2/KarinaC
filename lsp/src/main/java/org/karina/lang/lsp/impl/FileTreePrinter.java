package org.karina.lang.lsp.impl;

import org.karina.lang.lsp.lib.VirtualFileTreeNode;

import java.util.List;

public class FileTreePrinter {

    public static String prettyTree(VirtualFileTreeNode node) {
        var bob = new StringBuilder();
        prettyTree(node, "", true, bob);
        return bob.toString();
    }

    private static void prettyTree(VirtualFileTreeNode node, String prefix, boolean isLast, StringBuilder builder) {
        String displayName = node.name().isEmpty() ? "root" : node.name();
        builder.append(prefix);
        if (!prefix.isEmpty()) {
            builder.append(isLast ? "\\-> " : "|-- ");
        }
        builder.append(displayName).append("/\n");

        List<VirtualFileTreeNode> childDirs = node.children();
        List<VirtualFileTreeNode.VirtualFileNode> leafFiles = node.leafs();

        int total = childDirs.size() + leafFiles.size();

        for (int i = 0; i < childDirs.size(); i++) {
            VirtualFileTreeNode child = childDirs.get(i);
            boolean last = (i == total - 1 && leafFiles.isEmpty());
            prettyTree(child, prefix + (prefix.isEmpty() ? "" : (isLast ? "    " : "│   ")), last, builder);
        }

        for (int i = 0; i < leafFiles.size(); i++) {
            VirtualFileTreeNode.VirtualFileNode leaf = leafFiles.get(i);
            boolean last = (i + childDirs.size() == total - 1);
            printLeaf(leaf, prefix + (prefix.isEmpty() ? "" : (isLast ? "    " : "│   ")), last, builder);
        }
    }

    private static void printLeaf(VirtualFileTreeNode.VirtualFileNode leaf, String prefix, boolean isLast, StringBuilder builder) {
        builder.append(prefix);
        builder.append(isLast ? "└── " : "├── ");
        builder.append(leaf.name()).append('\n');
    }
}
