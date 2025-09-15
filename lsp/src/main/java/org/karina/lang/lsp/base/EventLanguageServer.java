package org.karina.lang.lsp.base;

import karina.lang.Option;
import karina.lang.ThrowableFunction;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.Capabilities;
import org.karina.lang.lsp.lib.LanguageClientExtension;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventClientService;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

public class EventLanguageServer implements LanguageServer, LanguageClientAware,
        EventClientService {
    private final EventService eventService;
    private final EventDocumentService documentService;
    private final EventWorkspaceService workspaceService;

    public EventLanguageServer(EventService eventService) {
        this.eventService = eventService;
        this.documentService = new EventDocumentService(this.eventService);
        this.workspaceService =  new EventWorkspaceService(this.eventService);
    }

    Map<String, Job> processes = new ConcurrentHashMap<>();

    LanguageClientExtension client;
    private InitializeParams initParams;
    @Override
    public void connect(LanguageClient client) {
        if (client instanceof LanguageClientExtension clientExtension) {
            this.client = clientExtension;
        } else {
            throw new IllegalArgumentException("Client must implement LanguageClientExtension");
        }

        System.setOut(createPrintStream());
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
            send(new ClientEvent.Log("No token provided to cancel process", MessageType.Warning));
            return;
        }
        var process = this.processes.get(token);
        if (process != null) {
            // also removes the process from the map
            process.cancel();
        } else {
            send(new ClientEvent.Log("No process with token " + token + " found to cancel", MessageType.Warning));
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
    public void sendTerminal(String message) {
        this.client.sendToTerminal(message);
    }

    @Override
    public void clearTerminal() {
        this.client.clearTerminal();
    }

    @Override
    public Job createJob(String title, ThrowableFunction<JobProgress, String, Exception> process) {
        return createJob(title, process, null);
    }

    @Override
    public Job createJob(
            String title, ThrowableFunction<JobProgress, String, Exception> process,
            @Nullable BooleanSupplier onKill
    ) {
        synchronized (this) {

            var token = UUID.randomUUID().toString();
            var processObj = new DefaultProcess();
            processObj.server = this;
            processObj.token = token;
            processObj.title = title;
            processObj.onKill = onKill;
            this.processes.put(token, processObj);
            var startParams = new WorkDoneProgressCreateParams(Either.forLeft(token));
            processObj.future = this.client.createProgress(startParams).thenRunAsync(() -> {
                var progressBegin = new WorkDoneProgressBegin();
                progressBegin.setTitle(title);
                progressBegin.setCancellable(true);
                progressBegin.setPercentage(0);
                var progressParams =
                        new ProgressParams(Either.forLeft(token), Either.forLeft(progressBegin));
                this.client.notifyProgress(progressParams);

                String message;
                try {
                    message = process.apply(processObj.new DefaultWorkProgress());
                } catch (Exception e) {
                    message = e.toString();
                    send(new ClientEvent.Log(
                            message,
                            MessageType.Error
                    ));
                }

                var end = new WorkDoneProgressEnd();
                end.setMessage(message);
                var endParams = new ProgressParams(Either.forLeft(token), Either.forLeft(end));
                this.client.notifyProgress(endParams);

                this.processes.remove(token);
            });
            return processObj;
        }
    }

    private PrintStream createPrintStream() {
        return new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    flushBuffer();
                } else {
                    this.buffer.append((char) b);
                    if (this.buffer.length() > 100000) {
                        this.buffer.setLength(0);
                        EventLanguageServer.this.sendTerminal("buffer too long, truncated\n");
                    }
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                for (int i = 0; i < len; i++) {
                    write(b[off + i]);
                }
            }

            @Override
            public void flush() {
                flushBuffer();
            }

            private void flushBuffer() {
                if (this.buffer.length() > 100000) {
                    this.buffer.setLength(0);
                    EventLanguageServer.this.sendTerminal("buffer too long, truncated\n");
                } else {
                    EventLanguageServer.this.sendTerminal(this.buffer.toString());
                    this.buffer.setLength(0);
                }
            }
        });
    }

}
