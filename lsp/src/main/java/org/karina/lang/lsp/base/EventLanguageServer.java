package org.karina.lang.lsp.base;

import karina.lang.Option;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.*;
import org.karina.lang.lsp.Capabilities;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.lib.VirtualFileSystem;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class EventLanguageServer implements LanguageServer, LanguageClientAware, EventClientService {
    private final EventService eventService;
    private final EventDocumentService documentService;
    private final EventWorkspaceService workspaceService;

    public EventLanguageServer(EventService eventService) {
        this.eventService = eventService;
        this.documentService = new EventDocumentService(this.eventService);
        this.workspaceService =  new EventWorkspaceService(this.eventService);
    }

    Map<String, Process> processes = new ConcurrentHashMap<>();

    LanguageClient client;
    private InitializeParams initParams;
    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }
    @Override
    public void setTrace(SetTraceParams params) {
        params.setValue(TraceValue.Verbose);
    }


    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        this.initParams = params;
        var workspace = params.getCapabilities().getWorkspace();
        if (workspace == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Client appears to not support workspace capabilities"));
        }

        var folders = params.getWorkspaceFolders();
        if (folders == null || folders.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalStateException(
                    "No workspace folders provided. Please set a workspace folder in your client."
            ));
        }
        var serverInfo = this.eventService.serverInfo();
        var result = new InitializeResult();
        result.setCapabilities(Capabilities.get());
        result.setServerInfo(serverInfo);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public void initialized(InitializedParams params) {
        var folders = this.initParams.getWorkspaceFolders();
        URI root = VirtualFileSystem.toUri(folders.getFirst().getUri());
        var clientInfo = Option.fromNullable(this.initParams.getClientInfo());
        this.eventService.onInit(this, this.initParams.getCapabilities(), clientInfo, root);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.documentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public synchronized void cancelProgress(WorkDoneProgressCancelParams params) {
        var token = params.getToken().getLeft();
        if (token == null) {
            return;
        }
        var process = this.processes.get(token);
        if (process != null) {
            // also removes the process from the map
            process.cancel();
        }
    }

    @Override
    public synchronized void send(ClientEvent clientEvent) {
        switch (clientEvent) {
            case ClientEvent.Log(String message, MessageType type) -> {
                this.client.logMessage(new MessageParams(type, message));
            }
            case ClientEvent.Popup(String message, MessageType type) -> {
                this.client.showMessage(new MessageParams(type, message));
            }
            case ClientEvent.RegisterCapability(Registration[] registration) -> {
                this.client.registerCapability(new RegistrationParams(Arrays.asList(registration)));
            }
            case ClientEvent.PublishDiagnostic(URI uri, List<org.eclipse.lsp4j.Diagnostic> diagnostics) -> {
                this.client.publishDiagnostics(new PublishDiagnosticsParams(uri.toString(), diagnostics));
            }
        }
    }


    @Override
    public synchronized Process createProgress(String title, Process.ThrowingFunction<Process.Progress, String> process) {
        var token = UUID.randomUUID().toString();
        var processObj = new Process();
        processObj.server = this;
        processObj.token = token;

        var startParams = new WorkDoneProgressCreateParams(Either.forLeft(token));
        processObj.future = this.client.createProgress(startParams).thenRun(() -> {
            var progressBegin = new WorkDoneProgressBegin();
            progressBegin.setTitle(title);
            progressBegin.setCancellable(true);
            progressBegin.setPercentage(0);
            var progressParams =
                    new ProgressParams(Either.forLeft(token), Either.forLeft(progressBegin));
            this.client.notifyProgress(progressParams);

            String message;
            try {
                message = process.apply(processObj.new Progress());
            } catch (Exception e) {
                message = e.getMessage();
            }

            var end = new WorkDoneProgressEnd();
            end.setMessage(message);
            var endParams = new ProgressParams(Either.forLeft(token), Either.forLeft(end));
            this.client.notifyProgress(endParams);

            this.processes.remove(token);
        });
        this.processes.put(token, processObj);

        return processObj;
    }
}
