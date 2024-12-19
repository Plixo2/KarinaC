package org.karina.lang.compiler.errors;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.types.LinkError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Error logging system for the compiler.
 * Used to log every type of error that can occur during the compilation process.
 * The errors are stored in a list and can be accessed later to generate a report.
 * <p>
 *
 * Remember to throw a {@link KarinaException} when an error is logged.
 * The {@link KarinaException} cannot contain any information about the error,
 * as the error information is stored in the log.
 * <p>
 *
 *
 *<pre>
 * {@code
 * Log.syntaxError(region, "Expected a number");
 * throw new Log.KarinaException();
 * }
 * </pre>
 *
 * Use {@link ErrorCollector} to collect multiple errors with a single try-with-resources block.
 *
 */
public class Log {
    private static final boolean COLOR = false;
    public static final String RESET = COLOR ? "\u001B[0m" : "";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[37m";
    private static final String WHITE = "\u001B[97m";

    public static final String ERROR_TITLE_COLOR = COLOR ? PURPLE : "";
    public static final String ERROR_MESSAGE_COLOR = COLOR ? YELLOW : "";
    public static final String ERROR_TRACE = COLOR ? GRAY : "";


    //Thread safe list of logs
    private static final List<LogWithTrace> entries = new CopyOnWriteArrayList<>();

    public static void fileError(FileLoadError error) {
        addError(error);
    }

    public static void syntaxError(Span region, String message) {
        addError(new Error.SyntaxError(region, message));
    }

    public static void linkError(LinkError error) {
        addError(error);
    }

    public static void importError(ImportError errorType) {
        addError(errorType);
    }

    public static void temp(Span region, String msg) {
        addError(new Error.TemporaryErrorRegion(region, msg));
    }

    public static void invalidState(Span region, Class<?> aClass, String expectedState) {
        addError(new Error.InvalidState(region, aClass, expectedState));
    }

    private static void addError(Error entry) {

        var stackTrace = Thread.currentThread().getStackTrace();
        entries.add(new LogWithTrace(stackTrace, entry));

    }

    public static LogCollector<ConsoleLogBuilder> generateReport(boolean verbose) {

        var report = new LogCollector<ConsoleLogBuilder>();

        var copy = new ArrayList<>(entries);
        for (var withException : copy) {
            var print = report.populate(withException.entry(), new ConsoleLogBuilder());
            if (verbose && withException.stack() != null) {
                print.appendStack(withException.stack());
            }
        }
        return report;

    }


    public static boolean hasErrors() {
        return !entries.isEmpty();
    }

    public static List<org.karina.lang.compiler.errors.types.Error> getLogs() {
        return entries.stream().map(LogWithTrace::entry).toList();
    }

    public static void clearLogs() {
        entries.clear();
    }

    public record LogWithTrace(@Nullable StackTraceElement[] stack, Error entry) {}

    public static class KarinaException extends RuntimeException {}

}
