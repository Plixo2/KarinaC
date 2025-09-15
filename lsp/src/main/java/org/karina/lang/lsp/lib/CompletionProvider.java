package org.karina.lang.lsp.lib;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;
import java.util.List;

public interface CompletionProvider {

    List<CompletionItem> getItems(
            OneShotCompiler.CompiledModelIndex index,
            URI uri,
            Position position
    );
}
