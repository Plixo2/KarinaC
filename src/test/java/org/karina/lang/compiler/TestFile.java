package org.karina.lang.compiler;

import org.karina.lang.compiler.api.Resource;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.boot.DefaultFileTree;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.eval.SimpleLibrary;
import org.karina.lang.eval.Interpreter;

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
        var success = compiler.compile(fileTree, collection);

        if (!success) {
            DiagnosticCollection.printDiagnostic(collection, true);
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
        var success = compiler.compile(fileTree, collection);

        if (success) {
            throw new AssertionError("Expected Fail for '" + this.name + "'");
        } else {
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
    }

    public Object run(String function) {
        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var success = compiler.compile(fileTree, collection);

        if (!success) {
            DiagnosticCollection.printDiagnostic(collection, true);
            throw new AssertionError("Expected success for '" + this.name + "'");
        }

        assert compiler.tree() != null;
        var solver = Interpreter.fromTree(compiler.tree());
        var testLibrary = new TestLibrary();
        testLibrary.addToInterpreter(solver);

        var foundFunction = solver.collection().function(function);

        return solver.eval(foundFunction, null, List.of());
    }

    private record TestResource(String name) implements Resource {

        @Override
        public String identifier() {
            return "test_" + this.name;
        }
    }
}
