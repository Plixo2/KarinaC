package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.objects.KType;

public sealed interface CastTo {
    record CastToType(KType asType) implements CastTo {}
    record AutoCast() implements CastTo {}
}
