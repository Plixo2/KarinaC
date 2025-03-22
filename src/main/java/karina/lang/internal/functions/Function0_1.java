package karina.lang.internal.functions;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface Function0_1<R> extends Supplier<R>, Callable<R> {
    R apply();

    @Override
    default R get() {
        return apply();
    }

    @Override
    default R call() {
        return apply();
    }
}
