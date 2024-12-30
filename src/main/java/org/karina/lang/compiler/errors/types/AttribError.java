package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KType;

import java.util.List;
import java.util.Set;

public sealed interface AttribError extends Error {

    record UnqualifiedSelf(Span region, Span method) implements AttribError {}

    record UnknownIdentifier(Span region, String name, Set<String> available) implements AttribError {}

    record DuplicateVariable(Span first, Span second, String name) implements AttribError {}

    record MissingField(Span region, String name) implements AttribError {}

    record UnknownField(Span region, String name) implements AttribError {}

    record FinalAssignment(Span region, String name) implements AttribError {}

    record ScopeFinalityAssignment(Span region, Span function, String name) implements AttribError {}

    record TypeMismatch(Span region, KType expected, KType found) implements AttribError {}

    record TypeCycle(Span region, String message) implements AttribError {}

    record NotAStruct(Span region, KType type) implements AttribError {
    }

    record NotAInterface(Span region, KType type) implements AttribError {
    }
}
