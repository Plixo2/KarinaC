package karina.lang.internal.functions;

import java.util.function.BiConsumer;

public interface Function2_0<P1, P2> extends BiConsumer<P1, P2> {
    void apply(P1 p1, P2 p2);

    @Override
    default void accept(P1 p1, P2 p2) {
        apply(p1, p2);
    }
}
