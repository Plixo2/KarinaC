package org.karina.lang.compiler.api;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.KarinaStage;
import org.karina.lang.compiler.errors.Log;

public class KarinaDefaultCompiler implements KarinaCompiler {


    @Override
    public <T> CompilationResult<T> compile(FileTreeNode files, DiagnosticCollection collection, @Nullable Backend<T> backend) {

        try {
            var parseTree = KarinaStage.parseFiles(files);
            var desugaredTree = KarinaStage.preProcessTree(parseTree);
            var importedTree = KarinaStage.importTree(desugaredTree);
            var attribTree = KarinaStage.attribTree(importedTree);

            CompilationResult<T> result;
            if (backend == null) {
                result = CompilationResult.ok(null);
            } else {
                result =  CompilationResult.ok(backend.accept(attribTree));
            }

            if (Log.hasErrors()) {
                System.err.println("Errors in log, this should not happen");
            }
            return result;
        } catch (Log.KarinaException ignored) {
            if (!Log.hasErrors()) {
                System.err.println("An exception was thrown, but no errors were logged");
            } else {
                collection.addAll(Log.getEntries());
            }
            return CompilationResult.error();
        } finally {
            Log.clearLogs();
        }

    }


}
