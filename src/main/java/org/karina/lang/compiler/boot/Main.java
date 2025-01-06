package org.karina.lang.compiler.boot;


import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;



public class Main {
    public static void main(String[] args) throws ParseException {
        BootHelper.printWelcome();

        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var config = CompileConfig.parseArgs(args);
        var fileTree = BootHelper.loadFiles(config.sourceDirectory, collection);
        if (fileTree == null) {
            BootHelper.printDiagnostic(collection, config.printVerbose);
            System.exit(1);
            return;
        }
        BootHelper.printFileTree(fileTree);

        var success = compiler.compile(fileTree, collection);

        if (success) {
            System.out.println("\u001B[33mCompilation successful\u001B[0m");
            System.out.flush();
        } else {
            BootHelper.printDiagnostic(collection, config.printVerbose);
            System.exit(1);
        }

    }


}
