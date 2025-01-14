package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.errors.Unique;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.KTree;

public class ImportResolver {

    public KTree.KPackage importTree(KTree.KPackage root) throws Log.KarinaException {
        return importPackage(root, root);
    }

    private KTree.KPackage importPackage(KTree.KPackage root, KTree.KPackage pkg) {

        var build = KTree.KPackage.builder();
        build.name(pkg.name());
        build.path(pkg.path());
        try (var collector = new ErrorCollector()) {
            for (var subPackage : pkg.subPackages()) {
                collector.collect(() ->
                        build.subPackage(importPackage(root, subPackage))
                );
            }
            for (var unit : pkg.units()) {
                collector.collect(() ->
                        build.unit(importUnit(root, unit))
                );
            }
        }
        return build.build();

    }

    public KTree.KUnit importUnit(KTree.KPackage root, KTree.KUnit unit) {

        var build = KTree.KUnit.builder();
        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());
        var contextBuilder = ImportContext.getImports(root, unit);
        build.unitScopeSymbolTable(contextBuilder.build());

        try (var collector = new ErrorCollector()) {
            for (var item : unit.items()) {
                collector.collect(() -> {
                    switch (item) {
                        case KTree.KFunction kFunction -> {
                            build.item(ItemImporting.importFunction(root, contextBuilder.copy(), kFunction));
                        }
                        case KTree.KStruct kStruct -> {
                            build.item(ItemImporting.importStruct(root, contextBuilder.copy(), kStruct));
                        }
                        case KTree.KEnum kEnum -> {
                            build.item(ItemImporting.importEnum(root, contextBuilder.copy(), kEnum));
                        }
                        case KTree.KInterface kImplBlock -> {
                            build.item(ItemImporting.importInterface(root, contextBuilder.copy(), kImplBlock));
                        }
                    }
                });
            }
        }
        var duplicate = Unique.testUnique(unit.items(), KTree.KItem::name);
        if (duplicate != null) {
            Log.importError(new ImportError.DuplicateItem(
                    duplicate.first().region(),
                    duplicate.duplicate().region(),
                    duplicate.value().value()
            ));
            throw new Log.KarinaException();
        }

        return build.build();

    }


}
