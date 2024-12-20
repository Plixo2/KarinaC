package org.karina.lang.compiler.errors;

import org.karina.lang.compiler.DidYouMean;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.types.LinkError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LogCollector<T extends LogBuilder> {
    private final List<T> logs = new ArrayList<>();


    public T populate(Error log, T builder) {
        this.logs.add(builder);
        switch (log) {
            case FileLoadError error -> {
                var fileError = builder.setTitle("File Handling");
                fileError.append("File: ").append(error.file().getAbsolutePath());
                fileError.append("Type: ").append(error.errorMessage());
            }
            case Error.InvalidState(var region, var aClass, var expectedState) -> {
                var invalidState = builder.setTitle("Invalid State");
                invalidState.append("Expected state: ").append(expectedState);
                invalidState.append("Class: ").append(aClass.getSimpleName());
                invalidState.setPrimarySource(region);
            }
            case Error.SyntaxError(var region, var msg) -> {
                var syntaxError = builder.setTitle("Syntax Error");
                syntaxError.append(msg);
                syntaxError.setPrimarySource(region);
            }
            case Error.TemporaryErrorRegion(var region, var message) -> {
                var tempError = builder.setTitle(message);
                tempError.setPrimarySource(region);
            }
            case ImportError errorType -> {
                var importError = builder.setTitle("Import Error");
                populateImportError(errorType, importError);
            }
            case LinkError errorType -> {
                var linkError = builder.setTitle("Type Error");
                populateLinkError(errorType, linkError);
            }
        }
        return builder;
    }


    private void populateLinkError(LinkError errorType, LogBuilder builder) {
        switch (errorType) {
            case LinkError.FinalAssignment finalAssignment -> {
                builder.append("Cannot reassign final symbol '").append(finalAssignment.name()).append("'");
                builder.setPrimarySource(finalAssignment.region());
            }
            case LinkError.ScopeFinalityAssignment scopeFinalityAssignment -> {
                builder.append("Cannot reassign a symbol used in a function expression'");
                builder.append("Variable '").append(scopeFinalityAssignment.name()).append("' is define outside the expression");
                builder.setPrimarySource(scopeFinalityAssignment.region());
            }
            case LinkError.DuplicateVariable duplicateVariable -> {
                builder.append("Variable '").append(duplicateVariable.name()).append("' was already declared");
                builder.setPrimarySource(duplicateVariable.second());
                builder.addSecondarySource(duplicateVariable.first());
            }
            case LinkError.UnknownIdentifier unknownIdentifier -> {
                builder.append("Unknown identifier '").append(unknownIdentifier.name()).append("'");
                builder.setPrimarySource(unknownIdentifier.region());
            }
            case LinkError.UnqualifiedSelf unqualifiedSelf -> {
                builder.append("Invalid use of 'self' in a static context");
                builder.setPrimarySource(unqualifiedSelf.region());
                builder.addSecondarySource(unqualifiedSelf.method());
            }
        }
    }

    private void populateImportError(ImportError errorType, LogBuilder logBuilder) {
        switch (errorType) {
            case ImportError.NoUnitFound(var path, var root) -> {
                var target = path.value().mkString(".");
                logBuilder.append("No unit found for path '").append(target).append("'");
                var suggestions = root.getAllUnitsRecursively()
                                      .stream()
                                      .map(ref -> ref.path().tail().mkString("."))
                                      .toList();
                var filtered = DidYouMean.suggestions(new HashSet<>(suggestions), target, 5);
                if (!filtered.isEmpty()) {
                    logBuilder.append("Did you mean: ").append(filtered);
                }
                logBuilder.setPrimarySource(path.region());
            }
            case ImportError.UnknownImportType(var region, var name, var available, var root) -> {
                logBuilder.append("Unknown type '").append(name).append("'");
                var suggestions = DidYouMean.suggestions(available, name, 5);
                var importSuggestion = DidYouMean.suggestImportForType(name, root, 5);

                if (!suggestions.isEmpty()) {
                    var quoted = suggestions.stream().map(x -> "'" + x + "'").toList();
                    logBuilder.append("Did you mean: ").append(quoted);
                }
                if (!importSuggestion.isEmpty()) {
                    var strList = importSuggestion.stream().map(ref -> ref.everythingButLast().tail()
                                                                       .toString()).toList();
                    logBuilder.append("type ").append("'").append(name).append("' also exists in ").append(strList);
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
            case ImportError.NoItemFound(var region, var item, var unit, var root) -> {
                logBuilder.append("No item '").append(item).append("' found in unit '")
                          .append(unit.path()).append("'");
                var suggestions = unit.items().stream().map(ref -> ref.name().value()).toList();
                var suggestionsFiltered = DidYouMean.suggestions(new HashSet<>(suggestions), item, 5);
                if (!suggestionsFiltered.isEmpty()) {
                    logBuilder.append("Did you mean: ").append(suggestionsFiltered);
                }
                var importSuggestion = DidYouMean.suggestImportForType(item, root, 5);
                if (!importSuggestion.isEmpty()) {
                    var strList = importSuggestion.stream().map(ref -> ref.everythingButLast().tail()
                                                                       .toString()).toList();
                    logBuilder.append("type ").append("'").append(item).append("' also exists in ").append(strList);
                }

                logBuilder.setPrimarySource(region);
                var startUnitRegion = new Span(
                        unit.region().source(),
                        unit.region().start(),
                        unit.region().start()
                );
                logBuilder.addSecondarySource(startUnitRegion);
            }
            case ImportError.JavaNotSupported(var region) -> {
                logBuilder.append("Java item are not supported yet");
                logBuilder.setPrimarySource(region);
            }
        }
    }

    public static String consoleString(LogCollector<ConsoleLogBuilder> builderConsoleReport) {
        var builder = new StringBuilder();
        for (var log : builderConsoleReport.logs) {
            builder.append(log.name()).append("\n");
            for (var line : log.lines()) {
                builder.append("    ").append(line).append("\n");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
