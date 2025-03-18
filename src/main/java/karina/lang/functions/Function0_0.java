package karina.lang.functions;

import java.util.concurrent.Callable;

public interface Function0_0 extends Runnable {
    void apply();

    @Override
    default void run() {
        apply();
    }
}
