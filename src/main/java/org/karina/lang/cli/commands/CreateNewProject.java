package org.karina.lang.cli.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CreateNewProject {
    private static final String PROJECT_TEMPLATE =
"""
fn main(args: [string]) {
    println("Hello, World!")
}
""";


    /**
     * Returns false if the project could not be created.
     */
    public static boolean createNewProject(String directory) throws IOException {
        directory = directory.replace("\\", "/");
        var path = Path.of(directory);

        if (Files.exists(path)) {
            System.out.println("File/Directory " + directory + " already exists.");
            return false;
        }

        var newDirectory = Files.createDirectories(path);

        var projectName = newDirectory.getFileName();

        if (!createSrcFolder(newDirectory)) {
            return false;
        }

        System.out.println("Created new project '" + projectName + "'");

        return true;

    }

    // Parent should be empty, so no extra checking is needed.
    private static boolean createSrcFolder(Path parent) throws IOException {

        var srcFolder = Files.createDirectory(parent.resolve("src"));

        // could be combined into a single
        // Files.write(..., StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)
        var newFile = Files.createFile(srcFolder.resolve("main.krna"));

        Files.write(newFile, PROJECT_TEMPLATE.getBytes(), StandardOpenOption.WRITE);

        return true;
    }

}
