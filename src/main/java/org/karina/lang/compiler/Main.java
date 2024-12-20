package org.karina.lang.compiler;


import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.FileTreeNode;

import java.util.function.Predicate;


public class Main {
    public static void main(String[] args) throws ParseException {
        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();
        var config = CompileConfig.parseArgs(args);

        var fileTree = loadFiles(config.sourceDirectory);
        var success = compiler.compile(fileTree, collection);

        if (success) {
            System.out.println("Compilation successful");
        } else {
            for (var log : collection) {
                System.err.println(log.mkString(config.printVerbose));
            }
            System.err.flush();
        }

    }

    private static FileTreeNode loadFiles(String path) {
        return FileLoader.loadTree(null, path, new CompileConfig.FilePredicate());
    }

}
