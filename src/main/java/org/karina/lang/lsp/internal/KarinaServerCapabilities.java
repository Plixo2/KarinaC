package org.karina.lang.lsp.internal;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.karina.lang.lsp.EventHandler;
import org.karina.lang.lsp.features.TokenProvider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is responsible for defining the capabilities of the server.
 */
public class KarinaServerCapabilities {

    public InitializeResult initialize() {
        var capabilities = new ServerCapabilities();


        var workspaceCapabilities = new WorkspaceServerCapabilities();
        workspaceCapabilities.setFileOperations(new FileOperationsServerCapabilities());

        var folderCapabilities = new WorkspaceFoldersOptions();
        folderCapabilities.setSupported(true);
        folderCapabilities.setChangeNotifications(true);
        workspaceCapabilities.setWorkspaceFolders(folderCapabilities);

        capabilities.setDefinitionProvider(true);
        capabilities.setSemanticTokensProvider(new SemanticTokensWithRegistrationOptions(
                new SemanticTokensLegend(
                        TokenProvider.SemanticTokenType.names(),
                        TokenProvider.SemanticTokenModifier.names()
                ),
                true,
                false
        ));
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setWorkspace(workspaceCapabilities);
        capabilities.setHoverProvider(true);

        return new InitializeResult(capabilities);
    }

    public void onInitialized() {

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

        EventHandler.INSTANCE.registerCapability(Collections.singletonList(registration));
    }

}
