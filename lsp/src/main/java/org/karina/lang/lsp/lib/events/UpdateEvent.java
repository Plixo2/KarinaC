package org.karina.lang.lsp.lib.events;


import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.impl.ClientConfiguration;

import java.net.URI;

/// Event from the client to the server.
/// @see RequestEvent when the client expects a response.
public sealed interface UpdateEvent {


    // ------------------ Workspace Client Config Change ------------------
    record UpdateClientConfig(ClientConfiguration configuration) implements UpdateEvent {};


    sealed interface UpdateFileEvent extends UpdateEvent {
        URI uri();;
        // ------------------ Workspace Watched File Events ------------------
        record CreateWatchedFile(URI uri) implements UpdateFileEvent {};
        record DeleteWatchedFile(URI uri) implements UpdateFileEvent {};

        // ------------------ Workspace File System Events ------------------
        record CreateFile(URI uri) implements UpdateFileEvent {};
        record DeleteFile(URI uri) implements UpdateFileEvent {};
        record RenameFile(URI oldUri, URI uri) implements UpdateFileEvent {};

        // ------------------ Text Document File System Events ------------------
        record OpenFile(URI uri, int version, String language, String text) implements UpdateFileEvent {};
        record CloseFile(URI uri) implements UpdateFileEvent {};
        record ChangeFile(URI uri, String text, @Nullable Range range, int version) implements UpdateFileEvent {};
        record SaveFile(URI uri) implements UpdateFileEvent {};
    }

}
