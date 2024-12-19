package org.karina.lang.lsp.features;

import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.lsp.ErrorHandler;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

public class TypeInfoProvider {


    public void updateAll(SyncFileTree sourceTree) {
        var root = packageFromVirtualTree(sourceTree);
        var resolver = new ImportResolver();

        var error = ErrorHandler.tryInternal(() -> {
            resolver.importTree(root);
        });
        if (error != null) {
            error.pushErrorsToFile();
        }

    }

    public static KTree.KPackage packageFromVirtualTree(SyncFileTree tree) {
        var build = KTree.KPackage.builder();
        build.name(tree.name());
        build.path(tree.path());
        for (var subTree : tree.getChildren()) {
            build.subPackage(packageFromVirtualTree(subTree));
        }
        for (var file : tree.getLeafs()) {
            if (file.state() instanceof KarinaFile.KarinaFileState.Typed(var ignored, var typed)) {
                build.unit(typed);
            }
        }

        return build.build();
    }
}
