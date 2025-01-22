package org.karina.lang.compiler.stages.parser;

import org.karina.lang.compiler.api.FileNode;
import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model.Model;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Helper for parsing source code into a {@link KTree.KUnit}.
 */
public class TextProcessor {

    public JKModel parseFiles(FileTreeNode<TextSource> fileTree) {
        try (var errorCollection = new ErrorCollector()) {
            return this.parseFiles(fileTree, errorCollection);
        }
    }

    private JKModel parseFiles(FileTreeNode<TextSource> fileTree, ErrorCollector collector) {
        JKModelBuilder builder = new JKModelBuilder();
        for (var file : getFiles(fileTree)) {
            collector.collect(() -> {
                var unitParser = new TextUnitParser(file.content(), file.name(), file.path());
                unitParser.parse(builder);
            });
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
