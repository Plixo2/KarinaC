package org.karina.lang.lsp.lib;

import org.karina.lang.compiler.utils.FileNode;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

public interface VirtualFileTreeNode extends FileTreeNode {


    String name();

    ObjectPath path();

    List<? extends VirtualFileTreeNode> children();

    List<VirtualFileNode> leafs();

    record VirtualFileNode(
            ObjectPath path, String name, VirtualFile content
    ) implements FileNode { }

}
