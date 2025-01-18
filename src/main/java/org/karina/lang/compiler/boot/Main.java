package org.karina.lang.compiler.boot;


import org.karina.lang.compiler.api.*;

import java.io.IOException;
import java.nio.file.Path;


public class Main {

    private static final Path sourceDirectory = Path.of("resources/src/");
    private static final Path buildDir = Path.of("resources/out/build.jar");
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

        var compiler = new KarinaDefaultCompiler(mainClass, buildDir);

        var fileTree = FileLoader.loadTree(
                null,
                sourceDirectory.toAbsolutePath().normalize().toString()
        );

        var collection = new DiagnosticCollection();
        var result = compiler.compile(fileTree, collection);
        if (result) {
            System.out.println("\u001B[33mCompilation Successful\u001B[0m");
            System.exit(0);
        } else {
            System.out.println("\u001B[31mCompilation failed\u001B[0m");
            System.out.flush();
            System.err.println();
            DiagnosticCollection.print(collection, true, System.err);
            System.err.flush();

            System.exit(1);
        }

    }


}
