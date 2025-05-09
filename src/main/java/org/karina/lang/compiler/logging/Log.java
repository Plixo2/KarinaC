package org.karina.lang.compiler.logging;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.logging.errors.*;
import org.karina.lang.compiler.logging.errors.Error;
import org.karina.lang.compiler.utils.Region;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Error logging system for the compiler.
 * Used to log every fieldType of error that can occur during the compilation process.
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

    private static final Set<LogTypes> LOG_OVERRIDE;

    static {
        var property = System.getProperty("karina.logging", "none");

        LOG_OVERRIDE = switch (property) {
            case "none" -> Set.of();
            //only missing JVM_CLASS_LOADING
            case "verbose" -> Set.of(
                    LogTypes.CHECK_TYPE,
                    LogTypes.METHOD_NAME,
                    LogTypes.CLASS_NAME,
                    LogTypes.CALLS,
                    LogTypes.EXPR,
                    LogTypes.VARIABLE,
                    LogTypes.IMPORTS,
                    LogTypes.IMPORT_PRELUDE,
                    LogTypes.IMPORT_STAGES,
                    LogTypes.IMPLICIT_CONVERSION,
                    LogTypes.AMBIGUOUS,
                    LogTypes.BRANCH,
                    LogTypes.SUPER_WARN,
                    LogTypes.ASSERTIONS,
                    LogTypes.STRING_INTERPOLATION,
                    LogTypes.LOADED_CLASSES,
                    LogTypes.CLOSURE,
                    LogTypes.MEMBER,
                    LogTypes.LOWERING,
                    LogTypes.GENERATION
            );
            case "verbose_jvm" -> Set.of(
                    LogTypes.CHECK_TYPE,
                    LogTypes.METHOD_NAME,
                    LogTypes.CLASS_NAME,
                    LogTypes.CALLS,
                    LogTypes.EXPR,
                    LogTypes.VARIABLE,
                    LogTypes.IMPORTS,
                    LogTypes.IMPORT_PRELUDE,
                    LogTypes.IMPORT_STAGES,
                    LogTypes.IMPLICIT_CONVERSION,
                    LogTypes.AMBIGUOUS,
                    LogTypes.BRANCH,
                    LogTypes.SUPER_WARN,
                    LogTypes.ASSERTIONS,
                    LogTypes.STRING_INTERPOLATION,
                    LogTypes.JVM_CLASS_LOADING,
                    LogTypes.LOADED_CLASSES,
                    LogTypes.CLOSURE,
                    LogTypes.MEMBER,
                    LogTypes.LOWERING,
                    LogTypes.GENERATION
            );
            default -> {
                throw new IllegalStateException(
                        "Invalid logging level: "
                        + property
                        + ". Valid options are: none, verbose, verbose_jvm"
                );
            }
        };

    }

    /**
     * Used to disable certain logs
     */
    public enum LogTypes {
        CHECK_TYPE,
        METHOD_NAME,
        CLASS_NAME,
        CALLS,
        EXPR,
        VARIABLE,
        IMPORTS,
        IMPORT_PRELUDE,
        IMPORT_STAGES,
        IMPLICIT_CONVERSION,
        AMBIGUOUS,
        BRANCH,
        SUPER_WARN,
        ASSERTIONS,
        STRING_INTERPOLATION,
        JVM_CLASS_LOADING,
        LOADED_CLASSES,
        CLOSURE,
        MEMBER,
        LOWERING,
        GENERATION


        ;

        public static final Set<LogTypes> VISIBLE = Set.of(


        );

        public boolean isVisible() {
            return VISIBLE.contains(this) || LOG_OVERRIDE.contains(this);
        }
    }


    //A thread-safe List of logs
    private static final List<LogWithTrace> ENTRIES = new CopyOnWriteArrayList<>();
    private static final List<LogWithTrace> WARNINGS = new CopyOnWriteArrayList<>();

    //Flight recorder for logging. Not safe for concurrent use.
    private static final FlightRecorder FLIGHT_RECORDER = new FlightRecorder();


    public static void begin(String name) {
        synchronized (FLIGHT_RECORDER) {
            FLIGHT_RECORDER.begin(name);
        }
    }

    public static void beginType(LogTypes type, String name) {
        if (!type.isVisible()) {
            return;
        }
        synchronized (FLIGHT_RECORDER) {
            FLIGHT_RECORDER.begin(name);
        }
    }

    public static void end(String name) {
        synchronized (FLIGHT_RECORDER) {
            FLIGHT_RECORDER.end(name);
        }
    }
    public static FlightRecorder.Sample addSample(String name) {
        synchronized (FLIGHT_RECORDER) {
            return FLIGHT_RECORDER.beginSample(name);
        }
    }
    public static FlightRecorder.Sample addSuperSample(String name) {
        synchronized (FLIGHT_RECORDER) {
            return FLIGHT_RECORDER.beginSuperSample(name);
        }
    }

    public static void end(String name, Object... args) {
        synchronized (FLIGHT_RECORDER) {
            for (var arg : args) {
                var string = "-> " + objectToString(arg);
                FLIGHT_RECORDER.begin(string).includeTime = false;
                FLIGHT_RECORDER.end(string);
            }
            FLIGHT_RECORDER.end(name);
        }
    }

    public static void endType(LogTypes type, String name, Object... args) {
        if (!type.isVisible()) {
            return;
        }
        synchronized (FLIGHT_RECORDER) {
            if (args.length != 0) {
                var str = String.join(" ", Arrays.stream(args).map(Log::objectToString).toList());
                FLIGHT_RECORDER.begin(str).includeTime = false;
                FLIGHT_RECORDER.end(str);
            }

            FLIGHT_RECORDER.end(name);
        }
    }

    private static String objectToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Region region) {
            region = region.reorder();
            var path = region.source().resource().identifier();
            var column = region.start().column() + 1;
            var line = region.start().line() + 1;
            return "region: " + path + ":" + line + ":" + column;
        }
        return obj.toString();
    }

    public static void recordType(LogTypes type, String name, Object... args) {
        if (!type.isVisible()) {
            return;
        }
        var literal =  name + ": " + String.join(" ", Arrays.stream(args).map(Log::objectToString).toList());
        synchronized (FLIGHT_RECORDER) {
            FLIGHT_RECORDER.begin(literal).includeTime = false;
            FLIGHT_RECORDER.end(literal);
        }
    }


    public static void record(Object name, Object... args) {

        var literal =  name + ": " + String.join(" ", Arrays.stream(args).map(Log::objectToString).toList());
        synchronized (FLIGHT_RECORDER) {
            FLIGHT_RECORDER.begin(literal).includeTime = false;
            FLIGHT_RECORDER.end(literal);
        }
    }

    public static @Nullable FlightRecorder.SectionRecord getRecordedLogs() {
        synchronized (FLIGHT_RECORDER) {
            return FLIGHT_RECORDER.topLevel;
        }
    }

    public static void warn(Region region, String warning) {
        addWarning(new Error.TemporaryErrorRegion(region, warning));
    }

    public static void warn(Object... args) {
        var string = String.join(" ", Arrays.stream(args).map(Log::objectToString).toList());
        addWarning(new Error.Warn(string));
    }

    public static void assert_(boolean args, Object... message) {
        if (!args) {
            var messageStr = String.join(" ", Arrays.stream(message).map(Log::objectToString).toList());
            messageStr = "(warn) Assertion failed: " + messageStr;
            Log.recordType(Log.LogTypes.ASSERTIONS, messageStr);
            internal(new AssertionError(messageStr));
            throw new KarinaException();
        }
    }

    public static void assert_(Supplier<Boolean> function, Object... message) {
        assert_(function.get(), message);
    }



    public static void fileError(FileLoadError error) {
        addError(error);
    }

    public static void syntaxError(Region region, String message) {
        addError(new Error.SyntaxError(region, message));
    }

    public static void lowerError(LowerError error) {
        addError(error);
    }

    public static void attribError(AttribError error) {
        addError(error);
    }

    public static void importError(ImportError errorType) {
        addError(errorType);
    }

    public static void invalidName(Region region, String name) {
        addError(new ImportError.InvalidName(region, name, null));
    }
    public static void invalidName(Region region, String name, String message) {
        addError(new ImportError.InvalidName(region, name, message));
    }

    public static void temp(Region region, String msg) {
        addError(new Error.TemporaryErrorRegion(region, msg));
    }

    public static void internal(Throwable e) {
        addError(new Error.InternalException(e));
    }

    public static void bytecode(TextSource resource, String name, String msg) {
        addError(new Error.BytecodeLoading(resource.resource(), name, msg));
    }

    public static void bytecode(Region region, String name, String msg) {
        addError(new Error.BytecodeLoading(region.source().resource(), name, msg));
    }


    public static void cliParseError(String message) {
        addError(new Error.ParseError(message));
    }

    public static void invalidState(Region region, Class<?> aClass, String expectedState) {
        addError(new Error.InvalidState(region, aClass, expectedState));
    }

    private static void addError(Error entry) {

        var stackTrace = Thread.currentThread().getStackTrace();
        ENTRIES.add(new LogWithTrace(stackTrace, entry));

    }

    private static void addWarning(Error entry) {

        var stackTrace = Thread.currentThread().getStackTrace();
        WARNINGS.add(new LogWithTrace(stackTrace, entry));

    }

    public static List<LogWithTrace> getEntries() {
        return new ArrayList<>(ENTRIES);
    }

    public static List<LogWithTrace> getWarnings() {
        return new ArrayList<>(WARNINGS);
    }

    public static boolean hasErrors() {
        return !ENTRIES.isEmpty();
    }

    public static boolean hasWarnings() {
        return !WARNINGS.isEmpty();
    }

//    public static List<Error> getLogs() {
//        return entries.stream().map(LogWithTrace::entry).toList();
//    }

    public static void clearLogs() {
        ENTRIES.clear();
        WARNINGS.clear();
        FLIGHT_RECORDER.clear();
    }


    public static class KarinaException extends RuntimeException {}

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

























































}


