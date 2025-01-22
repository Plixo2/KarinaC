package org.karina.lang.compiler.errors;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.Resource;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.types.AttribError;

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
    /*
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
    */


    //A thread-safe List of logs
    private static final List<LogWithTrace> entries = new CopyOnWriteArrayList<>();
    private static final List<LogWithTrace> warnings = new CopyOnWriteArrayList<>();

    public static void warn(Region region, String warning) {
        addWarning(new Error.TemporaryErrorRegion(region, warning));
    }

    public static void fileError(FileLoadError error) {
        addError(error);
    }

    public static void syntaxError(Region region, String message) {
        addError(new Error.SyntaxError(region, message));
    }

    public static void attribError(AttribError error) {
        addError(error);
    }

    public static void importError(ImportError errorType) {
        addError(errorType);
    }

    public static void temp(Region region, String msg) {
        addError(new Error.TemporaryErrorRegion(region, msg));
    }

    public static void bytecode(Resource resource, String name, String msg) {
        addError(new Error.BytecodeLoading(resource, name, msg));
    }


    public static void cliParseError(String message) {
        addError(new Error.ParseError(message));
    }

    public static void invalidState(Region region, Class<?> aClass, String expectedState) {
        addError(new Error.InvalidState(region, aClass, expectedState));
    }

    private static void addError(Error entry) {

        var stackTrace = Thread.currentThread().getStackTrace();
        entries.add(new LogWithTrace(stackTrace, entry));

    }

    private static void addWarning(Error entry) {

        var stackTrace = Thread.currentThread().getStackTrace();
        warnings.add(new LogWithTrace(stackTrace, entry));

    }

    public static List<LogWithTrace> getEntries() {
        return new ArrayList<>(entries);
    }

    public static List<LogWithTrace> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public static boolean hasErrors() {
        return !entries.isEmpty();
    }

    public static boolean hasWarnings() {
        return !warnings.isEmpty();
    }

//    public static List<Error> getLogs() {
//        return entries.stream().map(LogWithTrace::entry).toList();
//    }

    public static void clearLogs() {
        entries.clear();
        warnings.clear();
    }



    public record LogWithTrace(@Nullable StackTraceElement[] stack, Error entry) {

        public String mkString(boolean addTrace) {

            var builder = new StringBuilder();
            var report = new LogFactory<ConsoleLogBuilder>();
            var print = report.populate(this.entry, new ConsoleLogBuilder());
            if (addTrace && this.stack != null) {
                print.appendStack(this.stack);
            }

            builder.append(print.name()).append("\n");
            for (var line : print.lines()) {
                builder.append("    ").append(line).append("\n");
            }
            builder.append("\n");

            return builder.toString();

        }

    }

    public static class KarinaException extends RuntimeException {}

}
