package org.karina.lang.compiler.api;


public class KarinaBootstrap {
//    private boolean printWelcome = false;
//    private boolean printTree = false;
//    private boolean printResult = false;
//    private PrintStream stdOut = System.out;
//    private PrintStream errOut = System.err;
//
//    public KarinaBootstrap setPrintStream(PrintStream stream) {
//        this.stdOut = stream;
//        return this;
//    }
//
//    public KarinaBootstrap setErrPrintStream(PrintStream stream) {
//        this.errOut = stream;
//        return this;
//    }
//
//    public KarinaBootstrap enablePrintWelcome() {
//        this.printWelcome = true;
//        return this;
//    }
//
//    public KarinaBootstrap enablePrintTree() {
//        this.printTree = true;
//        return this;
//    }
//
//    public KarinaBootstrap enablePrintResult() {
//        this.printResult = true;
//        return this;
//    }
//
//
//    public int run(Configuration configuration) throws IOException {
//        if (this.printWelcome) {
//            printWelcome(this.stdOut);
//        }
//        if (this.printResult) {
//            printConfig(configuration, this.stdOut);
//        }
//
//        var compiler = new KarinaDefaultCompiler();
//        var collection = new DiagnosticCollection();
//
//        var fileTree = FileLoader.loadTree(
//                null,
//                configuration.sourceDirectory().toAbsolutePath().normalize().toString(),
//                new FilePredicate()
//        );
//
//        if (this.printTree) {
//            printFileTree(fileTree, this.stdOut);
//        }
//
//        return switch (configuration.target()) {
//            case ConfiguredTarget.InterpreterTarget interpreterTarget -> {
//                yield interpret(
//                        compiler, collection, fileTree, configuration.verbose(),
//                        interpreterTarget
//                );
//            }
//            case ConfiguredTarget.JavaTarget javaTarget -> {
//                yield compile(compiler, collection, fileTree, configuration.verbose(), javaTarget);
//            }
//            case ConfiguredTarget.NoTarget noTarget -> {
//                yield check(compiler, collection, fileTree, configuration.verbose());
//            }
//        };
//    }
//
//
//    private int interpret(
//            KarinaCompiler compiler,
//            DiagnosticCollection collection,
//            FileTreeNode<TextSource> fileTree,
//            boolean verbose,
//            ConfiguredTarget.InterpreterTarget target)
//    {
//
//        var backend = new InterpreterBackend(verbose, this.stdOut);
//
//        var result = compiler.compile(fileTree, collection, backend);
//
//        if (result.isError()) {
//            handleError(collection, verbose);
//            return 1;
//        }
//        if (this.printResult) {
//            this.stdOut.println("\u001B[33mCompilation Successful\u001B[0m");
//        }
//
//        var interpreter = result.asOk().result();
//
//        var library = new SimpleLibrary();
//        library.addToInterpreter(interpreter);
//
//        var function = interpreter.collection().function(target.mainMethod());
//
//        var evalResult = interpreter.eval(function, null, List.of(
//                (Object) (new String[]{"Hello", "World"})
//        ));
//        var evalResultString = Interpreter.toString(evalResult);
//
//        if (this.printResult) {
//            this.stdOut.println("\u001B[35mResult: " + evalResultString + "\u001B[0m");
//            this.stdOut.flush();
//        }
//        return 0;
//    }
//
//    private int compile(
//            KarinaCompiler compiler,
//            DiagnosticCollection collection,
//            FileTreeNode<TextSource> fileTree,
//            boolean verbose,
//            ConfiguredTarget.JavaTarget target) throws IOException
//    {
//
//        var backend = new BytecodeBackend(target.mainClass());
//
//        var result = compiler.compile(fileTree, collection, backend);
//
//        if (result.isError()) {
//            handleError(collection, verbose);
//            return 1;
//        }
//        if (this.printResult) {
//            this.stdOut.println("\u001B[33mCompilation Successful\u001B[0m");
//        }
//        var jar = result.asOk().result();
//
//        jar.dump(target.jarPath());
//        jar.write(target.jarPath());
//
//        if (target.runImmediately()) {
//
//            var builder = new ProcessBuilder("java", "-jar", target.jarPath().toString());
//
//            if (this.printResult) {
//                this.stdOut.println();
//                this.stdOut.println();
//                builder.inheritIO();
//            }
//
//            var process = builder.start();
//            try {
//                process.waitFor();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            if (this.printResult) {
//                this.stdOut.println();
//            }
//            if (this.printResult) {
//                this.errOut.flush();
//                this.stdOut.flush();
//
//                System.err.flush();
//                System.out.flush();
//
//                System.out.println("Process exited with code " + process.exitValue());
//                System.out.flush();
//            }
//
//
//        }
//
//        return 0;
//    }
//
//    private int check(
//            KarinaCompiler compiler,
//            DiagnosticCollection collection,
//            FileTreeNode<TextSource> fileTree,
//            boolean verbose)
//    {
//
//        var result = compiler.compile(fileTree, collection, null);
//
//        if (result.isError()) {
//            handleError(collection, verbose);
//            return 1;
//        }
//        if (this.printResult) {
//            this.stdOut.println("\u001B[33mCompilation Successful\u001B[0m");
//        }
//        return 0;
//    }
//
//
//    private void handleError(DiagnosticCollection collection, boolean verbose) {
//
//        if (this.printResult) {
//            this.stdOut.println("\u001B[31mCompilation failed\u001B[0m");
//            this.stdOut.flush();
//        }
//
//        this.errOut.println();
//        for (var log : collection) {
//            this.errOut.println(log.mkString(verbose));
//        }
//        this.errOut.flush();
//
//    }
//
//    private void printConfig(Configuration configuration, PrintStream stream) {
//        stream.println("\u001B[36m");
//        stream.println("Config ");
//        if (configuration.verbose()) {
//            stream.println("- Verbose");
//        }
//
//        stream.println("- Source: " + configuration.sourceDirectory());
//        stream.print("- Target: ");
//        switch (configuration.target()) {
//            case ConfiguredTarget.InterpreterTarget interpreterTarget -> {
//                stream.println("Interpreter");
//                stream.println("\t- Method: " + interpreterTarget.mainMethod());
//            }
//            case ConfiguredTarget.JavaTarget javaTarget -> {
//                stream.println("Java");
//                stream.println("\t- Path: " + javaTarget.jarPath());
//                stream.println("\t- Main: " + javaTarget.mainClass() + ".class");
//                if (javaTarget.runImmediately()) {
//                    stream.println("\t- Run");
//                }
//            }
//            case ConfiguredTarget.NoTarget noTarget -> {
//                stream.println("None");
//            }
//        }
//
//        stream.println("\u001B[0m");
//        stream.println();
//    }
//
//
//    public static void printWelcome(PrintStream stream) {
//        var welcome_small =
//                """
//                \u001B[34m
//                    _  __
//                   | |/ /  __ _   _ _   _   _ _    __ _
//                   | ' <  / _  | | '_| | | | ' \\  / _  |
//                   |_|\\_\\ \\__,_| |_|   |_| |_||_| \\__,_|
//                \u001B[0m
//                """;
//
//        stream.println(welcome_small);
//    }
//
//    public static void printFileTree(FileTreeNode tree, PrintStream stream) {
//        StringBuilder buffer = new StringBuilder(50);
//        print(tree, buffer, "", "");
//
//        stream.println("\u001B[37m" + buffer + "\u001B[0m");
//    }
//
//    private static void print(FileTreeNode<TextSource> tree, StringBuilder buffer, String prefix, String childrenPrefix) {
//        buffer.append(prefix);
//        buffer.append(tree.name()).append('/');
//        buffer.append('\n');
//
//        for (var it = tree.children().iterator(); it.hasNext();) {
//            var next = it.next();
//            var hasNext = it.hasNext() || !tree.leafs().isEmpty();
//            if (hasNext) {
//                print(next, buffer, childrenPrefix + "|__ ", childrenPrefix + "|   ");
//            } else {
//                print(next, buffer, childrenPrefix + "\\__ ", childrenPrefix + "    ");
//            }
//        }
//
//        for (var it = tree.leafs().iterator(); it.hasNext();) {
//            var next = it.next();
//            if (it.hasNext()) {
//                buffer.append(childrenPrefix).append("|__ ");
//            } else {
//                buffer.append(childrenPrefix).append("\\__ ");
//            }
//            buffer.append(next.name()).append(".krna");
//            buffer.append('\n');
//        }
//
//    }
//
//    @AllArgsConstructor
//    public static class FilePredicate implements Predicate<String> {
//
//        @Override
//        public boolean test(String path) {
//
//            var extension = FileLoader.getFileExtension(path);
//            return extension.equals("krna");
//
//        }
//    }

}
