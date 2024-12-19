package org.karina.lang.compiler.json;

public sealed interface JsonPrimitive extends JsonElement
        permits JsonBoolean, JsonNull, JsonNumber, JsonString {}
