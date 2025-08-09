package org.karina.lang.lsp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.FileLoader;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.UpdateEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.events.RequestEvent;
import org.karina.lang.lsp.impl.*;
import org.karina.lang.lsp.lib.*;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class KarinaLanguageServer implements LanguageServer, LanguageClientAware,
        EventService {
    public static final boolean USE_FILESYSTEM_EVENTS = true;

    public final VirtualFileSystem vfs = new DefaultVirtualFileSystem();
    public final RealFileSystem rfs = new DefaultRealFileSystem();
    private VirtualFileTreeNode treeNode = DefaultVirtualFileTreeNode.root();

    private @Nullable BuildConfig buildConfig;
    private URI clientURI;

    private final KDocumentService documentService = new KDocumentService(this);
    private final KWorkspaceService workspaceService =  new KWorkspaceService(this);

    private LanguageClient theClient;


    private @Nullable ClientInfo clientInfo;
    private ClientCapabilities clientCapabilities;



    @Override
    public void connect(LanguageClient client) {
        this.theClient = client;
    }
    @Override
    public void setTrace(SetTraceParams params) {
        params.setValue(TraceValue.Verbose);
    }


    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        this.clientInfo = params.getClientInfo();
        this.clientCapabilities = params.getCapabilities();


        var workspace = this.clientCapabilities.getWorkspace();
        if (workspace == null) {
            errorMessage("No workspace capabilities found in client capabilities");
            return CompletableFuture.failedFuture(new IllegalStateException("No workspace capabilities found"));
        }
        var folders = params.getWorkspaceFolders();

        if (folders != null && !folders.isEmpty()) {
            this.clientURI = VirtualFileSystem.toUri(folders.getFirst().getUri());
        } else if (params.getRootUri() != null) {
            this.clientURI = VirtualFileSystem.toUri(params.getRootUri());
        } else if (params.getRootPath() != null) {
            this.clientURI = VirtualFileSystem.toUri(params.getRootPath());
        } else {
            return CompletableFuture.failedFuture(new IllegalStateException("No root found in initialize parameters"));
        }


        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

        //<editor-fold desc="Workspace capabilities">
        var workspaceCapabilities = new WorkspaceServerCapabilities();

        var fileOperations = new FileOperationsServerCapabilities();
        var filter = new FileOperationFilter(new FileOperationPattern("**/*.krna"), "file");
        fileOperations.setDidCreate(new FileOperationOptions(List.of(filter)));
        fileOperations.setDidDelete(new FileOperationOptions(List.of(filter)));
        fileOperations.setDidRename(new FileOperationOptions(List.of(filter)));
        workspaceCapabilities.setFileOperations(fileOperations);

        var folderCapabilities = new WorkspaceFoldersOptions();
        folderCapabilities.setSupported(false);
        folderCapabilities.setChangeNotifications(false);
        workspaceCapabilities.setWorkspaceFolders(folderCapabilities);

        capabilities.setWorkspace(workspaceCapabilities);
        //</editor-fold>


        return CompletableFuture.supplyAsync(() -> {
            InitializeResult result = new InitializeResult();
            result.setCapabilities(capabilities);
            result.setServerInfo(new ServerInfo(
                    "Karina Language Server",
                    KarinaCompiler.VERSION
            ));
            return result;
        });
    }
    @Override
    public void initialized(InitializedParams params) {
        if (USE_FILESYSTEM_EVENTS) {
            //<editor-fold desc="ensure dynamic DidChangeWatchedFiles registration capabilities">
            var workspaceClientCapabilities = KarinaLanguageServer.this.clientCapabilities.getWorkspace();
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
            this.theClient.registerCapability(new RegistrationParams(List.of(registration)));
        }

        if (this.clientInfo != null) {
            var version = Optional.ofNullable(this.clientInfo.getVersion()).orElse("");
            logMessage(
                    "Hello " + this.clientInfo.getName() + " " + version
            );
        }

        switch (JsonBuildConfig.fromJsonFile(this.clientURI, this)) {
            case IOResult.Error<JsonBuildConfig>(var e) -> {
                errorMessage(e.getMessage());
                this.buildConfig = null;
            }
            case IOResult.Success<JsonBuildConfig> v -> {
                this.buildConfig = v.value();
            }
        }
        if (this.buildConfig != null) {
            loadInitial(this.buildConfig.projectRootUri());
        }


    }
    private void loadInitial(URI projectRootURI) {
        var files = this.rfs.listAllFilesRecursively(
                projectRootURI,
                new FileLoader.FilePredicate("krna")
        );
        switch (files) {
            case IOResult.Error<List<URI>>(var e) -> {
                errorMessage(e.getMessage());
            }
            case IOResult.Success<List<URI>>(var fileList) -> {
                for (var uri : fileList) {
                    if (!this.vfs.isFileOpen(uri)) {
                        var content = this.rfs.readFileFromDisk(uri).orMessageAndNull(this);
                        if (content == null) {
                            continue;
                        }
                        this.handleTransaction(this.vfs.reloadFromDisk(uri, content));
                    }
                }
            }
        }
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
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }
    @Override
    public void exit() {
        System.exit(0);
    }


    public void errorMessage(Object object) {
        var value = Objects.toString(object);
        var message = new MessageParams(MessageType.Error, value);
        this.theClient.logMessage(message);
        this.theClient.showMessage(message);
    }

    public void infoMessage(Object object) {
        var value = Objects.toString(object);
        var message = new MessageParams(MessageType.Info, value);
        this.theClient.logMessage(message);
        this.theClient.showMessage(message);
    }

    public void warningMessage(Object object) {
        var value = Objects.toString(object);
        var message = new MessageParams(MessageType.Warning, value);
        this.theClient.logMessage(message);
        this.theClient.showMessage(message);
    }

    public void logMessage(Object object) {
        var value = Objects.toString(object);
        var message = new MessageParams(MessageType.Log, value);
        this.theClient.logMessage(message);
    }



    public void handleTransaction(@Nullable FileTransaction transaction) {
        if (transaction == null || this.buildConfig == null) {
            return;
        }
        var files = this.vfs.files();
        var openFiles = files.stream().filter(VirtualFile::isOpen).toList();


        if (transaction.isObjectNew()) {
            this.treeNode = DefaultVirtualFileTreeNode.builder().build(files, this.buildConfig.projectRootUri());
        }


        var prettyTree = FileTreePrinter.prettyTree(this.treeNode);


//        logMessage("---stuff---");
//
//        logMessage(this.buildConfig);
//
//        logMessage("---transaction---");
//
//        logMessage(transaction);
//
//        logMessage("---files---");
//
//        for (var file : files) {
//            logMessage(file.uri()  + ": " + prev(file.content()));
//        }
//
//        logMessage("---open files---");
//
//        for (var file : openFiles) {
//            logMessage(file.uri());
//        }

//        logMessage("---tree---");
//
//        logMessage(prettyTree);
//
//        logMessage("---end---");

        testCompile();

    }
    private String prev(String c) {
        c = StringEscapeUtils.escapeJava(c);

        if (c.length() > 8) {
            return StringUtils.abbreviate(c, 8);
        } else {
            return c;
        }

    }

    private void testCompile() {
        testClear();


        var builder = KarinaCompiler.builder();
        builder.outputConfig(null);
        builder.useBinaryFormat(true);

        var errors = new DiagnosticCollection();

        builder.errorCollection(errors);
        builder.flightRecordCollection(null);

        var compiler = builder.build();
        var result = compiler.compile(this.treeNode);

        if (result == null) {
            pushErrors(errors);
        }

    }

    private void pushErrors(DiagnosticCollection logs) {
        Map<VirtualFile, List<Diagnostic>> diagnostics = new HashMap<>();

        for (var log : logs) {

            var information = new CodeDiagnosticInformation();
            log.entry().addInformation(information);

            var diagnosticAndFile = CodeDiagnosticInformation.toDiagnosticAndFile(information);
            if (diagnosticAndFile == null) {
                continue;
            }
            diagnostics.computeIfAbsent(
                    diagnosticAndFile.file(),
                    _ -> new ArrayList<>()
            ).add(diagnosticAndFile.diagnostic());

        }

        for (var value : diagnostics.entrySet()) {
            var file = value.getKey();
            var diagnosticsList = value.getValue();

            if (diagnosticsList.isEmpty()) {
                continue;
            }

            var publishDiagnosticsParams = new PublishDiagnosticsParams(
                    file.uri().toString(),
                    diagnosticsList
            );

            this.theClient.publishDiagnostics(publishDiagnosticsParams);
        }

    }

    private void testClear() {
        var files = this.vfs.files();
        for (var file : files) {
            this.theClient.publishDiagnostics(
                    new PublishDiagnosticsParams(file.uri().toString(), List.of())
            );
        }
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
                    var content = this.rfs.readFileFromDisk(uri).orMessageAndNull(this);
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
                    var content = this.rfs.readFileFromDisk(uri).orMessageAndNull(this);
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
                    var content = this.rfs.readFileFromDisk(newUri).orMessageAndNull(this);
                    if (content != null) {
                       this.handleTransaction(this.vfs.reloadFromDisk(newUri, content));
                    }
                }
            }

        }
    }

    @Override
    public <T> CompletableFuture<T> request(RequestEvent<T> request) {
        switch (request) {
            case RequestEvent.SemanticTokensRequest(URI params) -> {

            }
        }
        throw new NullPointerException("");
    }

    @Override
    public void send(ClientEvent clientEvent) {

        switch (clientEvent) {
            case ClientEvent.Log(String message, MessageType type) -> {
                this.theClient.logMessage(new MessageParams(type, message));
            }
            case ClientEvent.Popup(String message, MessageType type) -> {
                this.theClient.showMessage(new MessageParams(type, message));
            }
            case ClientEvent.RegisterCapability(Registration[] registration) -> {
                this.theClient.registerCapability(new RegistrationParams(Arrays.asList(registration)));
            }
        }
    }
}
