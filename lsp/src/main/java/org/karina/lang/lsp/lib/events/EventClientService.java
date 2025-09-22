package org.karina.lang.lsp.lib.events;

import org.eclipse.lsp4j.MessageType;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.util.Objects;
import java.util.function.Function;


public interface EventClientService {
    void send(ClientEvent clientEvent);

    <T> Job<T> createJob(String title, Function<JobProgress, T> process);


    default void warningMessage(Object object) {
        var value = Objects.toString(object);
        this.send(new ClientEvent.Log(value, MessageType.Warning));
        this.send(new ClientEvent.Popup(value, MessageType.Warning));
    }

    default void timing(String type, String file, long start) {
        var duration = System.currentTimeMillis() - start;
        System.out.println(type + " " + file + " " + duration + "ms");
    }
    default void timing(String type, String file, String state, long start) {
        var duration = System.currentTimeMillis() - start;
        System.out.println(type + " " + file + ": " + state + " " + duration + "ms");
    }
}
