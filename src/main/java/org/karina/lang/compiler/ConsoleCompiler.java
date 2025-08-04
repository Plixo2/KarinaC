package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.ColorOut;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.logging.LogColor;
import org.karina.lang.compiler.utils.AutoRun;
import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

public class ConsoleCompiler {

    public static boolean compile(Config config) throws IOException {
        var startTime = System.currentTimeMillis();

        printWelcome();
        printVersions();

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();

        var outputConfig = config.outputFile();
        var compiler = KarinaCompiler.builder()
                                     .outputConfig(outputConfig)
                                     .errorCollection(errors)
                                     .warningCollection(warnings)
                                     .flightRecordCollection(recordings)
                                     .useBinaryFormat(config.useBinaryFormat())
                                     .build();

        var sourceDirectory = config.source();
        var fileTree = FileLoader.loadTree(sourceDirectory);

        printStartMessage(fileTree, sourceDirectory);

        var compilation = compiler.compile(fileTree);

        writeFlight(recordings, config.flightConfig().outputFile());

        if (config.flightConfig().printToConsole()) {
            System.out.println();
            FlightRecordCollection.printColored(recordings, true, System.out);
            System.out.println();
        }

        if (compilation != null) {
            var outputFile = outputConfig != null ? outputConfig.outputFile() : null;
            var endTime = System.currentTimeMillis();
            onSuccess(outputFile, warnings, endTime - startTime);

            if (config.run()) {
                AutoRun.run(compilation);
            }

            return true;
        } else {
            onError(warnings, errors);
            return false;
        }

    }


    private static void printWelcome() {
        var welcome_small =
                """
                
                    _  __
                   | |/ /  __ _   _ _   _   _ _    __ _
                   | ' <  / _  | | '_| | | | ' \\  / _  |
                   |_|\\_\\ \\__,_| |_|   |_| |_||_| \\__,_|
                """;

        ColorOut.begin(LogColor.BLUE)
                .append(welcome_small)
                .out(System.out);
    }


    private static void printStartMessage(DefaultFileTree fileTree, Path sourceDirectory) {
        var fileCount = fileTree.leafCount();
        var fileCountString = fileCount == 1 ? "file" : "files";
        ColorOut.begin(LogColor.GRAY)
                .append("Compiling '")
                .append(sourceDirectory.toString().replace("\\", "/"))
                .append("' (")
                .append(fileCount)
                .append(" ")
                .append(fileCountString)
                .append(")")
                .out(System.out);
    }


    private static void printVersions() {
        var javaVersion = System.getProperty("java.version", "<unknown java version>");
        var vmName = System.getProperty("java.vm.name", "<unknown vm name>");

        ColorOut.begin(LogColor.GRAY)
                .append("Karina: ")
                .append(KarinaCompiler.VERSION)
                .out(System.out);

        ColorOut.begin(LogColor.GRAY)
                .append("Java: ")
                .append(vmName)
                .append(" ")
                .append(javaVersion)
                .out(System.out);
    }

    private static void onError(DiagnosticCollection warnings, DiagnosticCollection errors) {
        ColorOut.begin(LogColor.RED).append("Build failed").out(System.out);
        System.out.println();
        System.out.flush();

        LogColor.YELLOW.out(System.out);
        DiagnosticCollection.print(warnings, true, System.out);
        LogColor.NONE.out(System.out);
        System.out.flush();

        DiagnosticCollection.print(errors, true, System.err);
    }


    private static void onSuccess(@Nullable Path outputFile, DiagnosticCollection warnings, long deltaTime) {

        if (outputFile != null) {
            var absolutePath = outputFile.toAbsolutePath();
            var file = absolutePath.getFileName();
            var path = absolutePath.getParent().toString().replace("\\", "/");

            ColorOut.begin(LogColor.GRAY)
                    .append("Created '")
                    .append(file)
                    .append("'")
                    .append(" in ")
                    .append("file:///")
                    .append(path)
                    .out(System.out);
        }

        ColorOut.begin(LogColor.GRAY)
                .append("Finished in ")
                .append(deltaTime)
                .append("ms")
                .out(System.out);


        LogColor.YELLOW.out(System.out);
        DiagnosticCollection.print(warnings, true, System.out);
        LogColor.NONE.out(System.out);
    }

    /**
     * Write the flight record to a file
     * @param recordings the flight record collection
     * @param path the path to write to, can be null
     */
    private static void writeFlight(FlightRecordCollection recordings, @Nullable Path path) {
        if (path == null) {
            return;
        }
        try (var filePrintStream = new PrintStream(new FileOutputStream(path.toFile()))){
            FlightRecordCollection.print(recordings, false, filePrintStream);
        } catch (FileNotFoundException e) {
            // Just print to console, no need to crash or more verbose logging
            e.printStackTrace();
        }
    }

}
