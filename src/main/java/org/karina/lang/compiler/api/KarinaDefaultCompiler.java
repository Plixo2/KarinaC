package org.karina.lang.compiler.api;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.KarinaStage;
import org.karina.lang.compiler.stages.parser.KarinaUnitParser;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.preprocess.Preprocessor;

import java.util.ArrayList;
import java.util.List;

public class KarinaDefaultCompiler implements KarinaCompiler {


    @Override
    public <T> @Nullable T compile(FileTreeNode files, DiagnosticCollection collection, @Nullable Backend<T> backend) {

        try {
            KTree.KPackage parseTree;
            try (var errorCollection = new ErrorCollector()) {
                parseTree = parseFiles(files, errorCollection);
            }
            var desugaredTree = KarinaStage.preProcessTree(parseTree);
            var importedTree = KarinaStage.importTree(desugaredTree);
            var attribTree = KarinaStage.attribTree(importedTree);
            if (backend != null) {
                return backend.accept(attribTree);
            }

            if (Log.hasErrors()) {
                System.err.println("Errors in log, this should not happen");
            }
            return null;
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

    /**
     * Parses a file tree into a {@link KTree}.
     * @param collector to collect ALL errors while parsing
     */
    private KTree.KPackage parseFiles(FileTreeNode fileTree, ErrorCollector collector) {

        List<KTree.KUnit> units = new ArrayList<>();
        for (var files : fileTree.leafs()) {
            collector.collect(() -> {
                units.add(KarinaUnitParser.generateParseTree(
                        files.text(),
                        files.name(),
                        files.path()
                ));
            });
        }
        var subPackages = fileTree.children().stream().map(ref -> parseFiles(ref, collector)).toList();
        return new KTree.KPackage(fileTree.path(), fileTree.name(), subPackages, units);

    }


}
