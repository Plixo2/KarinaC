package org.karina.lang.lsp;

import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.parser.TextProcessor;
import org.karina.lang.compiler.stages.parser.TextUnitParser;
import org.karina.lang.compiler.stages.preprocess.PreProcessor;
import org.karina.lang.compiler.utils.ObjectPath;

public class KarinaStage {

    public static KTree.KUnit parseUnit(TextSource source, String name, ObjectPath path) {
        var unitParser = new TextUnitParser(source, name, path);
        return unitParser.parse();
    }
    public static KTree.KPackage parseFiles(FileTreeNode<TextSource> fileTree) {
        return new TextProcessor().parseFiles(fileTree);
    }

    public static KTree.KPackage preProcessTree(KTree.KPackage kPackage) {
        return new PreProcessor().desugarTree(kPackage);
    }
    public static KTree.KUnit preProcessUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new PreProcessor().desugarUnit(root, kUnit);
    }

    public static KTree.KPackage importTree(KTree.KPackage kPackage) {
        return new ImportProcessor().importTree(kPackage);
    }
    public static KTree.KUnit importUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new ImportProcessor().importUnit(root, kUnit);
    }

    public static KTree.KPackage attribTree(KTree.KPackage kPackage) {
        return new AttributionProcessor().attribTree(kPackage);
    }
    public static KTree.KUnit attribUnit(KTree.KPackage root, KTree.KUnit kUnit) {
        return new AttributionProcessor().attribUnit(root, kUnit);
    }

}
