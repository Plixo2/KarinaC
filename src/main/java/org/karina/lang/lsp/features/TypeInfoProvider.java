package org.karina.lang.lsp.features;

import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;
import org.karina.lang.lsp.ErrorHandler;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

/**
 * Main error checker for the language server.
 * Invoked on any change to the virtual file system.
 * Only invoked when the entire file system is in a typed state, so that we can build a
 * KPackage tree from the virtual file tree and then invoke the various stages of the compiler.
 * When an error is found, it is pushed to the respective file.
 */
public class TypeInfoProvider {

    public void updateAll(SyncFileTree sourceTree) {
        var root = packageFromVirtualTree(sourceTree);
        var importer = new ImportResolver();
        var attributes = new AttributionResolver();

        var error = ErrorHandler.tryInternal(() -> {
            var imported = importer.importTree(root);
            var attrib = attributes.attribTree(imported);
        });
        if (error != null) {
            error.pushErrorsToFile();
        }

    }

    /*
     * Convert a virtual file tree to a KPackage tree, but only for the typed files.
     * A Typed file is a file that has been successfully parsed.
     */
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
