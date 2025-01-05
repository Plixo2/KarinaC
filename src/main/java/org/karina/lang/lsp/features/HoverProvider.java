package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.errors.LogBuilder;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;
import org.karina.lang.lsp.ErrorHandler;
import org.karina.lang.lsp.EventHandler;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HoverProvider extends AbstractTypeTokenProvider<Hover> {



    @Override
    protected @Nullable Hover getType(
            SyncFileTree root,
            KarinaFile requestFile,
            Span.Position position
    ) {

        if (!(requestFile.state() instanceof KarinaFile.KarinaFileState.Typed(var ignored, var typed))) {
            return null;
        }
        var virtualPackageRoot = TypeInfoProvider.packageFromVirtualTree(root);
        var resolver = new ImportResolver();
        var definitionSite =
                tryImportIndividual(resolver, virtualPackageRoot, requestFile, typed, position);
        if (definitionSite != null) {
            var range = EventHandler.regionToRange(definitionSite.referred());
            var code = String.join("\n", LogBuilder.getCodeOfRegion(definitionSite.region(), false));
            var markdown = """
                    ```karina
                    %s
                    ```
                    """.formatted(code);
            var markedString = new MarkupContent(MarkupKind.MARKDOWN, markdown);
            return new Hover(markedString, range);
        } else {
            //Advanced hover
            var importer = new ImportResolver();
            var attributes = new AttributionResolver();

            var result = ErrorHandler.mapInternal(() -> {
                var imported = importer.importUnit(virtualPackageRoot, typed);
                var replacedTree = replaceUnit(virtualPackageRoot, imported.path(), imported);
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

            var attribLocation = findAttribLocation(locations, position);

            if (attribLocation == null) {
                return null;
            } else {
                return toHover(attribLocation);
            }
        }

    }

    public static KTree.KPackage replaceUnit(KTree.KPackage kPackage, ObjectPath path, KTree.KUnit toReplace) {
        var builder = KTree.KPackage.builder();
        builder.name(kPackage.name());
        builder.path(kPackage.path());
        for (var subPackage : kPackage.subPackages()) {
            builder.subPackage(replaceUnit(subPackage, path, toReplace));
        }
        for (var unit : kPackage.units()) {
            if (unit.path().equals(path)) {
                builder.unit(toReplace);
            } else {
                builder.unit(unit);
            }
        }

        return builder.build();
    }

    private Hover toHover(AttribProvider.AttribLocation location) {
        if (location instanceof AttribProvider.AttribLocation.AttribType(var defined, var type)) {
            var range = EventHandler.regionToRange(defined);
            var text = type.toString();
            var markedString = new MarkupContent(MarkupKind.PLAINTEXT, text);
            return new Hover(markedString, range);
        } if (location instanceof AttribProvider.AttribLocation.InlayHint(var defined, var ignored, var type)) {
            var range = EventHandler.regionToRange(defined);
            var text = type.toString();
            var markedString = new MarkupContent(MarkupKind.PLAINTEXT, text);
            return new Hover(markedString, range);
        } else {
            return null;
        }
    }

    public static @Nullable AttribProvider.AttribLocation findAttribLocation(
            List<AttribProvider.AttribLocation> available,
            Span.Position position
    ) {

        var bestHovered =
                available.stream().filter(ref -> ref.defined().doesContainPosition(position))
                         .min(Comparator.comparingInt(a -> specificity(a.defined())));
        return bestHovered.orElse(null);

    }

    public static int specificity(Span region) {
        var deltaLine = Math.abs(region.end().line() - region.start().line());
        var deltaChar = Math.abs(region.end().column() - region.start().column());

        return deltaLine * 50 + deltaChar;
    }
}
