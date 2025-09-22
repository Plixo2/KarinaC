package org.karina.lang.lsp;

import karina.lang.Option;
import karina.lang.Result;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.utils.FileLoader;
import org.karina.lang.lsp.impl.provider.*;
import org.karina.lang.lsp.lib.events.*;
import org.karina.lang.lsp.impl.*;
import org.karina.lang.lsp.lib.*;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;
import org.karina.lang.lsp.lib.provider.*;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RequiredArgsConstructor
public final class KarinaLSP implements EventService {

    public static final boolean USE_FILESYSTEM_EVENTS = true;

    public final RealFileSystem rfs = new DefaultRealFileSystem();
    private final VirtualFileSystem vfs = new DefaultVirtualFileSystem(this);
    private final VirtualFileElevator elevator = new DefaultVirtualFileElevator(this);

    private final SemanticTokenProvider  semanticTokenProvider  = new DefaultSemanticTokenProvider(this);
    private final DocumentSymbolProvider documentSymbolProvider = new DefaultDocumentSymbolProvider(this);
    private final CompletionProvider     completionProvider     = new DefaultCompletionProvider(this);
    private final HoverProvider          hoverProvider          = new DefaultHoverProvider(this);
    private final DefinitionProvider     definitionProvider     = new DefaultDefinitionProvider(this);
    private final InlayHintProvider      inlayHintProvider      = new DefaultInlayHintProvider(this);
    private final CodeLensProvider       codeLensProvider       = new DefaultCodeLensProvider(this);
    private final CompileProvider        compileProvider        = new DefaultCompileProvider(this);
    private final CodeActionProvider     codeActionProvider     = new DefaultCodeActionProvider();
    private final ExecuteCommandProvider ExecuteCommandProvider = new DefaultExecuteCommandProvider(this);

    private VirtualFileTreeNode.NodeMapping fileTree = VirtualFileTreeNode.NodeMapping.EMPTY;

    private CompiledModelIndex compiledModelIndex = CompiledModelIndex.EMPTY;
    private Job<CompiledModelIndex> lastJob = Job.of(this.compiledModelIndex);

    private final ModelUpdater modelUpdater = new DefaultModelUpdater(this);

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

        var level = this.clientConfiguration.logLevel().orElse(ClientConfiguration.LoggingLevel.NONE);

        if (fileTransaction.objectChange()) {
            var builder = DefaultVirtualFileTreeNode.builder();
            var newTree = builder.build(files, config.projectRootUri());
            this.fileTree = newTree;
            this.elevator.clearCompiledCache(fileTransaction.file());

            var prettyTree = FileTreePrinter.prettyTree(newTree.root());
            logMessage(prettyTree);
        }
        this.lastJob.cancel();

