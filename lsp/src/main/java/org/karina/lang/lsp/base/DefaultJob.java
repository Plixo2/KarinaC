package org.karina.lang.lsp.base;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class DefaultJob<T> implements Job<T> {
    public String title;
    String token;
    EventLanguageServer server;
    CompletableFuture<T> future;

    boolean canceled = false;

    boolean ended = false;

    @Override
    public void cancel() {
        this.canceled = true;
        if (this.server.processes.containsKey(this.token)) {
            this.server.processes.remove(this.token);

            var end = new WorkDoneProgressEnd();
            end.setMessage("Cancelled");
            var endParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(end));
            this.server.client.notifyProgress(endParams);
        }
    }

    @Override
    public T awaitResult() {
        return this.future.join();
    }

    @Override
    public boolean hasEnded() {
        return this.ended || this.canceled;
    }


    public void begin() {
        var progressBegin = new WorkDoneProgressBegin();
        progressBegin.setTitle(this.title);
        progressBegin.setCancellable(true);
        progressBegin.setPercentage(0);
        var progressParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(progressBegin));
        this.server.client.notifyProgress(progressParams);
    }

    public void end(String message) {
        this.ended = true;
        var end = new WorkDoneProgressEnd();
        end.setMessage(message);
        var endParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(end));
        this.server.client.notifyProgress(endParams);
    }

    public void progress(String message, int percentage) {
        var report = new WorkDoneProgressReport();
        report.setCancellable(true);
        report.setPercentage(Math.max(0, Math.min(100, percentage)));
        report.setMessage(message);
        var progressParams = new ProgressParams(
                Either.forLeft(DefaultJob.this.token),
                Either.forLeft(report)
        );
        this.server.client.notifyProgress(progressParams);
    }

    public class DefaultWorkProgress implements JobProgress {

        @Override
        public boolean isCancelled() {
            return DefaultJob.this.canceled;
        }

        @Override
        public void notify(String message, int percentage) {
            if (isCancelled()) {
                return;
            }
            DefaultJob.this.progress(message, percentage);
        }


    }

}
