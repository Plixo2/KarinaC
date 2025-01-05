package org.karina.lang.lsp.internal;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.karina.lang.lsp.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * Proxy class for the Document service, delegating the actual work to the EventHandler.
 */
public class DocumentEventProxy implements TextDocumentService {

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        var hover = EventHandler.INSTANCE.onHover(uri, params.getPosition());
        return CompletableFuture.completedFuture(hover);
    }

    @Override
    public CompletableFuture<List<InlayHint>> inlayHint(InlayHintParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());

        var hover = EventHandler.INSTANCE.onInlayHints(uri, params.getRange());
        return CompletableFuture.completedFuture(hover);
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        var tokens = EventHandler.INSTANCE.onSemanticToken(uri);
        return CompletableFuture.completedFuture(new SemanticTokens(tokens));
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        EventHandler.INSTANCE.onOpen(uri);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        EventHandler.INSTANCE.onClose(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        EventHandler.INSTANCE.onSave(uri);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        for (var contentChange : params.getContentChanges()) {
            EventHandler.INSTANCE.onChangeFromMemory(uri, contentChange.getText());
        }
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params
    ) {
        var uri = EventHandler.INSTANCE.toUri(params.getTextDocument().getUri());
        var position = params.getPosition();

        var location = EventHandler.INSTANCE.getDefinition(uri, position);
        var locationList = new ArrayList<Location>();
        if (location != null) {
            locationList.add(location);
        }
        return CompletableFuture.completedFuture(Either.forLeft(locationList));
    }
}
