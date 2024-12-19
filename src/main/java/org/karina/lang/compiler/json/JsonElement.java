package org.karina.lang.compiler.json;

import org.karina.lang.compiler.Span;

public sealed interface JsonElement permits JsonArray, JsonObject, JsonPrimitive {
    Span region();
}
