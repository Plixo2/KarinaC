package org.karina.lang.lsp;

import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.karina.lang.lsp.lib.VirtualFileSystem;

@RequiredArgsConstructor
public final class KWorkspaceService implements WorkspaceService {
    private final KarinaLanguageServer kls;

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {

    }
    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        if (!KarinaLanguageServer.USE_FILESYSTEM_EVENTS) {
            return;
        }
        for (var changedFile : params.getChanges()) {
            var uri = VirtualFileSystem.toUri(changedFile.getUri());
            if (this.kls.vfs.isOpen(uri)) {
                continue;
            }

            switch (changedFile.getType()) {
                case Created, Changed -> {
                    var content = this.kls.rfs.readFileFromDisk(uri).orMessageAndNull(this.kls);
                    if (content == null) {
                        continue;
                    }
                    this.kls.handleTransaction(this.kls.vfs.reloadFromDisk(uri, content));
                }
                case Deleted -> {
                    this.kls.handleTransaction(this.kls.vfs.deleteFile(uri));
                }
            }
        }
    }


    @Override
    public void didCreateFiles(CreateFilesParams params) {
        var uris = params.getFiles().stream().map(FileCreate::getUri).toList();
        for (var uriStr : uris) {
            var uri = VirtualFileSystem.toUri(uriStr);
            if (!this.kls.vfs.isOpen(uri)) {
                var content = this.kls.rfs.readFileFromDisk(uri).orMessageAndNull(this.kls);
                if (content == null) {
                    continue;
                }
                this.kls.handleTransaction(this.kls.vfs.reloadFromDisk(uri, content));
            }
        }
    }
    @Override
    public void didDeleteFiles(DeleteFilesParams params) {
        var uris = params.getFiles().stream().map(FileDelete::getUri).toList();
        for (var uriStr : uris) {
            var uri = VirtualFileSystem.toUri(uriStr);
            if (!this.kls.vfs.isOpen(uri)) {
                this.kls.handleTransaction(this.kls.vfs.deleteFile(uri));
            }
        }
    }
    @Override
    public void didRenameFiles(RenameFilesParams params) {
        for (var file : params.getFiles()) {
            var oldUri = VirtualFileSystem.toUri(file.getOldUri());
            var newUri = VirtualFileSystem.toUri(file.getNewUri());
            if (!this.kls.vfs.isOpen(oldUri)) {
                this.kls.handleTransaction(this.kls.vfs.deleteFile(oldUri));
                var content = this.kls.rfs.readFileFromDisk(newUri).orMessageAndNull(this.kls);
                if (content == null) {
                    continue;
                }
                this.kls.handleTransaction(this.kls.vfs.reloadFromDisk(newUri, content));
            }
        }

    }
}
