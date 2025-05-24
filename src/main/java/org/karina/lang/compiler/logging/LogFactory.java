package org.karina.lang.compiler.logging;

import org.karina.lang.compiler.logging.errors.*;
import org.karina.lang.compiler.logging.errors.Error;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.List;

public class LogFactory<T extends LogBuilder> {
    private final List<T> logs = new ArrayList<>();

    public T populate(Error log, T builder) {
        this.logs.add(builder);
        switch (log) {
            case FileLoadError error -> {
                var fileError = builder.setTitle("File Handling");
                if (error.file() != null) {
                    var path = error.file().getAbsolutePath().replace("\\", "/");
                    fileError.append("File: file:///").append(path);
                }
                fileError.append("Type: ").append(error.errorMessage());
            }
            case Error.InvalidState(var region, var aClass, var expectedState) -> {
                var invalidState = builder.setTitle("Invalid State");
                invalidState.append("Expected state: ").append(expectedState);
                invalidState.append("Class: ").append(aClass.getSimpleName());
                invalidState.setPrimarySource(region);
            }
            case Error.ParseError(var msg) -> {
                var parseError = builder.setTitle("Parse Error");
                parseError.append(msg);
            }
            case Error.SyntaxError(var region, var msg) -> {
                var syntaxError = builder.setTitle("Syntax Error");
                syntaxError.append(msg);
                syntaxError.setPrimarySource(region);
            }
            case Error.BytecodeLoading(var resource, var location, var msg) -> {
                var fileError = builder.setTitle("Bytecode Handling");
                fileError.append("File: ").append(resource.identifier());
                fileError.append("symbol: " + location);
                fileError.append(msg);
            }
            case Error.TemporaryErrorRegion(var region, var message) -> {
                var tempError = builder.setTitle(message);
                tempError.setPrimarySource(region);
            }
            case Error.Warn(var message) -> {
                builder.setTitle(message);
            }
            case Error.InternalException(var exception) -> {
                var internalError = builder.setTitle("Internal Exception");
                internalError.append("oh no, please report this issue\n");
                if (exception.getMessage() == null) {
                    internalError.append("<no message given>");
                } else {
                    internalError.append(exception.getMessage());
                }
                for (var stackTraceElement : exception.getStackTrace()) {
                    internalError.append(stackTraceElement.toString());
                }
            }
            case ImportError errorType -> {
                createImportError(errorType, builder);
            }
            case AttribError errorType -> {
                createAttribError(errorType, builder);
            }
            case LowerError lowerError -> {
                createLowerError(lowerError, builder);
            }
        }
        return builder;

    }

    private void createLowerError(LowerError errorType, LogBuilder builder) {
        switch (errorType) {
            case LowerError.NotValidAnymore notValidAnymore -> {
                builder.setTitle("Cannot be Expressed");
                builder.append(notValidAnymore.message());
                builder.setPrimarySource(notValidAnymore.region());
            }
        }
    }

    private void createAttribError(AttribError errorType, LogBuilder builder) {

        switch (errorType) {
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
                builder.addSecondarySource(finalAssignment.regionOfFinalObject(), "Defined here: ");
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
                builder.addSecondarySource(duplicateVariable.first(), "Defined here: ");
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
                builder.addSecondarySource(unqualifiedSelf.method(), "in method: ");
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
                    builder.addSecondarySource(available.region(), toStr + " in: ");
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
                    builder.addSecondarySource(available.region(), toStr + " in: ");
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

                if (unknownMember.protectedPointer() != null) {
                    builder.append("A member of the name '").append(unknownMember.name())
                           .append("' exists, but is not accessible here: ");
                    builder.append(unknownMember.protectedPointer());
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

    private void createImportError(ImportError errorType, LogBuilder builder) {

        switch (errorType) {
            case ImportError.NoClassFound(var region, var path) -> {
                builder.setTitle("No class found");
                var target = path.mkString(".");
                builder.append("No Class found for path '").append(target).append("'");
                builder.setPrimarySource(region);
            }
            case ImportError.UnknownImportType(var region, var name, var available) -> {
                builder.setTitle("Unknown type");
                builder.append("Unknown type '").append(name).append("'");
                var suggestions = DidYouMean.suggestions(available, name, 5);

                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Did you mean: ").append(quoted);
                }
                builder.setPrimarySource(region);
            }
            case ImportError.GenericCountMismatch(var region, var name, var expected, var found) -> {
                builder.setTitle("Generic count mismatch");
                builder.append("Generic count mismatch for '").append(name).append("'");
                builder.append("Expected ").append(expected).append(" but found ").append(found);
                builder.setPrimarySource(region);
            }
            case ImportError.DuplicateItem(var first, var second, var item) -> {
                builder.setTitle("Duplicate item");
                builder.append("Duplicate item '").append(item).append("'");
                builder.setPrimarySource(second);
                builder.addSecondarySource(first, "First defined here: ");
            }
            case ImportError.DuplicateItemWithMessage(var first, var second, var item, var message) -> {
                builder.setTitle("Duplicate item");
                builder.append("Duplicate item '").append(item).append("'");
                builder.append(message);
                builder.setPrimarySource(second);
                builder.addSecondarySource(first, "Defined here: ");
            }
            case ImportError.NoItemFound(var region, var item, var cls) -> {
                builder.setTitle("No item found");
                builder.append("No item '").append(item).append("' found in class '")
                          .append(cls.mkString(".")).append("'");
                builder.setPrimarySource(region);
            }
            case ImportError.InnerClassImport(Region region, ObjectPath cls) -> {
                builder.setTitle("Inner class import");
                builder.append("Inner class '").append(cls.mkString(".")).append("' cannot be imported directly");
                builder.setPrimarySource(region);
            }
            case ImportError.InvalidAlias(Region region, String givenAlias, String foundClassName) -> {
                builder.setTitle("Invalid alias");
                builder.append("Cannot alias class '").append(foundClassName)
                       .append("' as '").append(givenAlias).append("'");
                builder.append("The alias must contain the class name '").append(foundClassName).append("'");

                builder.setPrimarySource(region);
            }
            case ImportError.UnnecessaryAlias(Region region1, String givenAlias) -> {
                builder.setTitle("Unnecessary alias");
                builder.append("Unnecessary alias '").append(givenAlias).append("'");
                builder.append("Cannot use an alias without conflicts");
                builder.setPrimarySource(region1);
            }

            case ImportError.InvalidName(var region, var name, var msg) -> {
                builder.setTitle("Invalid name");
                builder.append("Invalid name '").append(name).append("'");
                if (msg != null) {
                    builder.append(msg);
                }
                builder.setPrimarySource(region);
            }
        }

    }

}
