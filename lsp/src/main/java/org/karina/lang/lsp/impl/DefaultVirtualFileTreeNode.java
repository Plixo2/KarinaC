package org.karina.lang.lsp.impl;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class DefaultVirtualFileTreeNode implements VirtualFileTreeNode {
    public final String name;
    public final @Nullable ObjectPath path;
    public final List<DefaultVirtualFileTreeNode> children = new ArrayList<>();
    public final List<VirtualFileNode> leafs = new ArrayList<>();


    public static DefaultVirtualFileTreeNode root() {
        return new DefaultVirtualFileTreeNode("", null);
    }
    public static FileTreeBuilder builder() {
        return new FileTreeBuilder();
    }

    public static class FileTreeBuilder {
        private FileTreeBuilder() {
        }

        public VirtualFileTreeNode build(List<VirtualFile> files) {
            var root = DefaultVirtualFileTreeNode.root();

            for (var file : files) {
                addToTree(root, file);
            }

            return root;
        }

        private void addToTree(DefaultVirtualFileTreeNode root, VirtualFile file) {
            var path = Path.of(file.uri());
            DefaultVirtualFileTreeNode current = root;
            ObjectPath objectPath = null;

            for (int i = 0; i < path.getNameCount(); i++) {
                var segment = path.getName(i).toString();
                objectPath = Optional.ofNullable(objectPath).map(ref -> ref.append(segment))
                                     .orElseGet(() -> new ObjectPath(segment));

                var isLeaf = (i == path.getNameCount() - 1);

                DefaultVirtualFileTreeNode next = null;
                for (var child : current.children) {
                    if (child.name().equals(segment)) {
                        next = child;
                        break;
                    }
                }

                if (next == null) {
                    if (isLeaf) {
                        var node = new VirtualFileNode(objectPath, segment, file);
                        current.leafs.add(node);
                        // should exit
                    } else {
                        next = new DefaultVirtualFileTreeNode(segment, objectPath);
                        current.children.add(next);
                    }
                }
                current = next;
            }
        }
    }
}




