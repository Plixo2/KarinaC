package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface CompletionProvider {

    List<CompletionItem> getItems(
            ProviderArgs index,
            URI uri,
            Position position
    );
}
