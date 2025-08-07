package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/// Configuration interface for [ConsoleCompiler]
public interface Config {

    /// @return Path to the source directory.
    Path source();

    /// @return Whether to run the compiled code.
    boolean run();

    boolean coloredConsole();

    boolean useBinaryFormat();

    /// When given, print to the console with the given configuration
    /// If null dont print to the console
    Config.FlightConfig flightConfig();

    /// @return Destination for the jar file. When null, no jar file will be generated.
    @Nullable Config.OutputConfig outputFile();

    interface OutputConfig {
        /// @return Destination for the jar file. When null, no jar file will be generated.
        ///         Otherwise returns a path to a file, not a directory.
        ///         A existing file will be overwritten.
        Path outputFile();

        /// @return Whether to emit class files in the same directory as the output file.
        boolean emitClassFiles();

    }

    interface FlightConfig {

        boolean printToConsole();

        @Nullable Path outputFile();

    }

    // -----------------------------------------------------------------------
    // Defaults
    // -----------------------------------------------------------------------

    static Config fromProperties() {
        var run               = getProperty("karina.run", false);
        var console           = getProperty("karina.console", true);
        var consoleColor      = getProperty("karina.color", true);
        var shouldEmitClasses = getProperty("karina.classes", true);
        var useBinaryFormat   = getProperty("karina.binary", false);
        var flight            = getPropertyAsString("karina.flight", "../resources/flight.txt");
        var outputFile        = getPropertyAsString("karina.out", "../resources/out/build.jar");
        var sourceDirectory   = getPropertyAsString("karina.source", "../resources/src/");

        record FlightConfig(
                boolean printToConsole,
                @Nullable Path outputFile
        ) implements Config.FlightConfig {}
        record OutputConfig(
                Path outputFile,
                boolean emitClassFiles
        ) implements Config.OutputConfig {}
        record MainConfig(
                Path source,
                boolean run,
                boolean coloredConsole,
                boolean useBinaryFormat,
                FlightConfig flightConfig,
                OutputConfig outputFile
        ) implements Config {}


        return new MainConfig(
                Path.of(sourceDirectory),
                run,
                consoleColor,
                useBinaryFormat,
                new FlightConfig(console, Path.of(flight)),
                new OutputConfig(Path.of(outputFile), shouldEmitClasses)
        );
    }


    private static boolean getProperty(String key, boolean defaultValue) {
        return System.getProperty(key, Boolean.toString(defaultValue)).equals("true");
    }

    private static String getPropertyAsString(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
}
