package org.karina.lang.lsp.impl;

import org.karina.lang.lsp.lib.VirtualFileTreeNode;


public class FileTreePrinter {

    public static String prettyTree(VirtualFileTreeNode node) {
        StringBuilder bob = new StringBuilder(50);
        print(node, bob, "", "");
        return bob.toString();
    }



    private static void print(VirtualFileTreeNode node, StringBuilder buffer, String prefix, String childrenPrefix) {

        var collapsedName = new StringBuilder(node.name().isEmpty() ? "<root>" : node.name());
        while (node.leafs().isEmpty() && node.children().size() == 1) {
            node = node.children().getFirst();
            collapsedName.append("/").append(node.name());
        }
        buffer.append(prefix);
        buffer.append(collapsedName);
        buffer.append('\n');


        boolean hasChildren = !node.children().isEmpty();
        for (var it = node.leafs().iterator(); it.hasNext();) {
            var next = it.next();
            if (it.hasNext() || hasChildren) {
                print(next, buffer, childrenPrefix + "|-> ", childrenPrefix + "|   ");
            } else {
                print(next, buffer, childrenPrefix + "\\-> ", childrenPrefix + "    ");
            }
        }


        for (var it = node.children().iterator(); it.hasNext();) {
            var next = it.next();
            if (it.hasNext()) {
                print(next, buffer, childrenPrefix + "|-> ", childrenPrefix + "|   ");
            } else {
                print(next, buffer, childrenPrefix + "\\-> ", childrenPrefix + "    ");
            }
        }
    }

    private static void print(VirtualFileTreeNode.VirtualFileNode node, StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(node.name()).append(": ").append(node.path());
        buffer.append('\n');
    }
}
