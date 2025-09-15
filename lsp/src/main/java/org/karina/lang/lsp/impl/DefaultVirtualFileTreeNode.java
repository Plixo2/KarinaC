package org.karina.lang.lsp.impl;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.FileLoader;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class DefaultVirtualFileTreeNode implements VirtualFileTreeNode {
    public final String name;
    public final @Nullable ObjectPath path;
    public final List<DefaultVirtualFileTreeNode> children = new ArrayList<>();
    public final List<VirtualFileTreeNode.VirtualFileNode> leafs = new ArrayList<>();


    public static DefaultVirtualFileTreeNode root() {
        return new DefaultVirtualFileTreeNode("", null);
    }
    public static FileTreeBuilder builder() {
        return new FileTreeBuilder();
    }

    public static class FileTreeBuilder {
        private FileTreeBuilder() {
        }

        public VirtualFileTreeNode build(List<VirtualFile> files, URI projectRoot) {
            var rootPath = Optional.ofNullable(projectRoot)
                                   .map(Path::of).map(Path::toAbsolutePath)
                                   .map(Path::normalize).orElse(null);

            var root = DefaultVirtualFileTreeNode.root();

            for (var file : files) {
                addToTree(root, file, rootPath);
            }

            return root;
        }

        /// @param projectRoot Normalized and absolut path to the project root
        private void addToTree(DefaultVirtualFileTreeNode root, VirtualFile file, Path projectRoot) {
            var path = Path.of(file.uri());
            if (!path.startsWith(projectRoot)) {
                return;
            }
            path = projectRoot.relativize(path);

            DefaultVirtualFileTreeNode current = root;
            ObjectPath objectPath = null;

            for (int i = 0; i < path.getNameCount(); i++) {
                var segment = path.getName(i).toString();
                var isLeaf = (i == path.getNameCount() - 1);
                if (isLeaf) {
                    segment = FileLoader.getFileNameWithoutExtension(segment);
                }
                var name = segment;
                objectPath = Optional.ofNullable(objectPath).map(ref -> ref.append(name))
                                     .orElseGet(() -> new ObjectPath(name));



                DefaultVirtualFileTreeNode next = null;
                for (var child : current.children) {
                    if (child.name().equals(name)) {
                        next = child;
                        break;
                    }
                }

                if (next == null) {
                    if (isLeaf) {
                        var node = new VirtualFileNode(objectPath, name, file);
                        current.leafs.add(node);
                        // should exit
                    } else {
                        next = new DefaultVirtualFileTreeNode(name, objectPath);
                        current.children.add(next);
                    }
                }
                current = next;
            }
        }
    }
}




