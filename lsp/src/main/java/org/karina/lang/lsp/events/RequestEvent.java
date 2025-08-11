package org.karina.lang.lsp.events;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.SemanticTokens;

import java.net.URI;
import java.util.List;

/// Request from the client to the server
public sealed interface RequestEvent<R> {

    // ------------------ Text Document Semantic Token Request ------------------
    record SemanticTokensRequest(URI uri) implements RequestEvent<SemanticTokens> {};

    // ------------------ Text Document Code Lens Request ------------------
    record RequestCodeLens(URI uri) implements RequestEvent<List<? extends CodeLens>> {}
}
