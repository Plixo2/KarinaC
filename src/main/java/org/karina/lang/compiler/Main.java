package org.karina.lang.compiler;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.*;
import org.karina.lang.compiler.utils.AutoRun;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.Path;

/**
 * Main entry
 */
public class Main {

    public static void main(String[] args) throws IOException {
        var failWithException = args.length != 0 && args[0].equals("--test");
        var run = args.length != 0 && args[0].equals("--run");

        var startTime = System.currentTimeMillis();

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


        var console = System.getProperty("karina.console", "false").equals("true");
        var flight = System.getProperty("karina.flight", "resources/flight.txt");
        var outputFile = System.getProperty("karina.out", "resources/out/build.jar");
        var shouldEmitClasses = System.getProperty("karina.classes", "true").equals("true");


        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();

        var compiler = new KarinaCompiler();
        compiler.setOutputFile(outputFile);
        compiler.setEmitClasses(shouldEmitClasses);

        compiler.setErrorCollection(errors);
        compiler.setWarningCollection(warnings);
        compiler.setFlightRecordCollection(recordings);

        var sourceDirectory = System.getProperty("karina.source", "resources/src/");
        var sourceDirectoryPath = Path.of(sourceDirectory);
        var fileTree = FileLoader.loadTree(sourceDirectoryPath);

        var fileCount = fileTree.leafCount();
        var fileCountString = fileCount == 1 ? "file" : "files";
        ColorOut.begin(LogColor.YELLOW)
                .append("Compiling '")
                .append(sourceDirectoryPath)
                .append("' (")
                .append(fileCount)
                .append(" ")
                .append(fileCountString)
                .append(")")
                .out(System.out);


        var success = compiler.compile(fileTree);


        writeFlight(recordings, flight);
        if (console) {
            System.out.println();
            FlightRecordCollection.printColored(recordings, true, System.out);
            System.out.println();
        }

        if (success) {
            var endTime = System.currentTimeMillis();
            onSuccess(outputFile, warnings, endTime - startTime);

            if (run && compiler.getJarCompilation() != null) {
                AutoRun.run(compiler.getJarCompilation());
            }

        } else {
            onError(warnings, errors);

            if (failWithException) {
                throw new IllegalStateException("Compilation failed");
            } else {
                System.exit(1);
            }
        }

    }

    private static void onError(DiagnosticCollection warnings, DiagnosticCollection errors) {
        ColorOut.begin(LogColor.RED).append("BUILD FAILED").out(System.out);

        LogColor.YELLOW.out(System.out);
        DiagnosticCollection.print(warnings, true, System.out);
        LogColor.NONE.out(System.out);
        System.out.flush();

        DiagnosticCollection.print(errors, true, System.err);
    }

    private static void onSuccess(String outputFile, DiagnosticCollection warnings, long deltaTime) {

        var absolutePath = Path.of(outputFile).toAbsolutePath();
        var file = absolutePath.getFileName();
        var path = absolutePath.getParent().toString().replace("\\", "/");
        ColorOut.begin(LogColor.CYAN)
                .append("Output path: file:///")
                .append(path)
                .append(" (")
                .append(file)
                .append(")")
                .out(System.out);

        ColorOut.begin(LogColor.GREEN)
                .append("Build in ")
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
    private static void writeFlight(FlightRecordCollection recordings, @Nullable String path) {
        if (path == null) {
            return;
        }
        try (var filePrintStream = new PrintStream(new FileOutputStream(Path.of(path).toFile()))){
            FlightRecordCollection.print(recordings, false, filePrintStream);
        } catch (FileNotFoundException e) {
            // Just print to console, no need to crash or more verbose logging
            e.printStackTrace();
        }
    }

}
