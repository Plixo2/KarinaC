package org.karina.lang.compiler.boot;


import org.karina.lang.compiler.api.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.LogBuilder;

import java.io.*;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Main {

    private static final Path sourceDirectory = Path.of("resources/src/");
    private static final Path buildDir = Path.of("resources/out/build.jar");
    private static final Path log = Path.of("resources/flight.txt");
    private static final String mainClass = "src.Main";


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
        var compiler = new KarinaDefaultCompiler(true);

        Log.begin("file-load");
        var fileTree = FileLoader.loadTree(
                sourceDirectory.toAbsolutePath().normalize().toString()
        );
        Log.end("file-load");

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();

        var success = compiler.compile(fileTree, errors, warnings, recordings);

        Log.end("all");

        FlightRecordCollection.printColored(recordings, true, System.out);
        writeFlight(recordings);
        System.out.println();

        if (success) {
            System.out.println("\u001B[32mCompilation Successful\u001B[0m");

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

    private static void writeFlight(FlightRecordCollection recordings) {
        try (var filePrintStream = new PrintStream(new FileOutputStream(log.toFile()))){
            FlightRecordCollection.print(recordings, false, filePrintStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        try (var filePrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(log.toFile())))){
//            FlightRecordCollection.print(recordings, false, filePrintStream);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

}
