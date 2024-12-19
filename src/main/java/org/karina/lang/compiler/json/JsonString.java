package org.karina.lang.compiler.json;

import org.karina.lang.compiler.Span;

public record JsonString(Span region, String value) implements JsonPrimitive {

    @Override
    public String toString() {
        return "\"" + this.value + "\"";
    }
}
