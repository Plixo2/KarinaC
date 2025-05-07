package org.karina.lang.compiler;

import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.*;
import java.nio.file.Path;

/**
 * Main entry
 */
public class Main {

    public static void main(String[] args) throws IOException {
        var welcome_small =
                """
                \u001B[34m
                    _  __
                   | |/ /  __ _   _ _   _   _ _    __ _
                   | ' <  / _  | | '_| | | | ' \\  / _  |
                   |_|\\_\\ \\__,_| |_|   |_| |_||_| \\__,_|
                \u001B[0m
                """;
        System.out.println(welcome_small);


        Log.begin("all");


        var sourceDirectory = System.getProperty("karina.source", "resources/src/");
        if (sourceDirectory == null) {
            throw new IllegalStateException("No source directory provided, use -Dkarina.source=<path>");
        }
        var console = System.getProperty("karina.console", "false").equals("true");
        var flight = System.getProperty("karina.flight", "resources/flight.txt");
        var out = System.getProperty("karina.out", "resources/out/build.jar");

        var compiler = new KarinaCompiler(out);

        Log.begin("file-load");
        var fileTree = FileLoader.loadTree(
                Path.of(sourceDirectory).toAbsolutePath().normalize().toString()
        );
        Log.end("file-load");

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();

        var success = compiler.compile(fileTree, errors, warnings, recordings);

        Log.end("all");

        writeFlight(recordings, flight);
        if (console) {
            FlightRecordCollection.printColored(recordings, true, System.out);
        }
        System.out.println();

        if (success) {
            System.out.println("\u001B[32mCompilation Successful\u001B[0m");

            if (out == null) {
                System.out.println("\u001B[33mNo output path specified, use -Dkarina.out=<path>\u001B[0m");
            }

            System.out.flush();
            System.out.println("\u001B[33m");
            DiagnosticCollection.print(warnings, true, System.out);
            System.out.println("\u001B[0m");
            System.out.flush();

            System.exit(0);
        } else {
            System.out.println("\u001B[31mCompilation failed\u001B[0m");

            System.out.flush();
            System.out.println("\u001B[33m");
            DiagnosticCollection.print(warnings, true, System.out);
            System.out.println("\u001B[0m");
            System.out.flush();

            System.err.flush();
            System.err.println();
            DiagnosticCollection.print(errors, true, System.err);
            System.err.flush();

            System.exit(1);
        }



    }

    private static void writeFlight(FlightRecordCollection recordings, String path) {
        try (var filePrintStream = new PrintStream(new FileOutputStream(Path.of(path).toFile()))){
            FlightRecordCollection.print(recordings, false, filePrintStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
