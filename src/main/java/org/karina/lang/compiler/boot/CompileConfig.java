package org.karina.lang.compiler.boot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.FileLoadError;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Predicate;

@AllArgsConstructor
public class CompileConfig {
    public static Options cliOptions = getOptions();

    public String sourceDirectory;
    public @Nullable String outputPath;
    public boolean printVerbose;

    public static CompileConfig parseArgs(String[] args)
            throws ParseException, Log.KarinaException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(cliOptions, args);

        var sourceDirectory = cmd.getOptionValue("src");
        var configFile = cmd.getOptionValue("config");

        if (configFile == null && sourceDirectory == null) {
            throw new ParseException("Missing source directory or config file");
        } else if (configFile != null && sourceDirectory != null) {
            throw new ParseException("Cannot specify both source directory and config file");
        }

        if (configFile != null) {
            if (cmd.hasOption("output")) {
                throw new ParseException("Cannot specify output file with config file");
            } else if (cmd.hasOption("verbose")) {
                throw new ParseException("Cannot specify verbose output with config file");
            }


            return parseObject(configFile);
        } else {
            var outputPath = cmd.getOptionValue("output");
            var verboseOutput = cmd.hasOption("verbose");

            return new CompileConfig(sourceDirectory, outputPath, verboseOutput);
        }

    }

    private static @NotNull Options getOptions() {
        Options options = new Options();

        var input = new Option("s","src", true, "source directory");
        input.setRequired(false);
        options.addOption(input);

        var configFile = new Option("c", "config", true, "config file");
        configFile.setRequired(false);
        options.addOption(configFile);

        var output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        var verbose = new Option("v", "verbose", false, "print verbose output");
        verbose.setRequired(false);
        options.addOption(verbose);
        return options;
    }

    private static CompileConfig parseObject(String path) {
        var folder = new File(path).getAbsoluteFile();
        var file = new File(folder, "karina-build.json");
        var content = FileLoader.loadUTF8FiletoString(file);

        var element = JsonParser.parseString(content);
        if (!(element instanceof JsonObject object)) {
            Log.fileError(new FileLoadError.InvalidJson(
                    file,
                    "Expected object, got " + element.getClass().getSimpleName()
            ));
            throw new Log.KarinaException();
        }
        var buildContent = object.get("build");
        if (!(buildContent instanceof JsonObject buildObject)) {
            Log.fileError(new FileLoadError.InvalidJson(
                    file,
                    "Expected 'build' object"
            ));
            throw new Log.KarinaException();
        }
        if (!(buildObject.get("src") instanceof JsonElement src)) {
            Log.fileError(new FileLoadError.InvalidJson(
                    file,
                    "Missing 'src' string field in build object"
            ));
            throw new Log.KarinaException();
        }

        String srcPath = src.getAsString();
        String outputPath = null;
        boolean verbose = false;

        if (buildObject.has("output")) {
            if (!(buildObject.get("output") instanceof JsonPrimitive output)) {
                Log.fileError(new FileLoadError.InvalidJson(
                        file,
                        "Expected 'output' field to be a string"
                ));
                throw new Log.KarinaException();
            }
            outputPath = output.getAsString();
        }
        if (object.has("verbose")) {
            if (!(object.get("verbose") instanceof JsonPrimitive verboseElement)) {
                Log.fileError(new FileLoadError.InvalidJson(
                        file,
                        "Expected 'verbose' field to be a boolean"
                ));
                throw new Log.KarinaException();
            }
            verbose = verboseElement.getAsBoolean();
        }

        //make absolut starting from the same parent folder as the config
        var absolutePath = Paths.get(folder.toString(), srcPath).toAbsolutePath().toString();

        return new CompileConfig(absolutePath, outputPath, verbose);
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
