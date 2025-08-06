package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

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


    static Config fromProperties() {
        var run               = System.getProperty("karina.run", "false").equals("true");
        var console           = System.getProperty("karina.console", "true").equals("true");
        var consoleColor      = System.getProperty("karina.color", "true").equals("true");
        var flight            = System.getProperty("karina.flight", "resources/flight.txt");
        var outputFile        = System.getProperty("karina.out", "resources/out/build.jar");
        var shouldEmitClasses = System.getProperty("karina.classes", "true").equals("true");
        var sourceDirectory   = System.getProperty("karina.source", "resources/src/");
        var useBinaryFormat   = System.getProperty("karina.binary", "false").equals("true");

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
}
