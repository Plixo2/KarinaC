package org.karina.lang.compiler.stages;

import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.preprocess.Preprocessor;

public class KarinaStage {

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
