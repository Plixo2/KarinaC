package org.karina.lang.eval;

import lombok.SneakyThrows;
import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.DiagnosticCollection;
import org.karina.lang.compiler.api.KarinaDefaultCompiler;
import org.karina.lang.compiler.boot.BootHelper;

import java.util.List;

public class Evaluate {

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

        var solver = compiler.compile(fileTree, collection, Interpreter::fromTree);

        if (solver == null) {
            DiagnosticCollection.printDiagnostic(collection, config.printVerbose);
            System.exit(1);
        }

        System.out.println("\u001B[33mCompilation successful\u001B[0m");
        System.out.println();


        var library = new SimpleLibrary();
        library.addToInterpreter(solver);

        var function = solver.collection().function("src.Main.main");

        var result = solver.eval(function, null, List.of());
        System.out.println("\u001B[35mResult: "  + Interpreter.toString(result) +  "\u001B[0m");

        System.out.flush();
    }
}
