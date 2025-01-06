package org.karina.lang.compiler.boot;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.errors.Log;

public class BootHelper {

    public static void printWelcome() {
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
    }


    public static void printDiagnostic(DiagnosticCollection collection, boolean printVerbose) {
        System.out.println("\u001B[31mCompilation failed\u001B[0m");
        System.out.flush();
        System.err.println();
        for (var log : collection) {
            System.err.println(log.mkString(printVerbose));
        }
        System.err.flush();
    }


    public static @Nullable FileTreeNode loadFiles(String path, DiagnosticCollection collection) {

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



    public static void printFileTree(FileTreeNode tree) {
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
