package org.karina.lang.compiler;


import org.karina.lang.compiler.stages.generate.CustomClassWriter;

import java.io.*;

///
/// Main entry, runs the hello-world project `resources/src/`
/// @see Config#fromProperties for all project defaults
///
public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments expected, use system properties to configure the compiler.");
        }

        var config = Config.fromProperties();
        var result = ConsoleCompiler.compile(config);

        var exitCode = result ? 0 : 1;
        System.exit(exitCode);



    }

}
