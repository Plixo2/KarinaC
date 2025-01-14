package org.karina.lang.compiler.boot;


import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;



public class Main {
    public static void main(String[] args) {
        BootHelper.printWelcome();

        var compiler = new KarinaDefaultCompiler();
        var collection = new DiagnosticCollection();

        var config = BootHelper.loadCfg(args, collection);
        BootHelper.exitOnNull(config, collection, false);
        assert config != null;
        var fileTree = BootHelper.loadFiles(config.sourceDirectory, collection);
        BootHelper.exitOnNull(fileTree, collection, config.printVerbose);
        BootHelper.printFileTree(fileTree);

        var success = compiler.compile(fileTree, collection, ref -> true);

        if (Boolean.TRUE.equals(success)) {
            System.out.println("\u001B[33mCompilation successful\u001B[0m");
            System.out.flush();
        } else {
            DiagnosticCollection.printDiagnostic(collection, config.printVerbose);
            System.exit(1);
        }

    }


}
