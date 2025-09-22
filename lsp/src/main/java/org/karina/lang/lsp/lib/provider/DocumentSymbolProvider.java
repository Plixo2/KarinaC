package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.DocumentSymbol;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface DocumentSymbolProvider {
    List<DocumentSymbol> getSymbols(ProviderArgs modelIndex, URI uri);

}
