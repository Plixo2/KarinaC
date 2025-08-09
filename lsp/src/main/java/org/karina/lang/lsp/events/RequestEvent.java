package org.karina.lang.lsp.events;

import org.eclipse.lsp4j.SemanticTokens;

import java.net.URI;

/// Request from the client to the server
public sealed interface RequestEvent<R> {

    // ------------------ Text Document Semantic Token Request ------------------
    record SemanticTokensRequest(URI params) implements RequestEvent<SemanticTokens> {};

}
