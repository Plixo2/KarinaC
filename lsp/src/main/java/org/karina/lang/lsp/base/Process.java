package org.karina.lang.lsp.base;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.lsp4j.ProgressParams;
import org.eclipse.lsp4j.WorkDoneProgressEnd;
import org.eclipse.lsp4j.WorkDoneProgressReport;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Future;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class Process {
    String token;
    EventLanguageServer server;
    Future<Void> future;

    private int progress = 0;

    public void cancel() {
        this.future.cancel(true);
        if (this.server.processes.containsKey(this.token)) {
            this.server.processes.remove(this.token);

            var end = new WorkDoneProgressEnd();
            end.setMessage("Cancelled");
            var endParams = new ProgressParams(Either.forLeft(this.token), Either.forLeft(end));
            this.server.client.notifyProgress(endParams);
        }

    }

    public boolean isDone() {
        return this.future.isDone();
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public class Progress {

        public boolean isCancelled() {
            return Process.this.isDone();
        }

        public void notify(String message, int percentage) {
            Process.this.progress = Math.max(0, Math.min(100, percentage));
            update(message);
        }

        public void notify(String message) {
            update(message);
        }

        public void notify(int percentage) {
            Process.this.progress = Math.max(0, Math.min(100, percentage));
            update(null);
        }

//        public void notify(String message, int percentage, boolean cancellable) {
//            Process.this.cancellable = cancellable;
//            Process.this.progress = Math.max(0, Math.min(100, percentage));
//            update(message);
//        }
//
//        public void notify(String message, boolean cancellable) {
//            Process.this.cancellable = cancellable;
//            update(message);
//        }
//
//        public void notify(int percentage, boolean cancellable) {
//            Process.this.cancellable = cancellable;
//            Process.this.progress = Math.max(0, Math.min(100, percentage));
//            update(null);
//        }
//        public void notify(boolean cancellable) {
//            Process.this.cancellable = cancellable;
//            update(null);
//        }

        private void update(@Nullable String message) {
            var report = new WorkDoneProgressReport();
//            report.setCancellable(Process.this.cancellable);
            report.setCancellable(true);
            report.setPercentage(Process.this.progress);
            report.setMessage(message);
            var progressParams = new ProgressParams(Either.forLeft(Process.this.token), Either.forLeft(report));
            Process.this.server.client.notifyProgress(progressParams);
        }
    }

}
