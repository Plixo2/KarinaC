package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.logging.DidYouMean;
import org.karina.lang.compiler.logging.ErrorInformation;
import org.karina.lang.compiler.utils.BinaryOperator;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;

import java.util.List;
import java.util.Set;

public sealed interface AttribError extends Error {

    @Override
    default void addInformation(ErrorInformation builder) {
        switch (this) {
            case AttribError.NotAValidInterface notAValidInterface -> {
                builder.setTitle("Not a valid interface");
                builder.append("Type '").append(notAValidInterface.type())
                       .append("' is not a valid interface for the given arguments");
                builder.append(notAValidInterface.args().toString()).append(" -> ").append(notAValidInterface.returning());
                builder.setPrimarySource(notAValidInterface.region());
            }
            case AttribError.NotAClass notAClass -> {
                builder.setTitle("Not a class");
                builder.append("Type '").append(notAClass.type()).append("' is not a Class");
                builder.setPrimarySource(notAClass.region());
            }
            case AttribError.TypeCycle typeCycle -> {
                builder.setTitle("Type cycle");
                builder.append(typeCycle.message());
                builder.append("Graph: ");
                typeCycle.graph().forEach(builder::append);
                builder.setPrimarySource(typeCycle.region());
            }
            case AttribError.TypeMismatch typeMismatch -> {
                builder.setTitle("Type mismatch");
                builder.append("Expected: ").append(typeMismatch.expected());
                builder.append("Found: ").append(typeMismatch.found());
                builder.setPrimarySource(typeMismatch.region());
            }
            case AttribError.FinalAssignment finalAssignment -> {
                builder.setTitle("Invalid assignment");
                builder.append("Can't reassign final symbol '").append(finalAssignment.name()).append("'");
                builder.setPrimarySource(finalAssignment.region());
                builder.addSecondarySource(finalAssignment.regionOfFinalObject(), "Defined here");
            }
            case AttribError.ScopeFinalityAssignment scopeFinalityAssignment -> {
                builder.setTitle("Invalid assignment");
                builder.append("Cannot reassign a symbol used in a function expression'");
                builder.append("Variable '").append(scopeFinalityAssignment.name()).append("' is define outside the expression");
                builder.setPrimarySource(scopeFinalityAssignment.region());
            }
            case AttribError.DuplicateVariable duplicateVariable -> {
                builder.setTitle("Duplicate variable");
                builder.append("variable '").append(duplicateVariable.name()).append("' was already declared");
                builder.setPrimarySource(duplicateVariable.second());
                builder.addSecondarySource(duplicateVariable.first(), "Defined here");
            }
            case AttribError.UnknownIdentifier(var region, var name, var available) -> {
                builder.setTitle("Unknown identifier");
                builder.append("Unknown identifier '").append(name).append("'");
                var suggestions = DidYouMean.suggestions(available, name, 5);
                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Did you mean: ").append(quoted);
                }
                builder.setPrimarySource(region);
            }
            case AttribError.UnqualifiedSelf unqualifiedSelf -> {
                builder.setTitle("Unqualified self");
                builder.append("Invalid use of 'self' in a static context");
                builder.setPrimarySource(unqualifiedSelf.region());
                builder.addSecondarySource(unqualifiedSelf.method(), "in method");
            }
            case AttribError.NotAInterface notAInterface -> {
                builder.setTitle("Not a interface");
                builder.append("Type '").append(notAInterface.type()).append("' is not a interface");
                builder.setPrimarySource(notAInterface.region());
            }
            case AttribError.ControlFlow controlFlow -> {
                builder.setTitle("Control flow");
                builder.append(controlFlow.message());
                builder.setPrimarySource(controlFlow.region());
            }
            case AttribError.SignatureMismatch signatureMismatch -> {
                builder.setTitle("Signature mismatch");
                builder.append("Could not match signature of method '").append(signatureMismatch.name()).append("'");
                builder.append("Found Signature ").append(signatureMismatch.found());
                builder.append("");
                builder.append("Available Signatures:");
                if (signatureMismatch.available().isEmpty()) {
                    builder.append("None");
                }
                for (var available : signatureMismatch.available()) {
                    var toStr = available.value().toString();
                    builder.addSecondarySource(available.region(), toStr);
                }
                builder.setPrimarySource(signatureMismatch.region());
            }
            case AttribError.MissingConstructor missingConstructor -> {
                builder.setTitle("Constructor mismatch");
                builder.append("Could not find Constructor of '").append(missingConstructor.object()).append("'");
                builder.append("With parameters ").append(missingConstructor.names());
                builder.append("Available names:");

                if (missingConstructor.available().isEmpty()) {
                    builder.append("None");
                }
                for (var available : missingConstructor.available()) {
                    var toStr = available.value().toString();
                    builder.addSecondarySource(available.region(), toStr);
                }
                builder.setPrimarySource(missingConstructor.region());
            }
            case AttribError.UnknownCast unknownCast -> {
                builder.setTitle("Unknown cast");
                builder.append("Cannot infer type of cast, give the type explicitly");
                builder.setPrimarySource(unknownCast.region());
            }
            case AttribError.InvalidNarrowingCast invalidNarrowingCast -> {
                builder.setTitle("Invalid narrowing Cast");
                builder.append("Uncheck narrowing casts are not allowed");
                builder.setPrimarySource(invalidNarrowingCast.region());
            }
            case AttribError.UnknownMember unknownMember -> {
                builder.setTitle("Unknown member");
                builder.append("Unknown Member '").append(unknownMember.name()).append("'")
                       .append(" of ").append(unknownMember.of());

                var suggestionField = DidYouMean.suggestions(unknownMember.availableFields(), unknownMember.name(), 10);

                if (!suggestionField.isEmpty()) {
                    var quoted = suggestionField.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Available Fields: ").append(quoted);
                }

                var suggestionMethod = DidYouMean.suggestions(unknownMember.availableMethods(), unknownMember.name(), 10);

                if (!suggestionMethod.isEmpty()) {
                    var quoted = suggestionMethod.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Available Methods: ").append(quoted);
                }

                if (!unknownMember.protectedMembers().isEmpty()) {
                    builder.append("members of the name '").append(unknownMember.name())
                           .append("' exists, but are not accessible here: ");
                }
                for (var protectedMember : unknownMember.protectedMembers()) {
                    builder.addSecondarySource(protectedMember.region(), protectedMember.value());
                }

                builder.setPrimarySource(unknownMember.region());
            }
            case AttribError.DuplicateInterface duplicateInterface -> {
                builder.setTitle("Duplicate interface");
                builder.append("Duplicate Interface '").append(duplicateInterface.interfaceType()).append("'");
                builder.setPrimarySource(duplicateInterface.region());
            }
            case AttribError.NotSupportedType notSupportedType -> {
                builder.setTitle("Unsupported type");
                builder.append("Type '").append(notSupportedType.type())
                       .append("' is not supported here");
                builder.setPrimarySource(notSupportedType.region());
            }
            case AttribError.NotSupportedExpression notSupportedExpression -> {
                builder.setTitle("Unsupported use of expression");
                builder.append(notSupportedExpression.readable());
                builder.setPrimarySource(notSupportedExpression.region());
            }
            case AttribError.NotSupportedOperator notSupportedOperator -> {
                builder.setTitle("Unsupported operator");
                builder.append("Operator '").append(notSupportedOperator.operator().value())
                       .append("' cannot be applied to '").append(notSupportedOperator.left()).append("'")
                       .append(" and '").append(notSupportedOperator.right()).append("'");
                builder.setPrimarySource(notSupportedOperator.operator().region());
            }
            case AttribError.NotAArray notAArray -> {
                builder.setTitle("Not a array");
                builder.append("Type '").append(notAArray.type()).append("' is not a array");
                builder.setPrimarySource(notAArray.region());
            }
            case AttribError.ParameterCountMismatch parameterCountMismatch -> {
                builder.setTitle("Parameter count mismatch");
                builder.append("Expected ").append(parameterCountMismatch.expected()).append(" parameters");
                builder.setPrimarySource(parameterCountMismatch.region());
            }
        }
    }

    record UnqualifiedSelf(Region region, Region method) implements AttribError {}

    record UnknownIdentifier(Region region, String name, Set<String> available) implements AttribError {}

    record DuplicateVariable(Region first, Region second, String name) implements AttribError {}

    record UnknownCast(Region region) implements AttribError {}

    record InvalidNarrowingCast(Region region) implements AttribError {}

    record UnknownMember(Region region, String of, String name, Set<String> availableMethods, Set<String> availableFields, List<RegionOf<String>> protectedMembers) implements AttribError {}

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
