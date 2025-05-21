package org.karina.lang.compiler;

import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.TextSource;

import java.util.List;

public class TestFile {

    private final String name;
    private final TextSource source;
    private final boolean expectedResult;

    public TestFile(String name, TextSource source, boolean expectedResult) {
        this.expectedResult = expectedResult;
        this.name = name;
        this.source = source;
    }

    public void expect() {
        var compiler = new KarinaCompiler();
        var collection = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        compiler.setErrorCollection(collection);
        compiler.setWarningCollection(warnings);

        var basePath = new ObjectPath("src");
        var node = new DefaultFileTree.DefaultFileNode(basePath.append(this.name), this.name, this.source);
        var fileTree = new DefaultFileTree(basePath, "src", List.of(), List.of(node));
        var result = compiler.compile(fileTree);

        if (this.expectedResult) {
            if (!result) {
                printErrDiagnostic(collection, true);
                throw new AssertionError("Expected success for '" + this.source.resource().identifier() + "'");
            }
        } else {
            if (result) {
                throw new AssertionError("Expected failure for '" + this.source.resource().identifier() + "'");
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
