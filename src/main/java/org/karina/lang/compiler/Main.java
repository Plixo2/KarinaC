package org.karina.lang.compiler;


import java.io.*;

/**
 * Main entry
 */
public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments expected, use system properties to configure the compiler.");
        }

        // Allow automatic cache rebuilding.
        System.setProperty("karina.allowCacheRebuilding", "true");

        var config = Config.fromProperties();
        var result = ConsoleCompiler.compile(config);

        var exitCode = result ? 0 : 1;
        System.exit(exitCode);

    }





}
