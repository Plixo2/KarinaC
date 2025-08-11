package org.karina.lang.lsp.events;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Registration;

import java.net.URI;
import java.util.List;

/// Event that the servers sends to the client
public sealed interface ClientEvent {
    record Log(String message, MessageType type) implements ClientEvent {}
    record Popup(String message, MessageType type) implements ClientEvent {}

    record RegisterCapability(Registration... registration) implements ClientEvent {}
    record PublishDiagnostic(URI uri, List<Diagnostic> diagnostics) implements ClientEvent {}

}
