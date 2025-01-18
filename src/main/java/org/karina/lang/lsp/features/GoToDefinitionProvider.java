package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.Location;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.KarinaStage;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KTree;
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
        var definitionSite =
                tryImportIndividual(virtualPackageRoot, requestFile, typed, position);
        if (definitionSite != null) {
            var uri = definitionSite.file().uriPath().toUri().toString();
            var range = EventHandler.regionToRange(definitionSite.region());
            return new Location(uri, range);
        } else {
            //advanced goto


            var result = ErrorHandler.mapInternal(() -> {
                var desugared = KarinaStage.preProcessUnit(virtualPackageRoot, typed);
                var replacedTreeSugar = HoverProvider.replaceUnit(virtualPackageRoot, desugared.path(), desugared);
                var imported = KarinaStage.importUnit(replacedTreeSugar, desugared);
                var replacedTreeImport = HoverProvider.replaceUnit(replacedTreeSugar, imported.path(), imported);
                return KarinaStage.attribUnit(replacedTreeImport, imported);
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
