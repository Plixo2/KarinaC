package org.karina.lang.lsp.base;


import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.events.RequestEvent;
import org.karina.lang.lsp.lib.events.UpdateEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
public final class EventDocumentService implements TextDocumentService {
    private final EventService eventService;

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocumentItem doc = params.getTextDocument();
        var uri = VirtualFileSystem.toUri(doc.getUri());
        this.eventService.update(new UpdateEvent.OpenFile(uri, doc.getVersion(), doc.getLanguageId(), doc.getText()));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        int version = params.getTextDocument().getVersion();
        for (TextDocumentContentChangeEvent change : params.getContentChanges()) {
            this.eventService.update(new UpdateEvent.ChangeFile(uri, change.getText(), change.getRange(), version));
        }
    }


    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        this.eventService.update(new UpdateEvent.CloseFile(uri));
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        this.eventService.update(new UpdateEvent.SaveFile(uri));
    }


    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        return this.eventService.request(new RequestEvent.RequestSemanticTokens(uri));
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        return this.eventService.request(new RequestEvent.RequestCodeLens(uri));
    }


    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
            DocumentSymbolParams params
    ) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        return this.eventService.request(new RequestEvent.RequestDocumentSymbols(uri)).thenApply(symbols ->
                symbols.stream().map(Either::<SymbolInformation, DocumentSymbol>forRight).toList()
        );
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
            CompletionParams position
    ) {
        var pos = position.getPosition();
        var uri = VirtualFileSystem.toUri(position.getTextDocument().getUri());
        return this.eventService.request(new RequestEvent.RequestCompletions(uri, pos)).thenApply(Either::forLeft);
    }
}
