package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.Location;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;
import org.karina.lang.lsp.*;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

import java.util.ArrayList;

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
        if (definitionSite != null) {
            var uri = definitionSite.file().uriPath().toUri().toString();
            var range = EventHandler.regionToRange(definitionSite.region());
            return new Location(uri, range);
        } else {
            //advanced goto

            var importer = new ImportResolver();
            var attributes = new AttributionResolver();

            var result = ErrorHandler.mapInternal(() -> {
                var imported = importer.importUnit(virtualPackageRoot, typed);
                var replacedTree = HoverProvider.replaceUnit(virtualPackageRoot, imported.path(), imported);
                return attributes.attribUnit(replacedTree, imported);
            });

            if (!(result instanceof ErrorHandler.Result.onSuccess<KTree.KUnit>(var unit))) {
                return null;
            }

            var locations = new ArrayList<AttribProvider.AttribLocation>();
            var attribProvider = new AttribProvider(locations, new Span(null, position, position));
            var error = ErrorHandler.tryInternal(() -> attribProvider.populateList(unit));
            if (error != null) {
                error.pushErrorsToFile();
                EventHandler.INSTANCE.publishDiagnostics();
                return null;
            }

            var attribLocation = HoverProvider.findAttribLocation(locations, position);

            if (attribLocation == null) {
                return null;
            } else {
                if (attribLocation instanceof AttribProvider.AttribLocation.Function(var def, var path, var virtual)) {
                    var item = KTree.findAbsolutItem(virtualPackageRoot, path);
                    if (virtual) {
                        item = KTree.findAbsoluteVirtualFunction(virtualPackageRoot, path);
                    }
                    if (item == null) {
                        //should not happen
                        return null;
                    } else {
                        if (item.region().source().resource() instanceof KarinaFile file) {
                            var uri = file.uriPath().toUri().toString();
                            var range = EventHandler.regionToRange(item.region());
                            return new Location(uri, range);
                        } else {
                            //should not happen
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            }
        }
    }
}
