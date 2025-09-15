package org.karina.lang.lsp.base;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class DefaultProcess implements Job {
    public @Nullable BooleanSupplier onKill;
    public String title;
    String token;
    EventLanguageServer server;
    Future<Void> future;

    private int progress = 0;

    @Override
    public void cancel() {
//        this.server.send(new ClientEvent.Log("terminating process '" + this.title + "'", MessageType.Warning));
        if (this.onKill != null) {
            var killed = this.onKill.getAsBoolean();
            if (killed) {
//                this.server.send(new ClientEvent.Popup("terminated process '" + this.title + "'", MessageType.Warning));
                this.future.cancel(true);
//                this.server.send(new ClientEvent.Log("terminated process really '" + this.title + "'", MessageType.Warning));
                if (this.server.processes.containsKey(this.token)) {
                    this.server.processes.remove(this.token);
                    var end = new WorkDoneProgressEnd();
                    end.setMessage("Cancelled");
                    var endParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(end));
                    this.server.client.notifyProgress(endParams);
                }
            } else {
                this.server.send(new ClientEvent.Popup("Could not terminate process '" + this.title + "'", MessageType.Error));
                this.future.cancel(true);
            }
        } else {
            this.future.cancel(true);
//            this.server.send(new ClientEvent.Popup("terminated process '" + this.title + "'", MessageType.Warning));
            if (this.server.processes.containsKey(this.token)) {
                this.server.processes.remove(this.token);

                var end = new WorkDoneProgressEnd();
                end.setMessage("Cancelled");
                var endParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(end));
                this.server.client.notifyProgress(endParams);

            }
        }

    }

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

    public class DefaultWorkProgress implements JobProgress {

        @Override
        public boolean isCancelled() {
            return DefaultProcess.this.isDone();
        }

        @Override
        public void notify(String message, int percentage) {
            DefaultProcess.this.progress = Math.max(0, Math.min(100, percentage));
            update(message);
        }

        @Override
        public void notify(String message) {
            update(message);
        }

        @Override
        public void notify(int percentage) {
            DefaultProcess.this.progress = Math.max(0, Math.min(100, percentage));
            update(null);
        }

        private void update(@Nullable String message) {
            var report = new WorkDoneProgressReport();
//            report.setCancellable(Process.this.cancellable);
            report.setCancellable(true);
            report.setPercentage(DefaultProcess.this.progress);
            report.setMessage(message);
            var progressParams = new ProgressParams(Either.forLeft(DefaultProcess.this.token), Either.forLeft(report));
            DefaultProcess.this.server.client.notifyProgress(progressParams);
        }
    }

}
