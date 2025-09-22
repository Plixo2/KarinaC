package org.karina.lang.compiler.stages.parser;

import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.FileTreeNode;


/**
 * Helper for parsing source code into a {@link org.karina.lang.compiler.model_api.ClassModel}.
 */
public class ParseProcessor {

    public Model parseTree(Context c, FileTreeNode fileTree) {
        var flatFiles = FileTreeNode.flatten(fileTree);
        ModelBuilder builder = new ModelBuilder();

        try (var fork = c.fork()) {
            for (var file : flatFiles) {
                fork.collect(subC -> {
                    ParseItem.parseClass(subC, file.content(), file.name(), file.path(), builder);
                    // return null, and mutate thread-safe ModelBuilder
                    return null;
                });
            }
            var _ = fork.dispatchParallel();
        }
        return builder.build(c);
    }




}
