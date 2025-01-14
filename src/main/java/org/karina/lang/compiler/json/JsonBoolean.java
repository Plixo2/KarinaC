package org.karina.lang.compiler.json;

import org.karina.lang.compiler.utils.Span;

public record JsonBoolean(Span region, boolean value) implements JsonPrimitive {

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
