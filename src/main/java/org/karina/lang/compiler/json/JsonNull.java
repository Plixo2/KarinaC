package org.karina.lang.compiler.json;

import org.karina.lang.compiler.Span;

public record JsonNull(Span region) implements JsonPrimitive {

    @Override
    public String toString() {
        return "null";
    }

}
