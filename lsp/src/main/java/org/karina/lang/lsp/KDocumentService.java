package org.karina.lang.lsp;


import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.karina.lang.lsp.lib.VirtualFileSystem;

import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
public final class KDocumentService implements TextDocumentService {
    private final KarinaLanguageServer kls;

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocumentItem doc = params.getTextDocument();
        var uri = VirtualFileSystem.toUri(doc.getUri());
        this.kls.handleTransaction(this.kls.vfs.openFile(uri, doc.getText(), doc.getVersion()));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        int version = params.getTextDocument().getVersion();
        for (TextDocumentContentChangeEvent change : params.getContentChanges()) {
            // Only handles full text change
            this.kls.handleTransaction(this.kls.vfs.updateFile(uri, change.getText(), version));
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        this.kls.handleTransaction(this.kls.vfs.closeFile(uri));
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        var uri = VirtualFileSystem.toUri(params.getTextDocument().getUri());
        this.kls.handleTransaction(this.kls.vfs.saveFile(uri));
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        return TextDocumentService.super.rename(params);
    }
}
