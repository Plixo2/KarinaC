package org.karina.lang.compiler;

import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.parser.KarinaVisitor;
import org.karina.lang.compiler.stages.imports.ImportResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Main compiler class, handling everything from file loading to code generation.
 * @see FileTreeNode
 * @see KarinaVisitor
 * @see ImportResolver
 */
public class KarinaC {

    void compile(CompileConfig config) throws Log.KarinaException {

        var fileTree = loadFiles(config.sourceDirectory, config.fileFilter);

        KTree.KPackage parseTree;
        try (var errorCollection = new ErrorCollector()) {
            parseTree = parseFiles(fileTree, errorCollection);
        }
        var importedTree = importTree(parseTree);

        DebugWriter.write(parseTree, "resources/raw.json");
        DebugWriter.write(importedTree, "resources/imported.json");

    }

    /**
     * Loads a file tree from the given path.
     * @see FileTreeNode
     * @see FileTreeNode.FileNode
     */
    private FileTreeNode loadFiles(String path, Predicate<String> fileFilter) {
        return FileLoader.loadTree(null, path, fileFilter);
    }

    /**
     * Parses a file tree into a {@link KTree}.
     */
    private KTree.KPackage parseFiles(FileTreeNode fileTree, ErrorCollector collector) {

        List<KTree.KUnit> units = new ArrayList<>();
        for (var files : fileTree.leafs()) {
            collector.collect(() -> {
                units.add(KarinaCParser.generateParseTree(
                        files.text(),
                        files.name(),
                        files.path()
                ));
            });
        }
        var subPackages = fileTree.children().stream().map(ref -> parseFiles(ref, collector)).toList();
        return new KTree.KPackage(fileTree.path(), fileTree.name(), subPackages, units);

    }

    /**
     * Resolves imports in a {@link KTree}.
     */
    private KTree.KPackage importTree(KTree.KPackage kPackage) {
        return new ImportResolver().importTree(kPackage);
    }


}
