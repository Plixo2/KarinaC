package org.karina.lang.compiler;

import org.karina.lang.compiler.api.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.interpreter.InterpreterBackend;
import org.karina.lang.compiler.api.DefaultFileTree;
import org.karina.lang.compiler.logging.errors.Error;
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
        var compiler = new KarinaDefaultCompiler("", null);
        var collection = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree, collection, warnings, null);

        if (!result) {
            printDiagnostic(collection, true);
            throw new AssertionError("Expected success for '" + this.name + "'");
        }
    }

    public <T> void expectError(Class<T> errorType) {
        expectError(errorType, "");
    }

    public <T> void expectError(Class<T> errorType, String msg) {
        var compiler = new KarinaDefaultCompiler("", null);
        var collection = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree, collection, warnings, null);

        if (result) {
            throw new AssertionError("Expected Fail for '" + this.name + "'" + " but got success for " + this.source.resource().identifier());
        }

        Log.LogWithTrace lastError = null;
        for (var log : collection) {
            lastError = log;
            if (log.entry().getClass().equals(errorType)) {
                if (msg.isEmpty()) {
                    return;
                }
                if (log.mkString(true).contains(msg)) {
                    return;
                }
                var message = "Expected Fail for '" + this.name + "' with '" + msg +
                        "' for fieldType " + errorType.getSimpleName();
                throw new AssertionError(message);
            }
        }

        String suffix = "";
        if (lastError != null) {

            suffix = lastError.mkString(true);
        }

        var message =
                "Expected Fail for '" + this.name + "' of fieldType '" + errorType.getSimpleName() + "'" +
                        " but got " + (lastError == null ? "no errors" : lastError.getClass().getSimpleName() + " " + suffix);
        throw new AssertionError(message);

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
