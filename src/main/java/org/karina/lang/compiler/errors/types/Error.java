package org.karina.lang.compiler.errors.types;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;

public sealed interface Error
        permits Error.InvalidState, Error.SyntaxError, Error.TemporaryErrorRegion, FileLoadError,
        ImportError, LinkError
{


    record TemporaryErrorRegion(Span region, String message) implements Error {}

    record SyntaxError(Span region, String msg) implements Error {}

    record InvalidState(Span region, Class<?> aClass, String expectedState) implements Error {}

}
