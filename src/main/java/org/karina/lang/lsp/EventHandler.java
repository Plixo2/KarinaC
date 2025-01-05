package org.karina.lang.lsp;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.lsp.features.*;
import org.karina.lang.lsp.fs.ContentRoot;
import org.karina.lang.lsp.fs.SyncFileTree;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is responsible for handling and dispatching events from the client.
 * Can also publish messages to the client.
 */
public class EventHandler {
    public final LanguageClient theClient;
    public static EventHandler INSTANCE;

    @Getter
    @Accessors(fluent = true)
    private final ClientSettings settings = new ClientSettings();

    public EventHandler(LanguageClient theClient) {
        this.theClient = theClient;
        INSTANCE = this;
    }

    public List<Workspace> workspaces = new ArrayList<>();

    public URI toUri(String uri) {
        String decodedPath = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        var uri1 = URI.create(decodedPath);
        if (!"file".equals(uri1.getScheme())) {
            errorMessage("Unsupported scheme: " + uri1.getScheme());
        }
        return uri1;
    }

    public void newWorkspace(URI uri) {
        var path = Path.of(uri).normalize();
        ContentRoot.InitializedContentRoot content = null;
        var configLoadResult = FileLoading.loadConfig(path);

        if (configLoadResult instanceof FileLoading.FileLoadResult.Success(var config)) {
            var name = config.sourceDirectoryName;
            var ooPath = new ObjectPath(name);
            var rootTree = new SyncFileTree(ooPath, name, new ArrayList<>(), new ArrayList<>());
            content = new ContentRoot.InitializedContentRoot(config, rootTree);
        } else {
            errorMessage(configLoadResult.getErrorMessage());
        }

        var workspace = new Workspace(new ContentRoot(path, content));
        this.workspaces.add(workspace);
    }

    private List<Workspace> getWorkspacesForFile(URI uri) {
        var path = Path.of(uri).normalize();
        var result = new ArrayList<Workspace>();
        for (var workspace : this.workspaces) {
            if (workspace.doesWorkspaceContain(path)) {
                result.add(workspace);
            }
        }
        return result;
    }

    //#region Server to Client
    public void errorMessage(Object object) {
        var value = String.valueOf(object);
        var message = new MessageParams(MessageType.Error, value);
        this.theClient.logMessage(message);
        this.theClient.showMessage(message);
    }

    public void infoMessage(Object object) {
        var value = String.valueOf(object);
        var message = new MessageParams(MessageType.Info, value);
        this.theClient.logMessage(message);
        this.theClient.showMessage(message);
    }

    public void logMessage(Object object) {
        var value = String.valueOf(object);
        var message = new MessageParams(MessageType.Log, value);
        this.theClient.logMessage(message);
    }

    public void registerCapability(List<Registration> registrations) {
        this.theClient.registerCapability(new RegistrationParams(registrations));
    }

    private void publishDiagnosticsForFile(URI uri, List<Diagnostic> diagnostics) {
        this.theClient.publishDiagnostics(new PublishDiagnosticsParams(
                uri.toString(),
                diagnostics
        ));
    }

    public void publishDiagnostics() {
        for (var workspace : this.workspaces) {
            for (var virtualFile : workspace.getAllFiles()) {
                if (virtualFile.isDirtyDiagnostic()) {
                    publishDiagnosticsForFile(
                            virtualFile.uriPath().toUri(),
                            virtualFile.getAndClearDiagnostics()
                    );
                }
            }
        }
        for (var workspace : this.workspaces) {
            logMessage("virtual file system:\n"  + workspace.toPrettyTreeString());
        }
    }
    //#endregion

    //#region Events

    public void onInit() {
        for (var workspace : this.workspaces) {
            var result = workspace.loadAllFiles();
            if (result != null && !(result instanceof FileLoading.FileLoadResult.Success)) {
                errorMessage(result.getErrorMessage());
            }
        }
        onAnyFileChange();
    }

    public void onSave(URI uri) {
        onChangeFull(List.of(uri));
    }

