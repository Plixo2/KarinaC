package org.karina.lang.cli;

public class PrintHelp {
    private static final String HELP_MESSAGE =
"""
Karina Command Line Interface (CLI) - Help

Usage:
    karina <command> [options]

Available commands and options:
    new <project-name>      Create a new project.
                            This will initialize a new project with a simple Hello World program.

    compile <project-path>  Compile the project located in the specified directory into a JAR file.
                            The directory must contain the necessary source files for compilation.

    run                     Run the current project. It will automatically look for the 'src'
                            directory, compile and execute it.

    --version, -v           Display the current version of Karina CLI.

    --help, -h, -?          Display this help message and usage details.

""";



    public static void printHelp() {
        System.out.println(HELP_MESSAGE);
    }
}
