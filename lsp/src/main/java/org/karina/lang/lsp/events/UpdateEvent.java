package org.karina.lang.lsp.events;

import org.eclipse.lsp4j.Range;

import java.net.URI;

/// Event from the client to the server.
/// @see RequestEvent when the client expects a response.
public sealed interface UpdateEvent {

    // ------------------ Workspace Watched File Events ------------------
    record CreateWatchedFile(URI uri) implements UpdateEvent {};
    record DeleteWatchedFile(URI uri) implements UpdateEvent {};

    // ------------------ Workspace File System Events ------------------
    record CreateFile(URI uri) implements UpdateEvent {};
    record DeleteFile(URI uri) implements UpdateEvent {};
    record RenameFile(URI oldUri, URI newUri) implements UpdateEvent {};

    // ------------------ Text Document File System Events ------------------
    record OpenFile(URI uri, int version, String language, String text) implements UpdateEvent {};
    record CloseFile(URI uri) implements UpdateEvent {};
    record ChangeFile(URI uri, String text, Range range, int version) implements UpdateEvent {};
    record SaveFile(URI uri) implements UpdateEvent {};

}
