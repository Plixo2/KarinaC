package org.karina.lang.compiler.errors;

import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.types.AttribError;

import java.util.ArrayList;
import java.util.HashSet;
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
            case Error.BytecodeLoading(var resource, var name, var msg) -> {
                var fileError = builder.setTitle("Bytecode Handling");
                fileError.append("File: ").append(resource.identifier());
                fileError.append("Class: ").append(name);
                fileError.append(msg);
            }
            case Error.TemporaryErrorRegion(var region, var message) -> {
                var tempError = builder.setTitle(message);
                tempError.setPrimarySource(region);
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
            }
            case AttribError.ScopeFinalityAssignment scopeFinalityAssignment -> {
                builder.append("Cannot reassign a symbol used in a function expression'");
                builder.append("Variable '").append(scopeFinalityAssignment.name()).append("' is define outside the expression");
                builder.setPrimarySource(scopeFinalityAssignment.region());
            }
            case AttribError.DuplicateVariable duplicateVariable -> {
                builder.append("Variable '").append(duplicateVariable.name()).append("' was already declared");
                builder.setPrimarySource(duplicateVariable.second());
                builder.addSecondarySource(duplicateVariable.first());
            }
            case AttribError.UnknownIdentifier(var region, var name, var available) -> {
                builder.append("Unknown identifier '").append(name).append("'");
                var suggestions = DidYouMean.suggestions(available, name, 5);
                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    builder.append("Did you mean: ").append(quoted);
                }
                builder.setPrimarySource(region);
            }
            case AttribError.UnqualifiedSelf unqualifiedSelf -> {
                builder.append("Invalid use of 'self' in a static context");
                builder.setPrimarySource(unqualifiedSelf.region());
                builder.addSecondarySource(unqualifiedSelf.method());
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
            case AttribError.MissingField missingField -> {
                builder.append("Missing Field '").append(missingField.name()).append("'");
                builder.setPrimarySource(missingField.region());
            }
            case AttribError.UnknownField unknownField -> {
                builder.append("Unknown Member '").append(unknownField.name()).append("'");
                builder.setPrimarySource(unknownField.region());
            }
            case AttribError.NotSupportedType notSupportedType -> {
                builder.setTitle("Unsupported type");
                builder.append("Type '").append(notSupportedType.type())
                       .append("' is not supported here");
                builder.setPrimarySource(notSupportedType.region());
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
            case ImportError.NoUnitFound(var region, var path) -> {
                var target = path.mkString(".");
                logBuilder.append("No unit found for path '").append(target).append("'");
//                var suggestions = root.getAllUnitsRecursively()
//                                      .stream()
//                                      .map(ref -> ref.path().tail().mkString("."))
//                                      .toList();
//                var filtered = DidYouMean.suggestions(new HashSet<>(suggestions), target, 5);
//                if (!filtered.isEmpty()) {
//                    logBuilder.append("Did you mean: ").append(filtered);
//                }
                logBuilder.setPrimarySource(region);
            }
            case ImportError.UnknownImportType(var region, var name, var available) -> {
                logBuilder.append("Unknown type '").append(name).append("'");
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
                logBuilder.addSecondarySource(first);
            }
            case ImportError.NoItemFound(var region, var item, var cls) -> {
                logBuilder.append("No item '").append(item).append("' found in class '")
                          .append(cls.mkString(".")).append("'");
                logBuilder.setPrimarySource(region);
            }
            case ImportError.JavaNotSupported(var region) -> {
                logBuilder.append("Java item are not supported yet");
                logBuilder.setPrimarySource(region);
            }
        }

    }

}
