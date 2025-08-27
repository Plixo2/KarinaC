package org.karina.lang.lsp.lib.events;

import karina.lang.ThrowableFunction;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;


public interface EventClientService {
    void send(ClientEvent clientEvent);

    void sendTerminal(String message);
    void clearTerminal();

    Job createJob(String title, ThrowableFunction<JobProgress, String, Exception> process);
}
