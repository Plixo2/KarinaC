package org.karina.lang.compiler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@AllArgsConstructor
public class CompileConfig {
    public static Options cliOptions = getOptions();

    public String sourceDirectory;
    public @Nullable String outputPath;
    public boolean printVerbose;

    public static CompileConfig parseArgs(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(cliOptions, args);

        var sourceDirectory = cmd.getOptionValue("src");
        var outputPath = cmd.getOptionValue("output");
        var verboseOutput = cmd.hasOption("verbose");

        return new CompileConfig(sourceDirectory, outputPath, verboseOutput);

    }

    private static @NotNull Options getOptions() {
        Options options = new Options();

        var input = new Option("s","src", true, "source directory");
        input.setRequired(true);
        options.addOption(input);

        var output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        var verbose = new Option("v", "verbose", false, "print verbose output");
        verbose.setRequired(false);
        options.addOption(verbose);
        return options;
    }

    private static CompileConfig parseObject(JsonObject object) {
        var src = object.get("src");
        if (src == null) {
            throw new IllegalArgumentException("Missing 'src' field in config");
        }
        var sourceDirectory = src.getAsString();
        var outputPath = object.get("output") == null ? null : object.get("output").getAsString();
        var verbose = object.get("verbose") != null && object.get("verbose").getAsBoolean();
        return new CompileConfig(sourceDirectory, outputPath, verbose);
    }

    /**
     * Predicate for filtering files by extension.
     */
    @AllArgsConstructor
    public static class FilePredicate implements Predicate<String> {

        @Override
        public boolean test(String path) {

            var extension = FileLoader.getFileExtension(path);
            return extension.equals("krna");

        }
    }
}
