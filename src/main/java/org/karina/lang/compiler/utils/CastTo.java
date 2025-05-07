package org.karina.lang.compiler.utils;

public sealed interface CastTo {
    record CastToType(KType asType) implements CastTo {}
    record AutoCast() implements CastTo {}
}
