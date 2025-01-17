package org.karina.lang.compiler.stages;

import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.parser.KarinaTextParser;
import org.karina.lang.compiler.stages.preprocess.Preprocessor;
import org.karina.lang.compiler.utils.ObjectPath;

public class KarinaStage {

    public static KTree.KUnit parseUnit(TextSource source, String name, ObjectPath path) {
        return KarinaTextParser.generateParseTree(source, name, path);
    }
    public static KTree.KPackage parseFiles(FileTreeNode fileTree) {
        try (var errorCollection = new ErrorCollector()) {
            return KarinaTextParser.parseFiles(fileTree, errorCollection);
        }
    }

    public static KTree.KPackage preProcessTree(KTree.KPackage kPackage) {
        return new Preprocessor().desugarTree(kPackage);
    }
    public static KTree.KUnit preProcessUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new Preprocessor().desugarUnit(root, kUnit);
    }

    public static KTree.KPackage importTree(KTree.KPackage kPackage) {
        return new ImportResolver().importTree(kPackage);
    }
    public static KTree.KUnit importUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new ImportResolver().importUnit(root, kUnit);
    }

    public static KTree.KPackage attribTree(KTree.KPackage kPackage) {
        return new AttributionResolver().attribTree(kPackage);
    }
    public static KTree.KUnit attribUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new AttributionResolver().attribUnit(root, kUnit);
    }

}
