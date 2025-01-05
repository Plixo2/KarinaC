package org.karina.lang.compiler.boot;


import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;

import javax.swing.tree.TreeNode;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


public class Main {
    public static void main(String[] args) throws ParseException {
        var welcome_small =
                """
                \u001B[34m
                    _  __
                   | |/ /  __ _   _ _   _   _ _    __ _
                   | ' <  / _  | | '_| | | | ' \\  / _  |
                   |_|\\_\\ \\__,_| |_|   |_| |_||_| \\__,_|
                \u001B[0m
                """;

        System.out.println(welcome_small);

        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var config = CompileConfig.parseArgs(args);
        var fileTree = loadFiles(config.sourceDirectory, collection);
        if (fileTree == null) {
            printDiagnostic(collection, config.printVerbose);
            System.exit(1);
            return;
        }
        printFileTree(fileTree);

        var success = compiler.compile(fileTree, collection);

        if (success) {
            System.out.println("\u001B[33mCompilation successful\u001B[0m");
            System.out.flush();
        } else {
            printDiagnostic(collection, config.printVerbose);
            System.exit(1);
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

    private static FileTreeNode loadFiles(String path, DiagnosticCollection collection) {

        try {
            var fileTree = FileLoader.loadTree(
                    null,
                    path,
                    new CompileConfig.FilePredicate()
            );
            if (Log.hasErrors()) {
                System.err.println("Errors in log, this should not happen");
            }
            return fileTree;
        } catch (Log.KarinaException ignored) {
            if (!Log.hasErrors()) {
                System.out.println("An exception was thrown, but no errors were logged");
            } else {
                collection.addAll(Log.getEntries());
            }
            return null;
        } finally {
            Log.clearLogs();
        }

    }

    private static void printFileTree(FileTreeNode tree) {
        StringBuilder buffer = new StringBuilder(50);
        print(tree, buffer, "", "");

        System.out.println("\u001B[37m" + buffer + "\u001B[0m");
    }

    private static void print(FileTreeNode tree, StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(tree.name()).append('/');
        buffer.append('\n');

        for (var it = tree.children().iterator(); it.hasNext();) {
            var next = it.next();
            var hasNext = it.hasNext() || !tree.leafs().isEmpty();
            if (hasNext) {
                print(next, buffer, childrenPrefix + "|__ ", childrenPrefix + "|   ");
            } else {
                print(next, buffer, childrenPrefix + "\\__ ", childrenPrefix + "    ");
            }
        }

        for (var it = tree.leafs().iterator(); it.hasNext();) {
            var next = it.next();
            if (it.hasNext()) {
                buffer.append(childrenPrefix).append("|__ ");
            } else {
                buffer.append(childrenPrefix).append("\\__ ");
            }
            buffer.append(next.name() + ".krna");
            buffer.append('\n');
        }

    }

}
