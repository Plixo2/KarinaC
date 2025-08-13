package org.karina.lang.compiler.logging;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.errors.Error;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.logging.errors.GenerateError;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.TextSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

///
/// Error logging system for the compiler.
/// Used to log every type of error that can occur during the compilation process.
/// The errors can be accessed later to generate a report.
///
///
/// Remember to throw a {@link KarinaException} when an error is logged.
/// The {@link KarinaException} cannot contain any information about the error,
/// as the error information is stored in the log in the {@link Context}.
///
///
///
///
/// ```java
/// // c is the context
/// Log.syntaxError(c, region, "Expected a number");
/// throw new Log.KarinaException();
/// }
/// ```
///
/// Use {@link Context#fork} to collect multiple errors with a single try-with-resources block.
///
///
///
public class Log {

    //Flight recorder for logging. Not safe for concurrent use.
    private static final FlightRecorder FLIGHT_RECORDER = new FlightRecorder();


    //TODO make recorder local to the context
    /**
     * The default logs that are enabled.
     * You can add custom logs here to enable them by default.
     * @see LogTypes
     */
    private static final Set<LogTypes> LOGS = Set.of(

    );

    private static Set<LogTypes> LOG_PROPERTY;

    static {
        var property = System.getProperty("karina.logging", "none");
        updateLogLevel(property);
    }
    public static void updateLogLevel(String level) {
        LOG_PROPERTY = switch (level) {
            case "none" -> Set.of();
            case "basic" -> Set.of(
                    LogTypes.METHOD_NAME,
                    LogTypes.CLASS_NAME,
                    LogTypes.EXPR,
                    LogTypes.VARIABLE,
                    LogTypes.BRANCH,
                    LogTypes.STRING_INTERPOLATION,
                    LogTypes.CLOSURE,
                    LogTypes.LOWERING,
                    LogTypes.LOWERING_BRIDGE_METHODS,
                    LogTypes.GENERATION
            );
            case "verbose" -> {
                var set = new HashSet<>(Set.of(LogTypes.values()));
                set.remove(LogTypes.JVM_CLASS_LOADING);
                set.remove(LogTypes.LOADED_CLASSES);
                set.remove(LogTypes.IMPORTS);
                set.remove(LogTypes.IMPORT_PRELUDE);

                yield set;
            }
            case "verbose_jvm" -> Set.of(LogTypes.values());
            default -> {
                throw new IllegalStateException(
                        "Invalid logging level: "
                                + level
                                + ". Valid options are: none, basic, verbose, verbose_jvm"
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
        LOWERING_BRIDGE_METHODS,
        GENERATION,
        GENERATION_CLASS,
        GENERATION_VAR

        ;

        public boolean isVisible() {
            return LOGS.contains(this) || LOG_PROPERTY.contains(this);
        }
    }




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


    public static void warn(IntoContext c, Region region, String warning) {
        addWarning(c, new Error.Default(region, warning));
    }

    public static void warn(IntoContext c, Object... args) {
        var string = String.join(" ", Arrays.stream(args).map(Log::objectToString).toList());
        addWarning(c, new Error.Warn(string));
    }

    public static void fileError(IntoContext c, FileLoadError error) {
        addError(c, error);
    }

    public static void syntaxError(IntoContext c, Region region, String message) {
        addError(c, new Error.SyntaxError(region, message));
    }

    public static void error(IntoContext c, Error errorType) {
        addError(c, errorType);
    }

    public static void invalidName(IntoContext c, Region region, String name) {
        addError(c, new ImportError.InvalidName(region, name, null));
    }

    public static void temp(IntoContext c, Region region, String msg) {
        addError(c, new Error.Default(region, msg));
    }

    public static void internal(IntoContext c, Throwable e) {
        addError(c, new Error.InternalException(e));
    }

    public static void bytecode(IntoContext c, TextSource resource, String name, String msg) {
        addError(c, new Error.BytecodeLoading(resource.resource(), name, msg));
    }


    public static void generate(IntoContext c, GenerateError error) {
        addError(c, error);
    }

    public static void bytecode(IntoContext c, Region region, String name, String msg) {
        addError(c, new Error.BytecodeLoading(region.source().resource(), name, msg));
    }

    private static void addError(IntoContext context, Error entry) {
        var stackTrace = Thread.currentThread().getStackTrace();
        context.intoContext().addError(new LogWithTrace(stackTrace, entry));
    }

    private static void addWarning(IntoContext context,Error entry) {
        var stackTrace = Thread.currentThread().getStackTrace();
        context.intoContext().addWarn(new LogWithTrace(stackTrace, entry));
    }

    public static void clearAllLogs() {
        FLIGHT_RECORDER.clear();
    }

    public static class KarinaException extends RuntimeException {}

    public record LogWithTrace(@Nullable StackTraceElement[] stack, Error entry) {

        public String mkString(boolean addTrace) {

            var information = new ConsoleErrorInformation();
            this.entry.addInformation(information);

            if (addTrace && this.stack != null) {
                information.appendStack(this.stack);
            }

            return information.readable();

        }

}

}


