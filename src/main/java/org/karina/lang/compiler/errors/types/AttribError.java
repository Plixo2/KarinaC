package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

import java.util.List;
import java.util.Set;

public sealed interface AttribError extends Error {

    record UnqualifiedSelf(Region region, Region method) implements AttribError {}

    record UnknownIdentifier(Region region, String name, Set<String> available) implements AttribError {}

    record DuplicateVariable(Region first, Region second, String name) implements AttribError {}

    record MissingField(Region region, String name) implements AttribError {}

    record UnknownField(Region region, String name) implements AttribError {}

    record FinalAssignment(Region region, String name) implements AttribError {}

    record ScopeFinalityAssignment(Region region, Region function, String name) implements AttribError {}

    record TypeMismatch(Region region, KType expected, KType found) implements AttribError {}

    record TypeCycle(Region region, String message, List<String> graph) implements AttribError {}

    record NotAStruct(Region region, KType type) implements AttribError { }

    record NotAArray(Region region, KType type) implements AttribError { }

    record NotAInterface(Region region, KType type) implements AttribError { }

    record NotSupportedType(Region region, KType type) implements AttribError { }

    record ParameterCountMismatch(Region region, int expected) implements AttribError {}

    record ControlFlow(Region region, String message) implements AttribError {}
}
