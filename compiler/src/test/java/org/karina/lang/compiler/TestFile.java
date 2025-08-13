package org.karina.lang.compiler;

import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.*;

import java.nio.file.Path;
import java.util.List;

public class TestFile {

    private final String identifier;

    FileTreeNode fileTree;

    public TestFile(String name, TextSource source) {
        this.identifier = source.resource().identifier();

        var node = new DefaultFileTree.DefaultFileNode(new ObjectPath(name), name, source);
        this.fileTree = new DefaultFileTree(null, "src", List.of(), List.of(node));
    }

    public TestFile(String identifier, FileTreeNode fileTree) {
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
        var collection = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var compiler = KarinaCompiler.builder()
                .errorCollection(collection)
                .warningCollection(warnings)
                .setOutputFile(Path.of("resources/out/build.jar"))
                .build();


        var compilation = compiler.compile(this.fileTree);

        if (expectedResult) {
            if (compilation == null) {
                printErrDiagnostic(collection, true);
                throw new AssertionError("Expected success for '" + this.identifier + "'");
            }
            if (run) {
                AutoRun.runWithPrints(compilation, true);
            }
        } else {
            if (compilation != null) {
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