        this.lastJob = this.modelUpdater.update(
                this.fileTree,
                this.elevator,
                this.codeActionProvider,
                fileTransaction.file(),
                level
        );
    }


    @Override
    public void update(UpdateEvent update) {
        switch (update) {
            case UpdateEvent.UpdateFileEvent fileEvent -> {
                updateFileEvent(fileEvent);
            }
            case UpdateEvent.UpdateClientConfig updateClientConfig -> {
                this.clientConfiguration = updateClientConfig.configuration();
            }
        }
    }

    @Override
    public <T> CompletableFuture<T> request(RequestEvent<T> request) {
        switch (request) {
            case RequestEvent.RequestSemanticTokens(URI uri) -> {
                return runOnNewestIndex(
                        (index) -> this.semanticTokenProvider.getTokens(index, uri)
                );
            }
            case RequestEvent.RequestCodeLens(URI uri) -> {
                return runOnNewestIndex(
                        (index) -> this.codeLensProvider.getLenses(index, uri)
                );
            }
            case RequestEvent.RequestDocumentSymbols(URI uri) -> {
                return runOnNewestIndex(
                        (index) -> this.documentSymbolProvider.getSymbols(index, uri)
                );
            }
            case RequestEvent.RequestCompletions(URI uri, Position pos) -> {
                return runOnNewestIndex(
                        (index) -> this.completionProvider.getItems(index, uri, pos)
                );
            }
            case RequestEvent.RequestDefinition(URI uri, Position position) -> {
                return runOnNewestIndex(
                        (index) -> this.definitionProvider.getDefinition(index, uri, position, false)
                );
            }
            case RequestEvent.RequestTypeDefinition(URI uri, Position position) -> {
                return runOnNewestIndex(
                        (index) -> this.definitionProvider.getDefinition(index, uri, position, true)
                );
            }
            case RequestEvent.RequestHover(URI uri, Position position) -> {
                return runOnNewestIndex(
                        (index) -> this.hoverProvider.getHover(index, uri, position)
                );
            }
            case RequestEvent.RequestInlayHints(URI uri, Range range) -> {
                return runOnNewestIndex(
                        (index) -> this.inlayHintProvider.getHints(index, uri, range)
                );
            }
            case RequestEvent.RequestCodeActions(URI uri, Range range, CodeActionContext context) -> {
                return runOnNewestIndex(
                        (index) -> this.codeActionProvider.getAction(index, uri, range, context)
                );
            }
            case RequestEvent.RequestExecuteCommand(String command, List<Object> args) -> {
                return runOnNewestIndex(
                        (index) -> this.ExecuteCommandProvider.executeCommand(index, this.compileProvider, command, args)
                );
            }
        }
    }

    private <T> CompletableFuture<T> runOnNewestIndex(Function<ProviderArgs, Object> task) {
        return CompletableFuture.supplyAsync(() -> {
            var result = getCurrentIndex();
            var index = new ProviderArgs(
                    this.vfs,
                    this.fileTree,
                    this.elevator,
                    result
            );
            return (T) task.apply(index);
        });
    }

    private CompiledModelIndex getCurrentIndex() {
        var result = this.lastJob.awaitResult();
        if (result != null) {
            this.compiledModelIndex = result;
        }
        return this.compiledModelIndex;
    }

    @Override
    public void send(ClientEvent clientEvent) {
        this.theClient.send(clientEvent);
    }


    @Override
    public <T> Job<T> createJob(String title, Function<JobProgress, T> process) {
        return this.theClient.createJob(title, process);
    }

    private void updateFileEvent(UpdateEvent.UpdateFileEvent fileEvent) {
        var vfs = this.vfs;
        switch (fileEvent) {
            case UpdateEvent.UpdateFileEvent.OpenFile(URI uri, int version, String language, String text) -> {
                this.handleTransaction(vfs.openFile(uri, text, version));
            }
            case UpdateEvent.UpdateFileEvent.ChangeFile(URI uri, String text, @Nullable Range range, int version) -> {
                if (vfs.isFileOpen(uri)) {
                    this.handleTransaction(vfs.updateFile(uri, text, range, version));
                }
            }
            case UpdateEvent.UpdateFileEvent.CloseFile(URI uri) -> {
                this.handleTransaction(vfs.closeFile(uri));
            }
            case UpdateEvent.UpdateFileEvent.SaveFile(URI uri) -> {
                this.handleTransaction(vfs.saveFile(uri));
            }
            case UpdateEvent.UpdateFileEvent.CreateWatchedFile(URI uri) -> {
                if (!vfs.isFileOpen(uri)) {
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(uri));
                    if (content != null) {
                        this.handleTransaction(vfs.reloadFromDisk(uri, content));
                    }
                }
            }
            case UpdateEvent.UpdateFileEvent.DeleteWatchedFile(URI uri) -> {
                if (!vfs.isFileOpen(uri)) {
                    this.handleTransaction(vfs.deleteFile(uri));
                }
            }
            case UpdateEvent.UpdateFileEvent.CreateFile(URI uri) -> {
                if (!vfs.isFileOpen(uri)) {
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(uri));
                    if (content != null) {
                        this.handleTransaction(vfs.reloadFromDisk(uri, content));
                    }
                }
            }
            case UpdateEvent.UpdateFileEvent.DeleteFile(URI uri) -> {
                if (!vfs.isFileOpen(uri)) {
                    this.handleTransaction(vfs.deleteFile(uri));
                }
            }
            case UpdateEvent.UpdateFileEvent.RenameFile(URI oldUri, URI newUri) -> {
                if (!vfs.isFileOpen(oldUri)) {
                    this.handleTransaction(vfs.deleteFile(oldUri));
                    var content = unwrapOrMessageAndNull(this.rfs.readFileFromDisk(newUri));
                    if (content != null) {
                        this.handleTransaction(vfs.reloadFromDisk(newUri, content));
                    }
                }
            }
        }
    }


}
