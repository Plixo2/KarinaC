package org.karina.lang.cli;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CLIParser {

    public PrimaryCommand accept(String[] args) {
        return fromArgs(args);
    }

    private static PrimaryCommand fromArgs(String[] args) {
        if (args.length == 0) {
            return new PrimaryCommand.None();
        }
        var arg = args[0].toLowerCase();
        return switch (arg) {
            case "-h", "--help", "-?" -> new PrimaryCommand.Help();
            case "-v", "--version" -> new PrimaryCommand.Version();

            case "new" -> {
                if (args.length > 2) {
                    yield new PrimaryCommand.UnknownArgument(arg, args[2]);
                } else if (args.length == 1) {
                    yield new PrimaryCommand.MissingArgument(arg, "<project-name>");
                }

                yield new PrimaryCommand.New(args[1]);
            }
            case "compile" -> {
                if (args.length == 1) {
                    yield new PrimaryCommand.MissingArgument(arg, "<project-path>");
                }
                var result = parseOptions(Arrays.copyOfRange(args, 2, args.length));
                yield switch (result) {
                    case OptionsParseResult.Error error -> error.command;
                    case OptionsParseResult.Ok ok -> new PrimaryCommand.Compile(args[1], ok.options);
                };
            }
            case "run" -> {
                var result = parseOptions(Arrays.copyOfRange(args, 1, args.length));
                yield switch (result) {
                    case OptionsParseResult.Error error -> error.command;
                    case OptionsParseResult.Ok ok -> new PrimaryCommand.Run(ok.options);
                };
            }
            default -> new PrimaryCommand.Unknown(arg);
        };

    }

    private static OptionsParseResult parseOptions(String[] args) {
        var options = new CompileOption();

        for (var i = 0; i < args.length; i++) {
            var arg = args[i].toLowerCase();

            switch (arg) {
                case "-l", "--logging" -> {
                    var level = nextArg(args, i);
                    if (level == null) {
                        return new OptionsParseResult.Error(new PrimaryCommand.MissingArgument(arg, "<level>"));
                    }
                    if (options.logging != null) {
                        return new OptionsParseResult.Error(new PrimaryCommand.Error(
                                "Duplicate option: --logging"
                        ));
                    }
                    options.logging = level;
                    i++;
                }
                case "-f", "--flight" -> {
                    var level = nextArg(args, i);
                    if (level == null) {
                        return new OptionsParseResult.Error(new PrimaryCommand.MissingArgument(arg, "<file>"));
                    }
                    if (options.flight != null) {
                        return new OptionsParseResult.Error(new PrimaryCommand.Error(
                                "Duplicate option: --flight"
                        ));
                    }
                    options.flight = level;
                    i++;
                }
                case "-c", "--console" -> {
                    if (options.console) {
                        return new OptionsParseResult.Error(new PrimaryCommand.Error(
                                "Duplicate option: --console"
                        ));
                    }
                    options.console = true;
                }
                default -> {
                    return new OptionsParseResult.Error(new PrimaryCommand.UnknownOption(arg));
                }
            };
        }

        return new OptionsParseResult.Ok(options);
    }

    //returns the next argument or null if there is no next argument
    private static @Nullable String nextArg(String[] args, int index) {
        if (index + 1 >= args.length) {
            return null;
        }
        return args[index + 1];
    }

    public sealed interface PrimaryCommand {
        record New(String name) implements PrimaryCommand {}
        record Run(CompileOption options) implements PrimaryCommand {}
        record Compile(String src, CompileOption options) implements PrimaryCommand {}
        record Version() implements PrimaryCommand {}
        record Help() implements PrimaryCommand {}
        record None() implements PrimaryCommand {}
        record Unknown(String src) implements PrimaryCommand {}
        record UnknownArgument(String command, String src) implements PrimaryCommand {}
        record MissingArgument(String command, String src) implements PrimaryCommand {}
        record UnknownOption(String message) implements PrimaryCommand {}
        record Error(String message) implements PrimaryCommand {}
    }

    public static class CompileOption {
        public @Nullable String logging;
        public @Nullable String flight;
        public boolean console = false;
    }

    private sealed interface OptionsParseResult {
        record Ok(CompileOption options) implements OptionsParseResult {}
        record Error(PrimaryCommand command) implements OptionsParseResult {}
    }
}
