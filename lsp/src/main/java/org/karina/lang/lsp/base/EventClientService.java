package org.karina.lang.lsp.base;

import org.karina.lang.lsp.events.ClientEvent;


public interface EventClientService {
    void send(ClientEvent clientEvent);

    void sendTerminal(String message);

    Process createProgress(String title, Process.ThrowingFunction<Process.Progress, String> process);
}
