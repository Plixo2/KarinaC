package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.Span;

public sealed interface LinkError extends Error{

    record UnqualifiedSelf(Span region, Span method) implements LinkError {}

    record UnknownIdentifier(Span region, String name) implements LinkError {}

    record DuplicateVariable(Span first, Span second, String name) implements LinkError {}

    record FinalAssignment(Span region, String name) implements LinkError {}

    record ScopeFinalityAssignment(Span region, Span function, String name) implements LinkError {}
}
