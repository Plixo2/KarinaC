package org.karina.lang.interpreter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Flow extends Throwable {
    public final Type type;
    private final Object value;

    public enum Type {
        BREAK,
        CONTINUE,
        RETURN
    }
}
