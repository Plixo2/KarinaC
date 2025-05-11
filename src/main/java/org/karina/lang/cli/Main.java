package org.karina.lang.cli;

import org.karina.lang.compiler.KarinaCompiler;

import java.io.IOException;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws IOException {
        var parser = new CLIParser();
        var command = parser.accept(args);

        switch (command) {
            case CLIParser.PrimaryCommand.Help help -> {
                printHelp();
            }
            case CLIParser.PrimaryCommand.None none -> {
                printHelp();
            }
            case CLIParser.PrimaryCommand.Version version -> {
                printVersion();
            }
            case CLIParser.PrimaryCommand.New aNew -> {
                if (!newProject(aNew.name())) {
                    System.exit(-1);
                }
            }
            case CLIParser.PrimaryCommand.Compile compile -> {
                CompileProject.compile(Path.of(compile.src()), false);

            }
            case CLIParser.PrimaryCommand.Run run -> {
                CompileProject.compile(Path.of("."), true);
            }
            case CLIParser.PrimaryCommand.Unknown unknown -> {
                printUnknownCommand(unknown.src());
                System.exit(-1);
            }
            case CLIParser.PrimaryCommand.UnknownArgument unknownArgument -> {
                printUnknownArgument(unknownArgument.command(), unknownArgument.src());
                System.exit(-1);
            }
            case CLIParser.PrimaryCommand.MissingArgument missingArgument -> {
                printMissingArgument(missingArgument.command(), missingArgument.src());
                System.exit(-1);
            }
        }

    }

    /**
     * Returns false if the project could not be created.
     */
    private static boolean newProject(String directory) throws IOException {
        return CreateNewProject.createNewProject(directory);
    }

    private static void printUnknownCommand(String command) {
        System.out.println("Unknown command: " + command);
        System.out.println("Use -h or --help for help.");
    }

    private static void printUnknownArgument(String command, String argument) {
        System.out.println("Command '" + command + "' has unknown argument: " + argument);
        System.out.println("Use -h or --help for help.");
    }

    private static void printMissingArgument(String command, String argument) {
        System.out.println("Command '" + command + "' is missing argument: " + argument);
        System.out.println("Use -h or --help for help.");
    }

    private static void printHelp() {
        PrintHelp.printHelp();
    }

    private static void printVersion() {
        var javaVersion = System.getProperty("java.version", "<unknown java version>");
        var vmName = System.getProperty("java.vm.name", "<unknown vm name>");
        var runtimeVersion = System.getProperty("java.runtime.version", "<unknown runtime version>");
        var vendor = System.getProperty("java.vendor.url", "unknown");

        System.out.println("Karina: " + KarinaCompiler.VERSION);
        System.out.println("Java: " + vmName + " " + javaVersion);
        System.out.println("      " + runtimeVersion);
        System.out.println("      " + vendor);

    }

}
