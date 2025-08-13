package org.karina.lang.lsp;

import karina.lang.Option;
import karina.lang.Result;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.utils.FileLoader;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.lsp.base.EventClientService;
import org.karina.lang.lsp.base.Process;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.UpdateEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.events.RequestEvent;
import org.karina.lang.lsp.impl.*;
import org.karina.lang.lsp.lib.*;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class KarinaLSP implements EventService {

    public static final boolean USE_FILESYSTEM_EVENTS = true;

    public final VirtualFileSystem vfs = new DefaultVirtualFileSystem();
    public final RealFileSystem rfs = new DefaultRealFileSystem();
    private VirtualFileTreeNode treeNode = DefaultVirtualFileTreeNode.root();

    private SemanticTokenProvider semanticTokenProvider = new DefaultSemanticTokenProvider();
    private OneShotCompiler oneShotCompiler = new OneShotCompiler(this);

    private Option<BuildConfig> buildConfig = Option.none();

    private EventClientService theClient;
    private ClientCapabilities clientCapabilities;

    private ClientConfiguration clientConfiguration = ClientConfiguration.getDefault();

    @Override
    public ServerInfo serverInfo() {
        return new ServerInfo(
                "Karina Language Server",
                KarinaCompiler.VERSION
        );
    }

    @Override
    public void onInit(EventClientService theClient, ClientCapabilities capabilities, Option<ClientInfo> clientInfo, URI clientRoot) {
        this.theClient = theClient;
        this.clientCapabilities = capabilities;


        if (USE_FILESYSTEM_EVENTS) {
            //<editor-fold desc="ensure dynamic DidChangeWatchedFiles registration capabilities">
            var workspaceClientCapabilities = KarinaLSP.this.clientCapabilities.getWorkspace();
            if (workspaceClientCapabilities == null) {
                throw new IllegalStateException(
                        "Client does not support 'workspace' capabilities"
                );
            }
            var clientDidChangeWatchedFilesCapabilities = workspaceClientCapabilities.getDidChangeWatchedFiles();
            if (clientDidChangeWatchedFilesCapabilities == null) {
                throw new IllegalStateException(
                        "Client does not support 'workspace/didChangeWatchedFiles' capability"
                );
            }
            if (!clientDidChangeWatchedFilesCapabilities.getDynamicRegistration()) {
                throw new IllegalStateException(
                        "Client does not support dynamic registration for 'workspace/didChangeWatchedFiles' capability"
                );
            }
            //</editor-fold>

            var watchers = new ArrayList<FileSystemWatcher>();
            watchers.add(new FileSystemWatcher(
                    Either.forLeft("**/*.{krna}"),
                    WatchKind.Create | WatchKind.Change | WatchKind.Delete
            ));

            var registration = new Registration(
                    "watchFiles",
                    "workspace/didChangeWatchedFiles",
                    new DidChangeWatchedFilesRegistrationOptions(watchers)
            );
            this.theClient.send(new ClientEvent.RegisterCapability(registration));
        }

        if (clientInfo instanceof Option.Some(var info)) {
            var version = Optional.ofNullable(info.getVersion()).orElse("");
            logMessage(
                    "Hello " + info.getName() + " " + version
            );
        }

        var configFromJson = JsonBuildConfig.fromJsonFile(clientRoot, this.rfs);

        if (configFromJson.asError() instanceof Option.Some(var e)) {
            errorMessage(e.getMessage());
        }
        if (configFromJson instanceof Result.Ok(var config)) {
            this.buildConfig = Option.some(config);
            loadInitial(config.projectRootUri());
        }

    }
    private void loadInitial(URI projectRootURI) {
        var files = this.rfs.listAllFilesRecursively(
                projectRootURI,
                new FileLoader.FilePredicate("krna")
        );

        switch (files) {
            case Result.Ok(List<URI> v) -> {
                for (var uri : v) {
                    if (!this.vfs.isFileOpen(uri)) {
                        var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(uri));
                        if (content == null) {
                            continue;
                        }
                        this.handleTransaction(this.vfs.reloadFromDisk(uri, content));
                    }
                }
            }
            case Result.Err(IOException e) -> {
                errorMessage(e.getMessage());
            }
        }
    }

    public void errorMessage(Object object) {
        var value = Objects.toString(object);
        this.theClient.send(new ClientEvent.Log(value, MessageType.Error));
        this.theClient.send(new ClientEvent.Popup(value, MessageType.Error));
    }

    public void infoMessage(Object object) {
        var value = Objects.toString(object);
        this.theClient.send(new ClientEvent.Log(value, MessageType.Info));
        this.theClient.send(new ClientEvent.Popup(value, MessageType.Info));
    }

    public void warningMessage(Object object) {
        var value = Objects.toString(object);
        this.theClient.send(new ClientEvent.Log(value, MessageType.Warning));
        this.theClient.send(new ClientEvent.Popup(value, MessageType.Warning));
    }

    public void logMessage(Object object) {
        var value = Objects.toString(object);
        this.theClient.send(new ClientEvent.Log(value, MessageType.Log));
    }

    public void handleTransaction(Option<FileTransaction> transaction) {
        if (!(transaction instanceof Option.Some(var fileTransaction))) {
            return;
        }
        if (!(this.buildConfig instanceof Option.Some(var config))) {
            return;
        }
        var files = this.vfs.files();
        if (fileTransaction.isObjectNew()) {
            this.treeNode = DefaultVirtualFileTreeNode.builder().build(files, config.projectRootUri());
            var prettyTree = FileTreePrinter.prettyTree(this.treeNode);
            logMessage(prettyTree);
        }


        this.oneShotCompiler.build(this.treeNode, files,
                this.clientConfiguration.logLevel().orElse(ClientConfiguration.LoggingLevel.NONE)
        );

//        var start = System.currentTimeMillis();
//
//
//        var end = System.currentTimeMillis();
//        logMessage("Transaction handled in " + (end - start) + "ms");

    }


    @Override
    public void update(UpdateEvent update) {
        switch (update) {
            case UpdateEvent.OpenFile(URI uri, int version, String language, String text) -> {
                this.handleTransaction(this.vfs.openFile(uri, text, version));
            }
            case UpdateEvent.ChangeFile(URI uri, String text, Range range, int version) -> {
                if (this.vfs.isFileOpen(uri)) {
                    // Only handles full text change
                    this.handleTransaction(this.vfs.updateFile(uri, text, version));
                }
            }
            case UpdateEvent.CloseFile(URI uri) -> {
                this.handleTransaction(this.vfs.closeFile(uri));
            }
            case UpdateEvent.SaveFile(URI uri) -> {
                this.handleTransaction(this.vfs.saveFile(uri));
            }
            case UpdateEvent.CreateWatchedFile(URI uri) -> {
                if (!this.vfs.isFileOpen(uri)) {
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(uri));
                    if (content != null) {
                        this.handleTransaction(this.vfs.reloadFromDisk(uri, content));
                    }
                }
            }
            case UpdateEvent.DeleteWatchedFile(URI uri) -> {
                if (!this.vfs.isFileOpen(uri)) {
                    this.handleTransaction(this.vfs.deleteFile(uri));
                }
            }
            case UpdateEvent.CreateFile(URI uri) -> {
                if (!this.vfs.isFileOpen(uri)) {
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(uri));
                    if (content != null) {
                        this.handleTransaction(this.vfs.reloadFromDisk(uri, content));
                    }
                }
            }
            case UpdateEvent.DeleteFile(URI uri) -> {
                if (!this.vfs.isFileOpen(uri)) {
                    this.handleTransaction(this.vfs.deleteFile(uri));
                }
            }
            case UpdateEvent.RenameFile(URI oldUri, URI newUri) -> {
                if (!this.vfs.isFileOpen(oldUri)) {
                    this.handleTransaction(this.vfs.deleteFile(oldUri));
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(newUri));
                    if (content != null) {
                       this.handleTransaction(this.vfs.reloadFromDisk(newUri, content));
                    }
                }
            }
            case UpdateEvent.UpdateClientConfig updateClientConfig -> {
                this.clientConfiguration = updateClientConfig.configuration();
            }
            case UpdateEvent.ExecuteCommand(String command, List<Object> args) -> {
                if (command.equals("karina.run")) {
                    if (args.isEmpty()) {
                        warningMessage("No URI provided for command: " + command);
                        return;
                    }
                    if (args.size() > 1) {
                        warningMessage("Too many arguments for command: " + command);
                    }

                    var uriStr = Objects.toString(args.getFirst());
                    if (uriStr.startsWith("\"") && uriStr.endsWith("\"")) {
                        uriStr = uriStr.substring(1, uriStr.length() - 1);
                    }
                    var uri = VirtualFileSystem.toUri(uriStr);
                    var files = this.vfs.files();
                    this.oneShotCompiler.run(this.treeNode, files, uri);
                } else {
                    warningMessage("Unknown command: " + command);
                }
            }
        }
    }

    @Override
    public <T> CompletableFuture<T> request(RequestEvent<T> request) {
        switch (request) {
            case RequestEvent.SemanticTokensRequest(URI uri) -> {
                SemanticTokens result = new SemanticTokens();
                if (this.vfs.getContent(uri) instanceof Option.Some(var content)) {
                    var start = System.currentTimeMillis();
                    var tokens = this.semanticTokenProvider.getTokens(content);
                    result.setData(tokens);
                    var end = System.currentTimeMillis();
                    logMessage("Semantic tokens in " + (end - start) + "ms");
                } else {
                    warningMessage("No content found for URI: " + uri);
                }
                return CompletableFuture.completedFuture((T) result);

            }
            case RequestEvent.RequestCodeLens(URI uri) -> {
                if (this.oneShotCompiler.findMain(this.treeNode, uri) instanceof Option.Some(var mainMethod)) {
                    var codeLens = new CodeLens();
                    var start = mainMethod.region().start();
                    codeLens.setRange(new Range(
                            new Position(start.line(), start.column()),
                            new Position(start.line(), start.column())
                    ));
                    codeLens.setCommand(new Command(
                            "Run",
                            "karina.run",
                            List.of(uri.toString())
                    ));
                    return CompletableFuture.completedFuture((T) List.of(codeLens));
                } else {
                    return CompletableFuture.completedFuture((T) List.of());
                }

            }
        }
    }

    @Override
    public void send(ClientEvent clientEvent) {
        this.theClient.send(clientEvent);
    }

    @Override
    public void sendTerminal(String message) {
        this.theClient.sendTerminal(message);
    }

    @Override
    public Process createProgress(String title, Process.ThrowingFunction<Process.Progress, String> process) {
        return this.theClient.createProgress(title, process);
    }

}
