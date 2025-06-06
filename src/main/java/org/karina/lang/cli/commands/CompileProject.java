package org.karina.lang.cli.commands;

import org.karina.lang.cli.CLIParser;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Compiles a Karina project from the command line.
 * Creates a build directory with the compiled classes,
 * the compiled jar file, the standard library
 * and a run script for linux and windows.
 */
public class CompileProject {

    private static final String WINDOWS_COMMAND =
"""
@echo off
pushd "%~dp0"
java -cp "out/build.jar;libs/karina_base.jar" main
popd
""";

    private static final String LINUX_COMMAND =
"""
#!/bin/bash

pushd "$(dirname "$0")" > /dev/null
java -cp "out/build.jar:libs/karina_base.jar" main
popd > /dev/null
""";
    
    /**
     * Compiles a Karina project.
     * @param project the path to the project, compiles everything in the 'src' directory, that has to exist
     * @param run if true, runs the program after compiling
     * @param compileOption compile options
     * @throws IOException an I/O error occurs when loading of the src directory fails
     *                      or the karina_base.jar or build scripts cannot be copied.
     */
    public static void compile(Path project, boolean run, CLIParser.CompileOption compileOption) throws IOException {
        project = project.toAbsolutePath().normalize();

        var buildDir = project.resolve("build/");
        var projectStr = project.resolve("src/").normalize().toString();

        var logFile = buildDir.resolve("flight.log").normalize().toString();
        var buildFile = buildDir.resolve("out/build.jar").normalize().toString();

        // System properties for the compiler
        System.setProperty("karina.source", projectStr);
        System.setProperty("karina.out", buildFile);
        System.setProperty("karina.classes", "true");
        System.setProperty("karina.cli", "true");

        System.setProperty("karina.flight", Objects.requireNonNullElse(compileOption.flight, logFile));
        System.setProperty("karina.logging", Objects.requireNonNullElse(compileOption.logging, "basic"));
        System.setProperty("karina.console", Boolean.toString(compileOption.console));
        System.setProperty("karina.binary", Boolean.toString(compileOption.binary));


        // "--run" is a flag to run the program after compiling
        String[] args;
        if (run) {
            args = new String[]{"--run"};
        } else {
            args = new String[]{};
        }

        // main compile step, does exit on error
        org.karina.lang.compiler.Main.main(args);

        // copy the karina standard library
        putKarinaLib(buildDir);

        // copy build scripts
        putScript(buildDir, WINDOWS_COMMAND, "run.bat");
        putScript(buildDir, LINUX_COMMAND, "run");


    }

    private static void putScript(Path buildDir, String content, String file) throws IOException {
        var script = buildDir.resolve(file);

        Files.write(script, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

    }

    private static void putKarinaLib(Path buildDir) throws IOException {
        var jarDest = buildDir.resolve("libs/karina_base.jar");

        Path destinationDir = jarDest.getParent();
        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        try (var resourceStream = ModelLoader.class.getResourceAsStream("/karina_base.jar")) {
            if (resourceStream == null) {
                System.out.println("Could not find karina_base.jar");
                System.exit(1);
            }

            try (var outputStream = new FileOutputStream(jarDest.toFile())){
                outputStream.write(resourceStream.readAllBytes());
            }
        }
    }
}
