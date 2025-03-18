package org.karina.lang.compiler.logging;

import org.karina.lang.compiler.logging.errors.Error;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.logging.errors.AttribError;

import java.util.ArrayList;
import java.util.List;

public class LogFactory<T extends LogBuilder> {
    private final List<T> logs = new ArrayList<>();

    public T populate(Error log, T builder) {

        this.logs.add(builder);
        switch (log) {
            case FileLoadError error -> {
                var fileError = builder.setTitle("File Handling");
                var path = error.file().getAbsolutePath().replace("\\", "/");
                fileError.append("File: file:///").append(path);
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
                internalError.append("oh no, please report this issue. Anyway, here is the trace:");
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
                var importError = builder.setTitle("Import Error");
                populateImportError(errorType, importError);
            }
            case AttribError errorType -> {
                var linkError = builder.setTitle("Attribution Error");
                populateLinkError(errorType, linkError);
            }
        }
        return builder;

    }

    private void populateLinkError(AttribError errorType, LogBuilder builder) {

        switch (errorType) {
            case AttribError.NotAStruct notAClass -> {
                builder.setTitle("Not a struct");
                builder.append("Type '").append(notAClass.type()).append("' is not a struct");
                builder.setPrimarySource(notAClass.region());
            }
            case AttribError.TypeCycle typeCycle -> {
                builder.setTitle("Type Cycle");
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
                builder.append("Can't reassign final symbol '").append(finalAssignment.name()).append("'");
                builder.setPrimarySource(finalAssignment.region());
                builder.addSecondarySource(finalAssignment.regionOfFinalObject(), "Defined here: ");
            }
            case AttribError.ScopeFinalityAssignment scopeFinalityAssignment -> {
                builder.append("Cannot reassign a symbol used in a function expression'");
                builder.append("Variable '").append(scopeFinalityAssignment.name()).append("' is define outside the expression");
                builder.setPrimarySource(scopeFinalityAssignment.region());
            }
            case AttribError.DuplicateVariable duplicateVariable -> {
                builder.append("Variable '").append(duplicateVariable.name()).append("' was already declared");
                builder.setPrimarySource(duplicateVariable.second());
                builder.addSecondarySource(duplicateVariable.first(), "Defined here: ");
            }
            case AttribError.UnknownIdentifier(var region, var name, var available) -> {
                builder.append("Unknown identifier '").append(name).append("'");
                builder.setPrimarySource(region);
                var suggestions = DidYouMean.suggestions(available, name, 5);
                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Did you mean: ").append(quoted);
                }
            }
            case AttribError.UnqualifiedSelf unqualifiedSelf -> {
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
                builder.setTitle("Control Flow");
                builder.append(controlFlow.message());
                builder.setPrimarySource(controlFlow.region());
            }
            case AttribError.SignatureMismatch signatureMismatch -> {
                builder.setTitle("Signature Mismatch");
                builder.append("Could not match signature of method '").append(signatureMismatch.name()).append("'");
                builder.append("Found Signature ").append(signatureMismatch.found());
                builder.setPrimarySource(signatureMismatch.region());
                builder.append("Available Signatures:");
                if (signatureMismatch.available().isEmpty()) {
                    builder.append("None");
                }
                for (var available : signatureMismatch.available()) {
                    var toStr = available.value().toString();
                    builder.addSecondarySource(available.region(), toStr + " in: ");
                }
            }
            case AttribError.MissingConstructor missingConstructor -> {
                builder.setTitle("Constructor Mismatch");
                builder.append("Could not find Constructor of '").append(missingConstructor.object()).append("'");
                builder.append("With parameters ").append(missingConstructor.names());
                builder.setPrimarySource(missingConstructor.region());
                builder.append("Available names:");
                if (missingConstructor.available().isEmpty()) {
                    builder.append("None");
                }
                for (var available : missingConstructor.available()) {
                    var toStr = available.value().toString();
                    builder.addSecondarySource(available.region(), toStr + " in: ");
                }
            }
            case AttribError.UnknownCast unknownCast -> {
                builder.append("Unknown Cast");
                builder.append("Cannot infer type of cast, give the type explicitly");
                builder.setPrimarySource(unknownCast.region());
            }
            case AttribError.UnknownField unknownField -> {
                builder.append("Unknown Member '").append(unknownField.name()).append("'");
                builder.setPrimarySource(unknownField.region());
            }
            case AttribError.NotSupportedType notSupportedType -> {
                builder.setTitle("Unsupported fieldType");
                builder.append("Type '").append(notSupportedType.type())
                       .append("' is not supported here");
                builder.setPrimarySource(notSupportedType.region());
            }
            case AttribError.NotSupportedExpression notSupportedExpression -> {
                builder.setTitle("Unsupported use of Expression");
                builder.append(notSupportedExpression.readable());
                builder.setPrimarySource(notSupportedExpression.region());
            }
            case AttribError.NotSupportedOperator notSupportedOperator -> {
                builder.setTitle("Unsupported Operator");
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

    private void populateImportError(ImportError errorType, LogBuilder logBuilder) {

        switch (errorType) {
            case ImportError.NoClassFound(var region, var path) -> {
                var target = path.mkString(".");
                logBuilder.append("No Class found for path '").append(target).append("'");
                logBuilder.setPrimarySource(region);
            }
            case ImportError.UnknownImportType(var region, var name, var available) -> {
                logBuilder.append("Unknown fieldType '").append(name).append("'");
                var suggestions = DidYouMean.suggestions(available, name, 5);

                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    logBuilder.append("Did you mean: ").append(quoted);
                }
                logBuilder.setPrimarySource(region);
            }
            case ImportError.GenericCountMismatch(var region, var name, var expected, var found) -> {
                logBuilder.append("Generic count mismatch for '").append(name).append("'");
                logBuilder.append("Expected ").append(expected).append(" but found ").append(found);
                logBuilder.setPrimarySource(region);
            }
            case ImportError.DuplicateItem(var first, var second, var item) -> {
                logBuilder.append("Duplicate item '").append(item).append("'");
                logBuilder.setPrimarySource(second);
                logBuilder.addSecondarySource(first, "Defined here: ");
            }
            case ImportError.DuplicateItemWithMessage(var first, var second, var item, var message) -> {
                logBuilder.append("Duplicate item '").append(item).append("'");
                logBuilder.append(message);
                logBuilder.setPrimarySource(second);
                logBuilder.addSecondarySource(first, "Defined here: ");
            }
            case ImportError.NoItemFound(var region, var item, var cls) -> {
                logBuilder.append("No item '").append(item).append("' found in class '")
                          .append(cls.mkString(".")).append("'");
                logBuilder.setPrimarySource(region);
            }
            case ImportError.InvalidName(var region, var name, var msg) -> {
                logBuilder.append("Invalid name '").append(name).append("'");
                if (msg != null) {
                    logBuilder.append(msg);
                }
                logBuilder.setPrimarySource(region);
            }
            case ImportError.JavaNotSupported(var region) -> {
                logBuilder.append("Java item are not supported yet");
                logBuilder.setPrimarySource(region);
            }
        }

    }

}
