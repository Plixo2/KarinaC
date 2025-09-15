package org.karina.lang.lsp.lib.process;

public interface JobProgress {

    boolean isCancelled();
    void notify(String message, int percentage);
    void notify(String message);
    void notify(int percentage);

}
