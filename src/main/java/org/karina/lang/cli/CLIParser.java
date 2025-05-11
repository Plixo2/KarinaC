package org.karina.lang.cli;

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
            case "-h", "--help", "-?" -> {
                yield new PrimaryCommand.Help();
            }
            case "-v", "--version" -> {
                yield new PrimaryCommand.Version();
            }
            case "new" -> {
                if (args.length > 2) {
                    yield new PrimaryCommand.UnknownArgument(arg, args[2]);
                } else if (args.length == 1) {
                    yield new PrimaryCommand.MissingArgument(arg, "<project-name>");
                }

                yield new PrimaryCommand.New(args[1]);
            }
            case "run" -> {
                if (args.length > 1) {
                    yield new PrimaryCommand.UnknownArgument(arg, args[1]);
                }

                yield new PrimaryCommand.Run();
            }
            case "compile" -> {
                if (args.length > 2) {
                    yield new PrimaryCommand.UnknownArgument(arg, args[2]);
                } else if (args.length == 1) {
                    yield new PrimaryCommand.MissingArgument(arg, "<project-path>");
                }

                yield new PrimaryCommand.Compile(args[1]);
            }
            default -> {
                yield new PrimaryCommand.Unknown(arg);
            }
        };

    }

    public sealed interface PrimaryCommand {
        record New(String name) implements PrimaryCommand {}
        record Run() implements PrimaryCommand {}
        record Compile(String src) implements PrimaryCommand {}
        record Version() implements PrimaryCommand {}
        record Help() implements PrimaryCommand {}
        record None() implements PrimaryCommand {}
        record Unknown(String src) implements PrimaryCommand {}
        record UnknownArgument(String command, String src) implements PrimaryCommand {}
        record MissingArgument(String command, String src) implements PrimaryCommand {}
    }
}
