package karina.lang.functions;

import java.util.function.Consumer;

public interface Function1_0<P1> extends Consumer<P1> {
    void apply(P1 p1);

    @Override
    default void accept(P1 p1) {
        apply(p1);
    }
}
