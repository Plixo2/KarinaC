package org.karina.lang.lsp.base;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import karina.lang.Option;
import karina.lang.Result;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.karina.lang.lsp.KarinaLSP;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.UpdateEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.lib.ClientConfiguration;
import org.karina.lang.lsp.lib.VirtualFileSystem;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class EventWorkspaceService implements WorkspaceService {
    private final EventService eventService;

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        var configuration = Option.<Result<ClientConfiguration, IOException>>none();
        if (params.getSettings() instanceof JsonObject object) {
            configuration = Option.some(
                ClientConfiguration.fromJson(object)
            );
        } else if (params.getSettings() instanceof String json) {
            configuration = Option.some(
                    ClientConfiguration.fromJson(json)
            );
        }
        if (configuration instanceof Option.Some(var result)) {
            var unwrapped = this.eventService.unwrapOrMessageAndNull(result);
            if (unwrapped != null) {
                this.eventService.update(new UpdateEvent.UpdateClientConfig(unwrapped));
            }
        }


    }
    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        if (!KarinaLSP.USE_FILESYSTEM_EVENTS) {
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

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        var command = params.getCommand();
        var args = Option.fromNullable(params.getArguments()).orElse(List.of());
        this.eventService.update(new UpdateEvent.ExecuteCommand(command, args));
        return CompletableFuture.completedFuture(null);
    }
}
