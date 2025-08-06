package org.karina.lang.compiler.stages.parser;

import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper for parsing source code into a {@link org.karina.lang.compiler.model_api.ClassModel}.
 */
public class ParseProcessor {

    public Model parseTree(Context c, FileTreeNode<TextSource> fileTree) {
        var flatFiles = getFiles(fileTree);
        ModelBuilder builder = new ModelBuilder();

        try (var fork = c.fork()) {
            for (var file : flatFiles) {
                fork.collect(subC -> {
                    var start = System.currentTimeMillis();
                    var unitParser = new TextUnitParser(subC, file.content(), file.name(), file.path());
                    // return null, and mutate thread-safe ModelBuilder
                    unitParser.visit(builder);

                    var end = System.currentTimeMillis();
                    Log.record("parse-" + file.name() + ": " + (end - start) + "ms");

                    return null;
                });
            }
            var _ = fork.dispatchParallel();
        }
        return builder.build(c);
    }



    private List<FileNode<TextSource>> getFiles(FileTreeNode<TextSource> fileTree) {
        var files = new ArrayList<FileNode<TextSource>>(fileTree.leafs());
        for (var child : fileTree.children()) {
            files.addAll(getFiles(child));
        }
        return files;
    }


}
