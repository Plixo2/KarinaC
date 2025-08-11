package org.karina.lang.lsp;

import org.eclipse.lsp4j.*;
import org.karina.lang.lsp.lib.SemanticToken;

import java.util.List;

public class Capabilities {


    public static ServerCapabilities get() {

        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

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

        var codeLensOptions = new CodeLensOptions();
        codeLensOptions.setResolveProvider(false);
        capabilities.setCodeLensProvider(codeLensOptions);

//        var execCmdOptions = new ExecuteCommandOptions();
//        execCmdOptions.setCommands(List.of("karina.run"));
//        execCmdOptions.setWorkDoneProgress(false);
//        capabilities.setExecuteCommandProvider(execCmdOptions);


        return capabilities;
    }
}
