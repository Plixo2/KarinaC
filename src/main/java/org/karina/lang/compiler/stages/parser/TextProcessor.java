package org.karina.lang.compiler.stages.parser;

import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.objects.KTree;

import java.util.ArrayList;


/**
 * Helper for parsing source code into a {@link KTree.KUnit}.
 */
public class TextProcessor {

    public KTree.KPackage parseFiles(FileTreeNode<TextSource> fileTree) {
        try (var errorCollection = new ErrorCollector()) {
            return this.parseFiles(fileTree, errorCollection);
        }
    }

    private KTree.KPackage parseFiles(FileTreeNode<TextSource> fileTree, ErrorCollector collector) {

        var units = new ArrayList<KTree.KUnit>();
        for (var files : fileTree.leafs()) {
            collector.collect(() -> {
                var unitParser = new TextUnitParser(files.content(), files.name(), files.path());
                units.add(unitParser.parse());
            });
        }
        var subPackages = fileTree.children().stream().map(ref -> parseFiles(ref, collector)).toList();
        return new KTree.KPackage(fileTree.path(), fileTree.name(), subPackages, units);

    }



}
