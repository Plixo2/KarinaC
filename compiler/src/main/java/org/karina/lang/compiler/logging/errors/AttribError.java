package org.karina.lang.compiler.logging.errors;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.BinaryOperator;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.RegionOf;

import java.util.List;
import java.util.Set;

public sealed interface AttribError extends Error {

    record UnqualifiedSelf(Region region, Region method) implements AttribError {}

    record UnknownIdentifier(Region region, String name, Set<String> available) implements AttribError {}

    record DuplicateVariable(Region first, Region second, String name) implements AttribError {}

    record UnknownCast(Region region) implements AttribError {}
    record InvalidNarrowingCast(Region region) implements AttribError {}

    record UnknownMember(Region region, String of, String name, Set<String> availableMethods, Set<String> availableFields, @Nullable String protectedPointer) implements AttribError {}

    record DuplicateInterface(Region region, KType.ClassType interfaceType) implements AttribError {}

    record FinalAssignment(Region region, Region regionOfFinalObject, String name) implements AttribError {}

    record ScopeFinalityAssignment(Region region, Region function, String name) implements AttribError {}

    record TypeMismatch(Region region, KType expected, KType found) implements AttribError {}

    record TypeCycle(Region region, String message, List<String> graph) implements AttribError {}

    record NotAClass(Region region, KType type) implements AttribError { }
    record NotAValidInterface(Region region, List<KType> args, KType returning, KType type) implements AttribError { }

    record NotAArray(Region region, KType type) implements AttribError { }

    record NotAInterface(Region region, KType type) implements AttribError { }

    record NotSupportedType(Region region, KType type) implements AttribError { }

    record NotSupportedExpression(Region region, String readable) implements AttribError { }

    record NotSupportedOperator(RegionOf<BinaryOperator> operator, KType left, KType right) implements AttribError { }

    record ParameterCountMismatch(Region region, int expected) implements AttribError {}

    record ControlFlow(Region region, String message) implements AttribError {}

    record SignatureMismatch(Region region, String name, List<KType> found, List<RegionOf<List<KType>>> available) implements AttribError {}

    record MissingConstructor(Region region, String object, List<String> names, List<RegionOf<List<String>>> available) implements AttribError {}
}
