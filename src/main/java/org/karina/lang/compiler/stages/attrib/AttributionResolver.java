package org.karina.lang.compiler.stages.attrib;

import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.objects.KTree;

public class AttributionResolver {

    public KTree.KPackage attribTree(KTree.KPackage tree) {
        return attribPackage(tree, tree);
    }

    private KTree.KPackage attribPackage(KTree.KPackage root, KTree.KPackage kPackage) {
        var build =  KTree.KPackage.builder();
        build.path(kPackage.path());
        build.name(kPackage.name());

        try (var collector = new ErrorCollector()){
            collector.collect(() -> {
                for (var subPackage : kPackage.subPackages()) {
                    build.subPackage(attribPackage(root, subPackage));
                }
            });

            collector.collect(() -> {
                for (var unit : kPackage.units()) {
                    build.unit(attribUnit(root, unit));
                }
            });
        }

        return build.build();
    }

    private KTree.KUnit attribUnit(KTree.KPackage root, KTree.KUnit unit) {
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
                            build.item(ItemAttribution.attribFunction(root, symbolTable, null, kFunction));
                        }
                        case KTree.KEnum kenum -> {
                            build.item(ItemAttribution.attribEnum(root, symbolTable, kenum));
                        }
                        case KTree.KStruct kStruct -> {
                            build.item(ItemAttribution.attribStruct(root, symbolTable, kStruct));
                        }
                        case KTree.KInterface kInterface -> {
                            build.item(ItemAttribution.attribInterface(root, symbolTable, kInterface));
                        }
                    }
                });
            }
        }

        return build.build();
    }

}
