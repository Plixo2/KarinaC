package org.karina.lang.eval;

import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;
import org.karina.lang.compiler.boot.BootHelper;
import org.karina.lang.compiler.boot.CompileConfig;

import java.util.List;

public class Evaluate {
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

        if (!success) {
            BootHelper.printDiagnostic(collection, config.printVerbose);
            System.exit(1);
        }

        System.out.println("\u001B[33mCompilation successful\u001B[0m");

        System.out.println();

        var solver = Solver.fromTree(compiler.tree());
        BuildIns.addToSolver(solver);

        var function = solver.collection().function("src.Main.main");

        var result = solver.enterFunction(function, null, List.of());
        System.out.println("\u001B[35mResult: "  + result +  "\u001B[0m");

        System.out.flush();
    }
}
