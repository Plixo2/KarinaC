package org.karina.lang.compiler;

import org.karina.lang.compiler.api.*;
import org.karina.lang.compiler.backend.interpreter.InterpreterBackend;
import org.karina.lang.compiler.api.DefaultFileTree;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.interpreter.Interpreter;

import java.util.List;

public class TestFile {

    public final String name;
    public final TextSource source;
    public TestFile(String name, String content) {
        this.name = name;
        this.source = new TextSource(new TestResource(name), content.lines().toList());
    }

    public TestFile(String name, TextSource source) {
        this.name = name;
        this.source = source;
    }

    public void expect() {
        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree, collection, null);

        if (result.isError()) {
            printDiagnostic(collection, true);
            throw new AssertionError("Expected success for '" + this.name + "'");
        }
    }

    public <T> void expectError(Class<T> errorType) {
        expectError(errorType, "");
    }

    public <T> void expectError(Class<T> errorType, String msg) {
        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree, collection, null);

        switch (result) {
            case CompilationResult.Error<?> v -> {
                Error lastError = null;
                for (var log : collection) {
                    lastError = log.entry();
                    if (log.entry().getClass().equals(errorType)) {
                        if (msg.isEmpty()) {
                            return;
                        }
                        if (log.mkString(true).contains(msg)) {
                            return;
                        }
                        var message = "Expected Fail for '" + this.name + "' with '" + msg +
                                "' for type " + errorType.getSimpleName();
                        throw new AssertionError(message);
                    }
                }

                var message =
                        "Expected Fail for '" + this.name + "' of type '" + errorType.getSimpleName() + "'" +
                                " but got " + (lastError == null ? "no errors" : lastError.getClass().getSimpleName());
                throw new AssertionError(message);
            }
            case CompilationResult.OK<?> v -> {
                throw new AssertionError("Expected Fail for '" + this.name + "'");
            }
        }

    }

    public Object run(String function) {
        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree, collection, new InterpreterBackend(true, System.out));

        switch (result) {
            case CompilationResult.Error<Interpreter> v -> {
                printDiagnostic(collection, true);
                throw new AssertionError("Expected success for '" + this.name + "'");
            }
            case CompilationResult.OK(var solver) -> {
                var testLibrary = new TestLibrary();
                testLibrary.addToInterpreter(solver);
                var foundFunction = solver.collection().function(function);
                return solver.eval(foundFunction, null, List.of());
            }
        }

    }

    private static void printDiagnostic(DiagnosticCollection collection, boolean printVerbose) {
        System.out.println("\u001B[31mCompilation failed\u001B[0m");
        System.out.flush();
        System.err.println();
        for (var log : collection) {
            System.err.println(log.mkString(printVerbose));
        }
        System.err.flush();
    }

    private record TestResource(String name) implements Resource {

        @Override
        public String identifier() {
            return "test_" + this.name;
        }
    }
}
