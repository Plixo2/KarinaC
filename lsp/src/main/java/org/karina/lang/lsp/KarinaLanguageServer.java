package org.karina.lang.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.lsp.impl.DefaultRealFileSystem;
import org.karina.lang.lsp.impl.DefaultVirtualFileSystem;
import org.karina.lang.lsp.impl.DefaultVirtualFileTreeNode;
import org.karina.lang.lsp.impl.FileTreePrinter;
import org.karina.lang.lsp.lib.FileTransaction;
import org.karina.lang.lsp.lib.RealFileSystem;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class KarinaLanguageServer implements LanguageServer, LanguageClientAware {
    public static final boolean USE_FILESYSTEM_EVENTS = false;

    public final VirtualFileSystem vfs = new DefaultVirtualFileSystem();
    public final RealFileSystem rfs = new DefaultRealFileSystem();
    private VirtualFileTreeNode treeNode = DefaultVirtualFileTreeNode.root();
    private @Nullable URI rootUri;


    private LanguageClient theClient;

    private KDocumentService documentService;
    private KWorkspaceService workspaceService;

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
            this.rootUri = VirtualFileSystem.toUri(folders.getFirst().getUri());
        } else if (params.getRootUri() != null) {
            this.rootUri = VirtualFileSystem.toUri(params.getRootUri());
        } else if (params.getRootPath() != null) {
            this.rootUri = VirtualFileSystem.toUri(params.getRootPath());
        } else {
            this.rootUri = null;
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

        this.documentService = new KDocumentService(this);
        this.workspaceService = new KWorkspaceService(this);

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
    }


    @Override
    public TextDocumentService getTextDocumentService() {
        return Objects.requireNonNull(this.documentService, "Document service is not initialized");
    }
    @Override
    public WorkspaceService getWorkspaceService() {
        return Objects.requireNonNull(this.workspaceService, "Workspace service is not initialized");
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
        this.theClient.publishDiagnostics(new PublishDiagnosticsParams());
    }



    public void handleTransaction(@Nullable FileTransaction transaction) {
        if (transaction == null) {
            return;
        }
        var files = this.vfs.files();

        if (transaction.isObjectNew()) {
            this.treeNode = DefaultVirtualFileTreeNode.builder().build(files);
        }

        var prettyTree = FileTreePrinter.prettyTree(this.treeNode);


        logMessage("---transaction---");
        logMessage(transaction);
        logMessage("---files---");
        for (var file : files) {
            logMessage(file.uri());
        }
        logMessage("---tree---");
        logMessage(prettyTree);
        logMessage("---end---");

    }


}
