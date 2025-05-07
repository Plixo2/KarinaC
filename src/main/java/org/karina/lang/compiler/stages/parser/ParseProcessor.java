package org.karina.lang.compiler.stages.parser;

import org.karina.lang.compiler.utils.FileNode;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper for parsing source code into a {@link org.karina.lang.compiler.model_api.ClassModel}.
 */
public class ParseProcessor {

    public Model parseTree(FileTreeNode<TextSource> fileTree) {
        try (var errorCollection = new ErrorCollector()) {
            return this.parseFiles(fileTree, errorCollection);
        }
    }

    private Model parseFiles(FileTreeNode<TextSource> fileTree, ErrorCollector collector) {
        ModelBuilder builder = new ModelBuilder();
        for (var file : getFiles(fileTree)) {
            Log.begin("parse-" + file.name());
            collector.collect(() -> {
                var unitParser = new TextUnitParser(file.content(), file.name(), file.path());
                unitParser.visit(builder);
            });
            Log.end("parse-" + file.name());
        }
        return builder.build();
    }

    private List<FileNode<TextSource>> getFiles(FileTreeNode<TextSource> fileTree) {
        var files = new ArrayList<FileNode<TextSource>>(fileTree.leafs());
        for (var child : fileTree.children()) {
            files.addAll(getFiles(child));
        }
        return files;
    }

}
