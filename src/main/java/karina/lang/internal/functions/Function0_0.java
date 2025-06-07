package karina.lang.internal.functions;


public interface Function0_0 extends Runnable {
    void apply();

    @Override
    default void run() {
        apply();
    }
}
