package org.karina.lang.cli;

import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
    

    public static void compile(Path project, boolean run) throws IOException {
        project = project.toAbsolutePath().normalize();

        var buildDir = project.resolve("build/");
        var projectStr = project.resolve("src/").normalize().toString();
        var logFile = buildDir.resolve("flight.log").normalize().toString();
        var buildFile = buildDir.resolve("out/build.jar").normalize().toString();

        System.setProperty("karina.source", projectStr);
        System.setProperty("karina.console", "false");
        System.setProperty("karina.flight", logFile);
        System.setProperty("karina.out", buildFile);
        System.setProperty("karina.classes", "true");
        System.setProperty("karina.logging", "basic");


        org.karina.lang.compiler.Main.main(new String[]{});

        putKarinaLib(buildDir);
        putScript(buildDir, WINDOWS_COMMAND, "run.bat");
        putScript(buildDir, LINUX_COMMAND, "run");

        if (run) {
            run();
        }

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


    private static void run() {
        exec("cd build && run");
    }

    private static void exec(String command) {

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            System.exit(exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
