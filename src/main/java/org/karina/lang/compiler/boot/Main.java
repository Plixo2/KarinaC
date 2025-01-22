package org.karina.lang.compiler.boot;


import org.karina.lang.compiler.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public class Main {

    private static final Path sourceDirectory = Path.of("resources/src/");
    private static final Path buildDir = Path.of("resources/out/build.jar");
    private static final String mainClass = "src.Main";

    private static final List<String> prelude = List.of(
            "java/lang/Object",
            "java/lang/System",
            "java/lang/String",
            "java/lang/Integer",
            "java/lang/Long",
            "java/lang/Short",
            "java/lang/Byte",
            "java/lang/Character",
            "java/lang/Boolean",
            "java/lang/Double",
            "java/lang/Float",
            "java/lang/Class"
    );

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

        var compiler = new KarinaDefaultCompiler(mainClass, buildDir);

        var fileTree = FileLoader.loadTree(
                null,
                sourceDirectory.toAbsolutePath().normalize().toString()
        );

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var result = compiler.compile(fileTree, errors, warnings);
        if (result) {
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
            DiagnosticCollection.print(warnings, true, System.err);
            System.out.println("\u001B[0m");
            System.out.flush();

            System.err.println();
            DiagnosticCollection.print(errors, true, System.err);
            System.err.flush();

            System.exit(1);
        }

    }


}
