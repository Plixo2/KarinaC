package org.karina.lang.compiler.api;

import org.karina.lang.compiler.DebugWriter;
import org.karina.lang.compiler.parser.KarinaUnitParser;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;

import java.util.ArrayList;
import java.util.List;

public class KarinaDefaultCompiler implements KarinaCompiler {
    @Override
    public boolean compile(FileTreeNode files, DiagnosticCollection collection) {

        try {
            KTree.KPackage parseTree;
            try (var errorCollection = new ErrorCollector()) {
                parseTree = parseFiles(files, errorCollection);
            }
            var importedTree = importTree(parseTree);
            var attributedTree = attributeTree(importedTree);

            DebugWriter.write(parseTree, "resources/raw.json");
            DebugWriter.write(importedTree, "resources/imported.json");
            DebugWriter.write(attributedTree, "resources/attributed.json");

            if (Log.hasErrors()) {
                System.err.println("Errors in log, this should not happen");
            }
            return true;
        } catch (Log.KarinaException ignored) {
            if (!Log.hasErrors()) {
                System.out.println("An exception was thrown, but no errors were logged");
            } else {
                collection.addAll(Log.getEntries());
            }
            return false;
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

    /*
     * Imports the tree, resolving all imports, build symbol table
     */
    private KTree.KPackage importTree(KTree.KPackage kPackage) {
        return new ImportResolver().importTree(kPackage);
    }

    /**
     * Attributing the tree, resolving all types
     */
    private KTree.KPackage attributeTree(KTree.KPackage tree) {
        return new AttributionResolver().attribTree(tree);
    }

}
