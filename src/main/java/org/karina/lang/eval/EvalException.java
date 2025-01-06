package org.karina.lang.eval;

import lombok.Getter;
import lombok.experimental.Accessors;

public class EvalException extends RuntimeException {
    @Getter
    @Accessors(fluent = true)
    private Object object;

    public EvalException(Object object) {
        super(object.toString());
    }
}
