package org.karina.lang.lsp.lib.process;

public interface JobProgress {

    boolean isCancelled();
    void notify(String message, int percentage);

}
