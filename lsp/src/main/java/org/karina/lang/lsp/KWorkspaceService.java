package org.karina.lang.lsp;

import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.karina.lang.lsp.events.UpdateEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.lib.VirtualFileSystem;

@RequiredArgsConstructor
public final class KWorkspaceService implements WorkspaceService {
    private final EventService eventService;

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

            switch (changedFile.getType()) {
                case Changed -> {
                    // should get handled by the client and send via textDocument/didChange
                }
                case Created -> {
                    this.eventService.update(new UpdateEvent.CreateWatchedFile(uri));
                }
                case Deleted -> {
                    this.eventService.update(new UpdateEvent.DeleteWatchedFile(uri));
                }
            }
        }
    }


    @Override
    public void didCreateFiles(CreateFilesParams params) {
        var uris = params.getFiles().stream().map(FileCreate::getUri).toList();
        for (var uriStr : uris) {
            var uri = VirtualFileSystem.toUri(uriStr);
            this.eventService.update(new UpdateEvent.CreateFile(uri));
        }
    }
    @Override
    public void didDeleteFiles(DeleteFilesParams params) {
        var uris = params.getFiles().stream().map(FileDelete::getUri).toList();
        for (var uriStr : uris) {
            var uri = VirtualFileSystem.toUri(uriStr);
            this.eventService.update(new UpdateEvent.DeleteFile(uri));
        }
    }

    @Override
    public void didRenameFiles(RenameFilesParams params) {
        for (var file : params.getFiles()) {
            var oldUri = VirtualFileSystem.toUri(file.getOldUri());
            var newUri = VirtualFileSystem.toUri(file.getNewUri());
            this.eventService.update(new UpdateEvent.RenameFile(oldUri, newUri));
        }

    }
}
