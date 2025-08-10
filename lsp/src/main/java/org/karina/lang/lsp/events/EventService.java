package org.karina.lang.lsp.events;

import karina.lang.Option;
import karina.lang.Result;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.ClientInfo;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerInfo;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.base.EventClientService;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public interface EventService extends EventClientService {
    void onInit(EventClientService theClient, ClientCapabilities capabilities, Option<ClientInfo> clientInfo, URI clientRoot);

    ServerInfo serverInfo();

    void update(UpdateEvent update);
    <T> CompletableFuture<T> request(RequestEvent<T> request);


    default <T> @Nullable T unwrapOrMessageAndNull(Result<T, ?> result) {
        return result.okOrElse(e -> {
            var value = Objects.toString(e);
            this.send(new ClientEvent.Log(value, MessageType.Error));
            this.send(new ClientEvent.Popup(value, MessageType.Error));
            return null;
        });
    }
}
