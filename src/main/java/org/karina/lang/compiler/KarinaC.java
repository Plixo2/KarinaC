package org.karina.lang.compiler;

import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.parser.KarinaVisitor;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Main compiler class, handling everything from file loading to code generation.
 * @see FileTreeNode
 * @see KarinaVisitor
 * @see ItemImporting
 */
public class KarinaC {

    void compile(CompileConfig config) throws Log.KarinaException {

        var fileTree = loadFiles(config.sourceDirectory, config.fileFilter);

        KTree.KPackage parseTree;
        try (var errorCollection = new ErrorCollector()) {
            parseTree = parseFiles(fileTree, errorCollection);
        }
        var importedTree = importTree(parseTree);
        var attributedTree = attributeTree(importedTree);

        DebugWriter.write(parseTree, "resources/raw.json");
        DebugWriter.write(importedTree, "resources/imported.json");
        DebugWriter.write(attributedTree, "resources/attributed.json");

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
     * Resolves item in a {@link KTree}.
     */
    private KTree.KPackage importTree(KTree.KPackage kPackage) {
        return new ImportResolver().importTree(kPackage);
    }


    private KTree.KPackage attributeTree(KTree.KPackage tree) {
        return new AttributionResolver().attribTree(tree);
    }
}
