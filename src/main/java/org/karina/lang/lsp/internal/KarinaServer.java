package org.karina.lang.lsp.internal;

import lombok.Getter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;
import org.karina.lang.lsp.EventHandler;

import java.util.concurrent.CompletableFuture;

public class KarinaServer implements LanguageServer, LanguageClientAware {
    LanguageClient theClient;

    private final KarinaServerCapabilities capabilities = new KarinaServerCapabilities();
    private final DocumentEventProxy documentEventProxy = new DocumentEventProxy();
    private final WorkspaceEventProxy workspaceEventProxy = new WorkspaceEventProxy();

    @Getter
    private EventHandler eventHandler;

    @Override
    public void connect(LanguageClient client) {
        this.theClient = client;
        this.eventHandler = new EventHandler(client);
    }

    @Override
    public void setTrace(SetTraceParams params) {
        // do nothing
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        var response = this.capabilities.initialize();

        if (params.getWorkspaceFolders() == null) {
            this.eventHandler.errorMessage("No workspace folders found");
        }
        for (var workspaceFolder : params.getWorkspaceFolders()) {
            this.eventHandler.newWorkspace(this.eventHandler.toUri(workspaceFolder.getUri()));
        }

        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public void initialized(InitializedParams params) {
        this.eventHandler.onInit();
        this.capabilities.onInitialized();
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        // do nothing
    }

    @Override
    public DocumentEventProxy getTextDocumentService() {
        return this.documentEventProxy;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceEventProxy;
    }

}
