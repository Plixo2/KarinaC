package org.karina.lang.lsp.lib;

import org.eclipse.lsp4j.DocumentSymbol;

import java.util.List;

public interface DocumentSymbolProvider {
    List<DocumentSymbol> getSymbols(String content);

}
