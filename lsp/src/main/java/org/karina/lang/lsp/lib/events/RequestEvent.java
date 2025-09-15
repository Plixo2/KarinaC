package org.karina.lang.lsp.lib.events;

import org.eclipse.lsp4j.*;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

/// Request from the client to the server
public sealed interface RequestEvent<R> {

    // ------------------ Text Document Semantic Token Request ------------------
    record RequestSemanticTokens(URI uri) implements RequestEvent<SemanticTokens> {};

    // ------------------ Text Document Code Lens Request ------------------
    record RequestCodeLens(URI uri) implements RequestEvent<List<? extends CodeLens>> {}

    record RequestDocumentSymbols(URI uri) implements RequestEvent<List<DocumentSymbol>> {}

    record RequestCompletions(URI uri, Position position) implements RequestEvent<List<CompletionItem>> {}

    record RequestHover(URI uri, Position position) implements RequestEvent<Hover> {}

    record RequestDefinition(URI uri, Position position) implements RequestEvent<Location> {}

    record RequestInlayHints(URI uri, Range range) implements RequestEvent<List<InlayHint>> {}
    record RequestCodeActions(URI uri, Range range, CodeActionContext context) implements RequestEvent<List<CodeAction>> {}

}
