package org.karina.lang.lsp.events;

import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Registration;

/// Event that the servers sends to the client
public sealed interface ClientEvent {
    record Log(String message, MessageType type) implements ClientEvent {}
    record Popup(String message, MessageType type) implements ClientEvent {}

    record RegisterCapability(Registration... registration) implements ClientEvent {}

}
