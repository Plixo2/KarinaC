package org.karina.lang.lsp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.stages.parser.KarinaUnitParser;
import org.karina.lang.lsp.fs.ContentRoot;
import org.karina.lang.lsp.fs.KarinaFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a workspace in the language server.
 * This keeps a virtual file system and provides methods to interact and manipulate it.
 * Also keeps track of the configuration of the workspace.
 * Both are kept in the {@link ContentRoot} class.
 */
@AllArgsConstructor
@Getter
public class Workspace {
    private final ContentRoot root;

    public boolean doesWorkspaceContain(Path path) {
        return path.startsWith(this.root.getAbsolutFSPath());
    }

    public boolean deleteFile(Path path) {
        path = path.normalize();
        if (this.root.getContent() == null) {
            return false;
        }
        var content = this.root.getContent();
        var absolutPathToSource = this.root.getAbsolutFSPath().resolve(content.configFile().rootToSourceDirectory).normalize();
        var fileName = path.getFileName().toString();
        if (!fileName.endsWith(".krna")) {
            return false;
        }
        //test if the file is in the source directory
        if (!path.startsWith(absolutPathToSource)) {
            return false;
        }
        var relativeToSrc = absolutPathToSource.relativize(path);
        var pathToDelete = toObjectPath(content.configFile().sourceDirectoryName, relativeToSrc);
        return content.sourceTree().delete(pathToDelete);
    }

    public @Nullable FileLoading.FileLoadResult<KarinaFile> getOrLoadFile(Path path) {
        var virtualFile = getKarinaFile(path);
        if (virtualFile == null) {
            return null;
        }

        if (virtualFile.state() == null) {
            var resultingContent = FileLoading.load(virtualFile, virtualFile.uriPath());
            if (resultingContent instanceof FileLoading.FileLoadResult.Success(var str)) {
                virtualFile.state(new KarinaFile.KarinaFileState.Raw(str));
                elevateFile(virtualFile);
                return new FileLoading.FileLoadResult.Success<>(virtualFile);
            } else {
                //resultingContent is never mapped, so null is valid
                return resultingContent.map((ref) -> null);
            }
        } else {
            return new FileLoading.FileLoadResult.Success<>(virtualFile);
        }
    }

    public void updateFile(Path uri, String text) {
        var virtualFile = getKarinaFile(uri);
        if (virtualFile == null) {
            return;
        }
        var textSource = new TextSource(
                virtualFile,
                text.lines().toList()
        );
        virtualFile.state(new KarinaFile.KarinaFileState.Raw(textSource));
        elevateFile(virtualFile);
    }

    public @Nullable FileLoading.FileLoadResult<?> updateFile(Path uri) {
        var virtualFile = getKarinaFile(uri);
        if (virtualFile == null) {
            return null;
        }
        var resultingContent = FileLoading.load(virtualFile, virtualFile.uriPath());
        if (resultingContent instanceof FileLoading.FileLoadResult.Success(var str)) {
            virtualFile.state(new KarinaFile.KarinaFileState.Raw(str));
            elevateFile(virtualFile);
            return null;
        } else {
            return resultingContent;
        }
    }

    private void elevateFile(KarinaFile file) {
        var simpleName = file.name();
        var path = file.path();
        if (file.state() instanceof KarinaFile.KarinaFileState.Raw(var source)) {
            var error = ErrorHandler.tryInternal(() -> {
                var unit = KarinaUnitParser.generateParseTree(source, simpleName, path);
                if (unit != null) {
                    file.state(new KarinaFile.KarinaFileState.Typed(source, unit));
                }
            });
            if (error == null) {
                file.clearDiagnostics();
            } else {
                error.pushErrorsToFile();
            }
        }
    }

    public @Nullable FileLoading.FileLoadResult<?> loadAllFiles() {
        if (this.root.getContent() == null) {
            return null;
        }
        var content = this.root.getContent();
        var absolutPathToSource = this.root.getAbsolutFSPath().resolve(content.configFile().rootToSourceDirectory);
        var loadedFiles = FileLoading.getAllKarinaFiles(absolutPathToSource);
        if (loadedFiles instanceof FileLoading.FileLoadResult.Success(var paths)) {
            for (var path : paths) {
                updateFile(path);
            }
            return null;
        } else {
            return loadedFiles;
        }
    }

    public ObjectPath toObjectPath(String prefix, Path path) {
        List<String> components = new ArrayList<>();
        components.add(prefix);

        for (int i = 0; i < path.getNameCount(); i++) {
            String part = path.getName(i).toString();
            if (i == path.getNameCount() - 1) {
                int dotIndex = part.lastIndexOf('.');
                if (dotIndex > 0) {
                    part = part.substring(0, dotIndex);
                }
            }
            components.add(part);
        }

        return new ObjectPath(components);
    }

    private @Nullable KarinaFile getKarinaFile(Path path) {
        if (!(validate(path) instanceof ValidPath(var content, var absolutPathToSource))) {
            return null;
        }
        var relativeToSrc = absolutPathToSource.relativize(path);
        var pathToInsert = toObjectPath(content.configFile().sourceDirectoryName, relativeToSrc);
        return content.sourceTree().createOrGet(path, pathToInsert);
    }

    private @Nullable ValidPath validate(Path path) {
        path = path.normalize();
        if (this.root.getContent() == null) {
            return null;
        }
        var content = this.root.getContent();
        var absolutPathToSource = this.root.getAbsolutFSPath().resolve(content.configFile().rootToSourceDirectory).normalize();
        if (!Files.exists(absolutPathToSource) || !Files.isDirectory(absolutPathToSource)) {
            return null;
        }
        if (!Files.isRegularFile(path)) {
            return null;
        }
        var fileName = path.getFileName().toString();
        if (!fileName.endsWith(".krna")) {
            return null;
        }

        //test if the file is in the source directory
        if (!path.startsWith(absolutPathToSource)) {
            return null;
        }

        return new ValidPath(content, absolutPathToSource);
    }

    record ValidPath( ContentRoot.InitializedContentRoot content, Path absolutToSource) {}

    public List<KarinaFile> getAllFiles() {
        if (this.root.getContent() == null) {
            return new ArrayList<>();
        }
        return this.root.getContent().sourceTree().getAllLeafsRecursive();
    }

    public String toPrettyTreeString() {
        if (this.root.getContent() == null) {
            return "";
        }
        return this.root.getContent().sourceTree().toPrettyString();
    }

    public boolean isFullyTyped() {
        if (this.root.getContent() == null) {
            return false;
        }
        for (var virtualFile : getAllFiles()) {
            if (virtualFile.state() instanceof KarinaFile.KarinaFileState.Raw) {
                return false;
            }
        }
        return true;
    }

}
