package org.karina.lang.lsp.events;

import java.util.concurrent.CompletableFuture;


public interface EventService {

    void update(UpdateEvent update);
    <T> CompletableFuture<T> request(RequestEvent<T> request);

    void send(ClientEvent clientEvent);
}
