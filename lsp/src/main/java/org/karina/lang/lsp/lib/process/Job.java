package org.karina.lang.lsp.lib.process;

public interface Job {

    void cancel();
    boolean isDone();
}
