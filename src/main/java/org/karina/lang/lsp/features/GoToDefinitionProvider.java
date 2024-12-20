package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.Location;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;
import org.karina.lang.lsp.*;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

public class GoToDefinitionProvider extends AbstractTypeTokenProvider<Location> {

    @Override
    protected @Nullable Location getType(
            SyncFileTree root, KarinaFile requestFile, Span.Position position) {
        if (!(requestFile.state() instanceof KarinaFile.KarinaFileState.Typed(var ignored, var typed))) {
            return null;
        }
        var virtualPackageRoot = TypeInfoProvider.packageFromVirtualTree(root);
        var resolver = new ImportResolver();
        var definitionSite =
                tryImportIndividual(resolver, virtualPackageRoot, requestFile, typed, position);
        if (definitionSite == null) {
            return null;
        } else {
            var uri = definitionSite.file().uriPath().toUri().toString();
            var range = EventHandler.regionToRange(definitionSite.region());
            return new Location(uri, range);
        }

    }
}
