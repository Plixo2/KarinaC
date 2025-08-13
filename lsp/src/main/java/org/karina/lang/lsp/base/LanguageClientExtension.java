package org.karina.lang.lsp.base;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

public interface LanguageClientExtension extends LanguageClient {
    @JsonNotification("karina/sendToTerminal")
    void sendToTerminal(String params);
}
