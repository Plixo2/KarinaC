package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Colored;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.logging.ConsoleColor;
import org.karina.lang.compiler.utils.AutoRun;
import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/// Compiler starting point printing messages to the console.
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
                                     .binaryFormat(config.useBinaryFormat())
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


            try(var autoRun = new AutoRun()) {
                if (config.run()) {
                    var result = autoRun.runWithPrints(compilation, true, new String[]{});
                    if (result != null) {
                        // just print errors to the console
                        result.cause().printStackTrace(System.out);
                    }
                }
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

        Colored.begin(ConsoleColor.BLUE)
               .append(welcome_small)
               .println(System.out);
    }


    private static void printStartMessage(DefaultFileTree fileTree, Path sourceDirectory) {
        var fileCount = fileTree.leafCount();
        var fileCountString = fileCount == 1 ? "file" : "files";
        Colored.begin(ConsoleColor.GRAY)
               .append("Compiling '")
               .append(sourceDirectory.toString().replace("\\", "/"))
               .append("' (")
               .append(fileCount)
               .append(" ")
               .append(fileCountString)
               .append(")")
               .println(System.out);
    }


    public static void printVersions() {
        var javaVersion = System.getProperty("java.version", "<unknown java version>");
        var vmName = System.getProperty("java.vm.name", "<unknown vm name>");

        Colored.begin(ConsoleColor.GRAY)
               .append("Karina: ")
               .append(KarinaCompiler.VERSION)
               .println(System.out);

        Colored.begin(ConsoleColor.GRAY)
               .append("Java: ")
               .append(vmName)
               .append(" ")
               .append(javaVersion)
               .println(System.out);
    }

    private static void onError(DiagnosticCollection warnings, DiagnosticCollection errors) {
        Colored.begin(ConsoleColor.RED).append("Build failed").println(System.out);
        System.out.println();
        System.out.flush();

        DiagnosticCollection.print(errors, true, System.err);
    }


    private static void onSuccess(@Nullable Path outputFile, DiagnosticCollection warnings, long deltaTime) {

        if (outputFile != null) {
            var absolutePath = outputFile.toAbsolutePath().normalize();
            var file = absolutePath.getFileName();
            var path = absolutePath.getParent().toString().replace("\\", "/");

            Colored.begin(ConsoleColor.GRAY)
                   .append("Created '")
                   .append(file)
                   .append("'")
                   .append(" in ")
                   .append("file:///")
                   .append(path)
                   .println(System.out);
        }

        Colored.begin(ConsoleColor.GRAY)
               .append("Finished in ")
               .append(deltaTime)
               .append("ms")
               .println(System.out);


        ConsoleColor.YELLOW.out(System.out);
        DiagnosticCollection.print(warnings, true, System.out);
        ConsoleColor.RESET.out(System.out);
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
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (var filePrintStream = new PrintStream(new FileOutputStream(path.toFile()))){
            FlightRecordCollection.print(recordings, false, filePrintStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
