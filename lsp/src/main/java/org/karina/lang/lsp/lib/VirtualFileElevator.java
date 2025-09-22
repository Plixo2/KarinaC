package org.karina.lang.lsp.lib;

import karina.lang.Option;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.util.concurrent.CompletableFuture;

public interface VirtualFileElevator {

    void clearCompiledCache(VirtualFile file);

    Option<VirtualFile.CompiledFileCacheState> awaitCache(VirtualFileTreeNode.NodeMapping mapping, VirtualFile file);
}