    public void onOpen(URI uri) {
        onChangeFull(List.of(uri));
    }

    public void onClose(URI uri) {

    }

    public void onDelete(List<URI> uris) {
        for (var uri : uris) {
            var workspaces = getWorkspacesForFile(uri);
            for (var workspace : workspaces) {
                if (workspace.deleteFile(Path.of(uri).normalize())) {
                    logMessage("Deleted file: " + uri);
                }
            }
        }
        onAnyFileChange();
    }

    public void onCreate(List<URI> uris) {
        onChangeFull(uris);
    }

    public void onChangeFromMemory(URI uri, String text) {
        var workspaces = getWorkspacesForFile(uri);
        for (var workspace : workspaces) {
            workspace.updateFile(Path.of(uri).normalize(), text);
        }
        onAnyFileChange();
    }

    public void onChangeFull(List<URI> uris) {
        for (var uri : uris) {
            var workspaces = getWorkspacesForFile(uri);
            for (var workspace : workspaces) {
                var result = workspace.updateFile(Path.of(uri).normalize());
                if (result != null && !(result instanceof FileLoading.FileLoadResult.Success)) {
                    errorMessage(result.getErrorMessage());
                }
            }
        }
        onAnyFileChange();
    }

    public List<Integer> onSemanticToken(URI uri) {

        var workspaces = getWorkspacesForFile(uri);
        if (workspaces.isEmpty()) {
            return List.of();
        } else {
            var workspace = workspaces.getFirst();
            var loadedFile = workspace.getOrLoadFile(Path.of(uri).normalize());
            if (loadedFile == null) {
                return List.of();
            }
            if (!(loadedFile instanceof FileLoading.FileLoadResult.Success(var karinaFile))) {
                errorMessage(loadedFile.getErrorMessage());
                return List.of();
            }
            if (karinaFile.state() == null) {
                return List.of();
            }

            var textSource = karinaFile.state().source();
            var tokenProvider = new TokenProvider();
            var tokens = tokenProvider.getTokens(textSource);

            return Objects.requireNonNullElseGet(tokens, List::of);
        }

    }

    public @Nullable Location getDefinition(URI uri, Position position) {
        var workspaces = getWorkspacesForFile(uri);
        if (workspaces.isEmpty()) {
            return null;
        } else {
            var workspace = workspaces.getFirst();
            return new GoToDefinitionProvider().from(workspace, uri, position, this.settings);
        }
    }

    public @Nullable Hover onHover(URI uri, Position position) {
        var workspaces = getWorkspacesForFile(uri);
        if (workspaces.isEmpty()) {
            return null;
        } else {
            var workspace = workspaces.getFirst();
            var hoverProvider = new HoverProvider();
            return hoverProvider.from(workspace, uri, position, this.settings);
        }
    }

    public List<InlayHint> onInlayHints(URI uri, Range range) {
        var workspaces = getWorkspacesForFile(uri);
        if (workspaces.isEmpty()) {
            return null;
        } else {
            var workspace = workspaces.getFirst();

            var provider = new InlayHintProvider();

            return provider.from(workspace, uri, range, this.settings);
        }
    }

    //#endregion

    private void onAnyFileChange() {
        var infoProvider = new TypeInfoProvider();
        for (var workspace : this.workspaces) {
            var fullyTyped = workspace.isFullyTyped();
            if (!fullyTyped && !this.settings.excludeErrorFiles) {
                continue;
            }
            for (var file : workspace.getAllFiles()) {
                if (file.isTyped()) {
                    file.clearDiagnostics();
                }
            }
            if (workspace.getRoot().getContent() == null) {
                continue;
            }
            infoProvider.updateAll(workspace.getRoot().getContent().sourceTree());
        }

        publishDiagnostics();
    }

    public static Range regionToRange(Span region) {
        return new Range(
                new Position(region.start().line(), region.start().column()),
                new Position(region.end().line(), region.end().column())
        );
    }

}
