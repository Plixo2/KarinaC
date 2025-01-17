package org.karina.lang.compiler.api.config;

import java.nio.file.Path;

public sealed interface ConfiguredTarget {

    /**
     *
     * @param jarPath absolut path to the source file, including file name and extension
     * @param mainClass main unit, where a main method is defined, e.g. src.Main
     */
    record JavaTarget(
            Path jarPath,
            String mainClass,
            boolean runImmediately
    ) implements ConfiguredTarget { }


    /**
     * @param mainMethod path of the main method to invoke, e.g src.Main.main
     */
    record InterpreterTarget(
            String mainMethod
    ) implements ConfiguredTarget { }

    /**
     * Empty target
     */
    record NoTarget() implements ConfiguredTarget { }
}
