package org.karina.lang.lsp.lib;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.FileNode;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.lsp.impl.DefaultVirtualFileTreeNode;

import java.util.ArrayList;
import java.util.List;

public interface VirtualFileTreeNode extends FileTreeNode {

    String name();

    ObjectPath path();

    List<? extends VirtualFileTreeNode> children();

    List<VirtualFileNode> leafs();

    record VirtualFileNode(
            ObjectPath path, String name, VirtualFile content
    ) implements FileNode { }


    static List<VirtualFileNode> flatten(VirtualFileTreeNode fileTree) {
        var files = new ArrayList<>(fileTree.leafs());
        for (var child : fileTree.children()) {
            files.addAll(flatten(child));
        }
        return files;
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    final class NodeMapping {
        private final VirtualFileTreeNode root;

        private final ImmutableMap<VirtualFile, VirtualFileTreeNode.VirtualFileNode> fileMapping;

        public List<VirtualFile> files() {
            return new ArrayList<>(this.fileMapping.keySet());
        }

        public static NodeMapping EMPTY = new NodeMapping(
                DefaultVirtualFileTreeNode.root(),
                ImmutableMap.of()
        );
    }
}
