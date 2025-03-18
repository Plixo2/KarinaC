package karina.lang.functions;

import java.util.function.BiFunction;

public interface Function2_1<P1, P2, R> extends BiFunction<P1, P2, R> {
    R apply(P1 p1, P2 p2);
}
