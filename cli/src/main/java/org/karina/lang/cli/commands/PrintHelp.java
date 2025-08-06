package org.karina.lang.cli.commands;

import org.karina.lang.compiler.KarinaCompiler;

public class PrintHelp {
    private static final String HELP_MESSAGE =
"""
Karina #ver CLI

Usage:
    karina new <project-name>
        Create a new project with the specified name.
            
    karina compile <project-path> [options]
        Compile the project located in the specified directory.
        The directory must contain a directory named 'src' with the source files.
        
    karina run [options]
        Compile and run the project located in the current directory.

    karina --version, -v
        Display the current version of Karina.

    karina --help, -h, -?
        Display this help message.
        
Options:
    --logging, -l <level>
        Set the logging level. Available levels: none, basic, verbose, verbose_jvm.
        Default is 'basic'.
        
    --flight, -f <file>
        Specify the flight log file. Default is 'build/flight.log'.
        
    --console, -c
        Displays log messages in the console.
    
    --disable-format, -df
        Disable binary format (slower load times).
        
""";


    public static void printHelp() {
        System.out.println(HELP_MESSAGE.replace("#ver", KarinaCompiler.VERSION));
    }
}
