package org.karina.lang.compiler;

import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.DiagnosticCollection;

import java.util.List;

public class TestFile {

    private final String identifier;

    FileTreeNode<TextSource> fileTree;

    public TestFile(String name, TextSource source) {
        this.identifier = source.resource().identifier();

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(name), name, source);
        this.fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
    }

    public TestFile(String identifier, FileTreeNode<TextSource> fileTree) {
        this.identifier = identifier;
        this.fileTree = fileTree;
    }
    public void expect(boolean expectedResult) {
        expect(expectedResult, false);
    }


    public void run() {
        expect(true, true);
    }

    private void expect(boolean expectedResult, boolean run) {
        var compiler = new KarinaCompiler();
        var collection = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        compiler.setErrorCollection(collection);
        compiler.setWarningCollection(warnings);

        compiler.setOutputFile("resources/out/build.jar");

        var result = compiler.compile(this.fileTree);

        if (expectedResult) {
            if (!result) {
                printErrDiagnostic(collection, true);
                throw new AssertionError("Expected success for '" + this.identifier + "'");
            }
            if (compiler.getJarCompilation() == null) {
                throw new AssertionError("Jar compilation is null for '" + this.identifier + "'");
            }
            if (run) {
                AutoRun.run(compiler.getJarCompilation(), true);
            }
        } else {
            if (result) {
                throw new AssertionError("Expected failure for '" + this.identifier + "'");
            }
        }
    }


    private static void printErrDiagnostic(DiagnosticCollection collection, boolean printVerbose) {
        System.err.println("\u001B[31mCompilation failed\u001B[0m");
        System.err.flush();
        System.err.println();
        for (var log : collection) {
            System.err.println(log.mkString(printVerbose));
        }
        System.err.flush();
    }
}
