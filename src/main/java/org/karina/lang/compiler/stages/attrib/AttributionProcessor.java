package org.karina.lang.compiler.stages.attrib;

import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.objects.KTree;

public class AttributionProcessor {

    public KTree.KPackage attribTree(KTree.KPackage tree) {
        return attribPackage(tree, tree);
    }

    private KTree.KPackage attribPackage(KTree.KPackage root, KTree.KPackage kPackage) {

        var build =  KTree.KPackage.builder();
        build.path(kPackage.path());
        build.name(kPackage.name());

        try (var collector = new ErrorCollector()) {
            for (var subPackage : kPackage.subPackages()) {
                collector.collect(() -> {
                    build.subPackage(attribPackage(root, subPackage));
                });
            }

            for (var unit : kPackage.units()) {
                collector.collect(() -> {
                    build.unit(attribUnit(root, unit));
                });
            }
        }

        return build.build();

    }

    public KTree.KUnit attribUnit(KTree.KPackage root, KTree.KUnit unit) {

        var build = KTree.KUnit.builder();
        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());
        var symbolTable = unit.unitScopeSymbolTable();
        build.unitScopeSymbolTable(symbolTable);

        try (var collector = new ErrorCollector()) {
            for (var item : unit.items()) {
                collector.collect(() -> {
                    switch (item) {
                        case KTree.KFunction kFunction -> {
                            build.item(AttributionItem.attribFunction(root, symbolTable, null, kFunction));
                        }
                        case KTree.KEnum kenum -> {
                            build.item(AttributionItem.attribEnum(root, symbolTable, kenum));
                        }
                        case KTree.KStruct kStruct -> {
                            build.item(AttributionItem.attribStruct(root, symbolTable, kStruct));
                        }
                        case KTree.KInterface kInterface -> {
                            build.item(AttributionItem.attribInterface(root, symbolTable, kInterface));
                        }
                    }
                });
            }
        }

        return build.build();

    }

}
