package org.karina.lang.cli;

import org.karina.lang.cli.commands.CompileProject;
import org.karina.lang.compiler.ConsoleCompiler;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Main class for the Karina CLI.
 * <p>
 * This class is responsible for parsing command line arguments and executing the appropriate commands.
 * It also handles errors and prints help messages.
 */
public class Main {


    public static void main(String[] args) throws IOException {
        var parser = new org.karina.lang.cli.CLIParser();
        var command = parser.accept(args);

        switch (command) {
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Help help -> {
                printHelp();
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.None none -> {
                printHelp();
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Version version -> {
                ConsoleCompiler.printVersions();
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.New aNew -> {
                if (!newProject(aNew.name())) {
                    System.exit(-1);
                }
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Compile compile -> {
                CompileProject.compile(Path.of(compile.src()), false, compile.options());
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Run run -> {
                CompileProject.compile(Path.of("."), true, run.options());
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Unknown unknown -> {
                printUnknownCommand(unknown.src());
                System.exit(-1);
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.UnknownArgument unknownArgument -> {
                printUnknownArgument(unknownArgument.command(), unknownArgument.src());
                System.exit(-1);
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.MissingArgument missingArgument -> {
                printMissingArgument(missingArgument.command(), missingArgument.src());
                System.exit(-1);
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.UnknownOption unknownOption -> {
                printUnknownOption(unknownOption.message());
                System.exit(-1);
            }
            case org.karina.lang.cli.CLIParser.PrimaryCommand.Error error -> {
                System.out.println(error.message());
                System.out.println("Use -h or --help for help.");
                System.exit(-1);
            }
        }

    }

    /**
     * Returns false if the project could not be created.
     */
    private static boolean newProject(String directory) throws IOException {
        return org.karina.lang.cli.commands.CreateNewProject.createNewProject(directory);
    }

    private static void printUnknownOption(String command) {
        System.out.println("Unknown option: " + command);
        System.out.println("Use -h or --help for help.");
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
        org.karina.lang.cli.commands.PrintHelp.printHelp();
    }

}
