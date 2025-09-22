package org.karina.lang.lsp;

import org.eclipse.lsp4j.*;
import org.karina.lang.lsp.lib.SemanticToken;

import java.util.List;

public class Capabilities {


    public static ServerCapabilities get() {

        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Incremental);
        capabilities.setPositionEncoding(PositionEncodingKind.UTF16);

        capabilities.setSemanticTokensProvider(new SemanticTokensWithRegistrationOptions(
                new SemanticTokensLegend(
                        SemanticToken.SemanticTokenType.names(),
                        SemanticToken.SemanticTokenModifier.names()
                ),
                new SemanticTokensServerFull(true),
                false
        ));

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

        var symbolOptions = new DocumentSymbolOptions();
        capabilities.setDocumentSymbolProvider(symbolOptions);

        capabilities.setCodeActionProvider(true);

        capabilities.setDefinitionProvider(true);
        capabilities.setTypeDefinitionProvider(true);
        capabilities.setHoverProvider(true);
        var inlayHintCapabilities = new InlayHintRegistrationOptions();
        capabilities.setInlayHintProvider(inlayHintCapabilities);
        inlayHintCapabilities.setResolveProvider(true);

        var codeLensOptions = new CodeLensOptions();
        codeLensOptions.setResolveProvider(false);
        capabilities.setCodeLensProvider(codeLensOptions);

        var completionProvider = new CompletionOptions();
        completionProvider.setResolveProvider(false);
        completionProvider.setTriggerCharacters(List.of(".", "::"));
        completionProvider.setAllCommitCharacters(List.of(
                ".",
                "(",
                ",",
                " ",
                ":",
                "<",
                "[",
                "{",
                "=",
                "+",
                "-",
                "*",
                "%",
                "|",
                "&",
                "?",
                "/",
                "\"",
                "'"
        ));
        capabilities.setCompletionProvider(completionProvider);


        return capabilities;
    }
}
