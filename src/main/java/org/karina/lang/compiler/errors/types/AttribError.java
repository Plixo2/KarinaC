package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.Span;

public sealed interface AttribError extends Error {

    record UnqualifiedSelf(Span region, Span method) implements AttribError {}

    record UnknownIdentifier(Span region, String name) implements AttribError {}

    record DuplicateVariable(Span first, Span second, String name) implements AttribError {}

    record FinalAssignment(Span region, String name) implements AttribError {}

    record ScopeFinalityAssignment(Span region, Span function, String name) implements AttribError {}
}
