package org.karina.lang.lsp.lib;

import org.karina.lang.lsp.impl.ClientConfiguration;
import org.karina.lang.lsp.lib.process.Job;
import org.karina.lang.lsp.lib.provider.CodeActionProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;

import javax.annotation.CheckReturnValue;

public interface ModelUpdater {

    @CheckReturnValue
    Job<CompiledModelIndex> update(
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator,
            CodeActionProvider actionProvider,
            VirtualFile file,
            ClientConfiguration.LoggingLevel loggingLevel
    );

}
