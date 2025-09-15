package org.karina.lang.lsp.base;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.events.RequestEvent;
import org.karina.lang.lsp.lib.events.UpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


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

    @Override
    public CompletableFuture<@Nullable Hover> hover(HoverParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        var pos = params.getPosition();
        return this.eventService.request(new RequestEvent.RequestHover(uri, pos));
    }

    @Override
    public CompletableFuture<List<InlayHint>> inlayHint(InlayHintParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        var range = params.getRange();
        return this.eventService.request(new RequestEvent.RequestInlayHints(uri, range));
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(
            CodeActionParams params
    ) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        var range = params.getRange();
        var context = params.getContext();

        return this.eventService.request(new RequestEvent.RequestCodeActions(uri, range, context))
                                .thenApply(ref -> ref.stream().map(Either::<Command, CodeAction>forRight).toList());

    }

    private void println(Object... obj) {
        this.eventService.send(new ClientEvent.Log(
                String.join(" ", Stream.of(obj).map(Objects::toString).toList()),
                MessageType.Log
        ));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params
    ) {
        var pos = params.getPosition();
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        return this.eventService.request(new RequestEvent.RequestDefinition(uri, pos)).thenApply(ref -> {
            if (ref == null) {
                return Either.forLeft(List.of());
            } else {
                return Either.forLeft(List.of(ref));
            }
        });
    }
}
