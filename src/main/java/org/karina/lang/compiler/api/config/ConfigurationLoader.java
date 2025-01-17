package org.karina.lang.compiler.api.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationLoader {
    private static final CommandLineParser CLI_PARSER = new DefaultParser();
    private static final Options CLI_OPTIONS;

    private static final String SRC_KEY = "src";

    private static final String EVAL_KEY = "eval";

    private static final String CONFIG_KEY = "config";

    private static final String OUTPUT_KEY = "output";

    private static final String MAIN_KEY = "main";

    private static final String VERBOSE_KEY = "verbose";

    private static final String HELP_KEY = "help";

    private static final String RUN_KEY = "run";

    static {
        CLI_OPTIONS = new Options();

        var input = Option.builder();
        input.longOpt(SRC_KEY);
        input.desc("Set the source directory");
        input.hasArg(true);
        input.argName("directory");
        input.required(false);
        CLI_OPTIONS.addOption(input.build());

        var eval = Option.builder();
        eval.longOpt(EVAL_KEY);
        eval.desc("Evaluate the current program");
        eval.hasArg(true);
        eval.argName("main function");
        eval.required(false);
        CLI_OPTIONS.addOption(eval.build());

        var config = Option.builder();
        config.longOpt(CONFIG_KEY);
        config.desc("Load a configuration file");
        config.hasArg(true);
        config.argName("file");
        config.required(false);
        CLI_OPTIONS.addOption(config.build());

        var output = Option.builder();
        output.longOpt(OUTPUT_KEY);
        output.desc("Set the Output path");
        output.hasArg(true);
        output.argName("file");
        output.required(false);
        CLI_OPTIONS.addOption(output.build());

        var main = Option.builder();
        main.longOpt(MAIN_KEY);
        main.desc("Set the Main class, e.g. src.Main");
        main.hasArg(true);
        main.argName("class");
        main.required(false);
        CLI_OPTIONS.addOption(main.build());

        var verbose = Option.builder();
        verbose.longOpt(VERBOSE_KEY);
        verbose.desc("Enable verbose output");
        verbose.hasArg(false);
        verbose.required(false);
        CLI_OPTIONS.addOption(verbose.build());

        var run = Option.builder();
        run.longOpt(RUN_KEY);
        run.desc("run the jar file immediately");
        run.hasArg(false);
        run.required(false);
        CLI_OPTIONS.addOption(run.build());

        var help = Option.builder();
        help.longOpt(HELP_KEY);
        help.option("h");
        help.desc("Print this help message");
        help.hasArg(false);
        help.required(false);
        CLI_OPTIONS.addOption(help.build());

    }

    /*
     * All paths are relative to the jar file, if not absolute.
     * Note: The program could exit when help is requested.
     */
    public static Configuration fromCommandLineArgs(String[] args) throws ConfigurationParseException {
        try {
            return fromCommandLine(args);
        } catch (ParseException e) {
            printCommandLineHelp(new PrintWriter(System.out));
            throw new ConfigurationParseException(e.getMessage());
        }
    }

    private static Configuration fromCommandLine(String[] args) throws ParseException, ConfigurationParseException {
        var cmd = CLI_PARSER.parse(CLI_OPTIONS, args);
        validate(cmd);

        if (cmd.hasOption(HELP_KEY)) {
            printCommandLineHelp(new PrintWriter(System.out));
            System.exit(0);
        }

        var definedConfig = cmd.hasOption(CONFIG_KEY);
        var definedOutput = cmd.hasOption(OUTPUT_KEY);
        var definedEval = cmd.hasOption(EVAL_KEY);

        if (definedConfig) {
            var path = Paths.get(cmd.getOptionValue(CONFIG_KEY)).toAbsolutePath().normalize();
            return fromDirectory(path);
        } else {
            boolean verboseOutput = cmd.hasOption(VERBOSE_KEY);
            String sourceDirectory = cmd.getOptionValue(SRC_KEY);

            var builder = Configuration.builder();
            builder.verbose(verboseOutput);
            builder.sourceDirectory(Paths.get(sourceDirectory).toAbsolutePath().normalize());

            if (definedOutput) {

                var main = cmd.getOptionValue(MAIN_KEY);
                var output = cmd.getOptionValue(OUTPUT_KEY);
                var run = cmd.hasOption(RUN_KEY);
                builder.target(new ConfiguredTarget.JavaTarget(Paths.get(output).toAbsolutePath().normalize(), main, run));

            } else if (definedEval) {

                var evalFunction = cmd.getOptionValue(EVAL_KEY);
                builder.target(new ConfiguredTarget.InterpreterTarget(evalFunction));

            } else {
                //no target
                builder.target(new ConfiguredTarget.NoTarget());

            }
            return builder.build();
        }
    }

    private static void validate(CommandLine cmd) throws ConfigurationParseException {
        var definedSrc = cmd.hasOption(SRC_KEY);
        var definedEval = cmd.hasOption(EVAL_KEY);
        var definedConfig = cmd.hasOption(CONFIG_KEY);
        var definedOutput = cmd.hasOption(OUTPUT_KEY);
        var definedMain = cmd.hasOption(MAIN_KEY);
        var definedVerbose = cmd.hasOption(VERBOSE_KEY);
        var definedHelp = cmd.hasOption(HELP_KEY);
        var definedRun = cmd.hasOption(RUN_KEY);

        if (definedHelp) {
            return;
        }

        if (definedConfig && definedSrc) {
            throw new ConfigurationParseException("Cannot specify both source directory and config file");
        } else if (!definedConfig && !definedSrc) {
            throw new ConfigurationParseException("Missing source directory or config file");
        }

        if (definedConfig) {
            if (definedOutput) {
                throw new ConfigurationParseException("Cannot specify output file with config file");
            } else if (definedVerbose) {
                throw new ConfigurationParseException("Cannot specify verbose output with config file");
            } else if (definedMain) {
                throw new ConfigurationParseException("Cannot specify main class with config file");
            } else if (definedEval) {
                throw new ConfigurationParseException("Cannot specify eval function with config file");
            } else if (definedRun) {
                throw new ConfigurationParseException("Cannot specify run with config file");
            }

        } else if (definedOutput)  {

            if (definedEval) {
                throw new ConfigurationParseException("Cannot specify both output file and eval function");
            } else if (!definedMain) {
                throw new ConfigurationParseException("Missing main class");
            }

        } else if (definedEval) {
            if (definedMain) {
                throw new ConfigurationParseException("Cannot specify main class with eval function");
            } else if (definedRun) {
                throw new ConfigurationParseException("Cannot specify run with eval function");
            }
        }

    }

    private static JsonElement loadJsonElementFromPath(Path path) throws ConfigurationParseException {
        var folder = new File(path.toAbsolutePath().normalize().toString()).getAbsoluteFile();
        var file = new File(folder, "karina-build.json");
        if (!file.exists()) {
            throw new ConfigurationParseException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new ConfigurationParseException("Not a file: " + file);
        }
        if (!file.canRead()) {
            throw new ConfigurationParseException("Cannot read file: " + file);
        }
        var charset = StandardCharsets.UTF_8;
        try {
            var content = Files.readString(file.toPath(), charset);
            return JsonParser.parseString(content);
        } catch (Exception e) {
            throw new ConfigurationParseException(e.getMessage());
        }
    }

    public static Configuration fromDirectory(Path path) throws ConfigurationParseException {
        var jsonElement = loadJsonElementFromPath(path);
        return fromJsonElement(jsonElement, path);
    }

    /**
     * @param path absolut path to the configuration file
     */
    public static Configuration fromJsonElement(JsonElement element, Path path) throws ConfigurationParseException {
        var builder = Configuration.builder();


        var gson = new Gson();
        JsonConfig jsonConfig;
        try {
            jsonConfig = gson.fromJson(element, JsonConfig.class);
        } catch(Exception e) {
            throw new ConfigurationParseException(e.getMessage());
        }

        if (jsonConfig.build == null) {
            throw new ConfigurationParseException("Missing build section");
        }
        if (jsonConfig.build.src == null) {
            throw new ConfigurationParseException("Missing source directory");
        }

        var absolutSrc =
                Paths.get(path.toString(), jsonConfig.build.src).toAbsolutePath().normalize();
        builder.sourceDirectory(absolutSrc);

        builder.verbose(jsonConfig.verbose);

        if (jsonConfig.eval != null) {
            if (jsonConfig.build.main != null) {
                throw new ConfigurationParseException("Cannot specify main class with eval function");
            } else if (jsonConfig.build.output != null) {
                throw new ConfigurationParseException("Cannot specify output file with eval function");
            } else if (jsonConfig.run) {
                throw new ConfigurationParseException("Cannot specify run with eval function");
            }
            builder.target(new ConfiguredTarget.InterpreterTarget(jsonConfig.eval));

        } else if (jsonConfig.build.output != null) {
            if (jsonConfig.build.main == null) {
                throw new ConfigurationParseException("Missing main class");
            }
            boolean run = jsonConfig.run;
            var absolutJar =
                    Paths.get(path.toString(), jsonConfig.build.output).toAbsolutePath().normalize();
            builder.target(new ConfiguredTarget.JavaTarget(absolutJar, jsonConfig.build.main, run));
        }

        return builder.build();
    }


    public static void printCommandLineHelp(PrintWriter printWriter) {
        HelpFormatter formatter = new HelpFormatter();
        var cmdLineSyntax = "karina";
        formatter.printHelp(printWriter,
                formatter.getWidth(),
                cmdLineSyntax,
                null, CLI_OPTIONS,
                formatter.getLeftPadding(),
                formatter.getDescPadding(),
                null,
                false);
        printWriter.flush();
    }


    private static class JsonConfig {
        JsonBuild build;
        boolean verbose;
        String eval;
        boolean run;
    }
    private static class JsonBuild {
        String src;
        String output;
        String main;
    }
}
