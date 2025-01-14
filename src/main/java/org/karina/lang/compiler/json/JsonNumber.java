package org.karina.lang.compiler.json;

import org.karina.lang.compiler.utils.Span;

import java.math.BigDecimal;

public record JsonNumber(Span region, BigDecimal value) implements JsonPrimitive {

    @Override
    public String toString() {
        return this.value.toString();
    }

}
