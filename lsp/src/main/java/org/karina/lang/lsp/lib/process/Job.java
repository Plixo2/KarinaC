package org.karina.lang.lsp.lib.process;


public interface Job<T> {
    void cancel();
    T awaitResult();
    boolean hasEnded();


    static <T> Job<T> of(T result) {
        return new Job<>() {
            @Override
            public void cancel() {
                // no-op
            }

            @Override
            public T awaitResult() {
                return result;
            }

            @Override
            public boolean hasEnded() {
                return true;
            }
        };
    }
}
