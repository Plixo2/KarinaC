package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.imports.ItemImporting;
import org.karina.lang.lsp.*;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTypeTokenProvider<T> {

    public @Nullable T from(
            Workspace workspace, URI uri, Position position, ClientSettings settings) {
        if (workspace.getRoot().getContent() == null) {
            return null;
        }
        if (!workspace.isFullyTyped() && !settings.excludeErrorFiles) {
            return null;
        }
        var loadedFile = workspace.getOrLoadFile(Path.of(uri).normalize());
        if (loadedFile == null) {
            return null;
        }
        if (!(loadedFile instanceof FileLoading.FileLoadResult.Success(var karinaFile))) {
            EventHandler.INSTANCE.errorMessage(loadedFile.getErrorMessage());
            return null;
        }
        if (karinaFile.state() == null) {
            return null;
        }
        if (!karinaFile.isTyped()) {
            EventHandler.INSTANCE.errorMessage("This file has errors, please fix them first.");
            return null;
        }
        var internalPosition = new Span.Position(position.getLine(), position.getCharacter());

        return getType(workspace.getRoot().getContent().sourceTree(), karinaFile, internalPosition);
    }



    protected abstract @Nullable T getType(SyncFileTree root, KarinaFile requestFile, Span.Position position);

    protected @Nullable GoToDefinitionProvider.TypeDefinitionSite tryImportIndividual(
            ImportResolver resolver, KTree.KPackage root, KarinaFile file, KTree.KUnit unit, Span.Position position) {

        var error = ErrorHandler.mapInternal(() -> {
            var importedUnit = resolver.importUnit(root, unit);
            var list = new ArrayList<ImportedTypeVisitor.SourceLocation>();
            var typeVisitor = new ImportedTypeVisitor(list);
            typeVisitor.populateTypeList(importedUnit);
            var locationFound = findLocation(root, list, position);
            if (locationFound != null) {
                var location = locationFound.region();
                var referred = locationFound.value();
                if (location.source().resource() instanceof KarinaFile target) {
                    if (location.doesContainPosition(position)) {
                        location = new Span(location.source(), location.start(), location.start());
                    }
                    return new GoToDefinitionProvider.TypeDefinitionSite(target, location, referred);
                }
            }
            return null;
        });

        return switch (error) {
            case ErrorHandler.Result.onFailure(var ignored) -> null;
            case ErrorHandler.Result.onSuccess(var v) -> v;
        };
    }

    private @Nullable SpanOf<Span> findLocation(
            KTree.KPackage root,
            List<ImportedTypeVisitor.SourceLocation> available,
            Span.Position position
    ) {

        for (var sourceLocation : available) {
            var definition = sourceLocation.defined();
            if (definition.doesContainPosition(position)) {
                switch (sourceLocation) {
                    case ImportedTypeVisitor.SourceLocation.ImportNameToken(var ignored, var name, var unitPath) -> {
                        var unit = root.findUnit(unitPath);
                        if (unit != null) {
                            var item = unit.findItem(name);
                            if (item != null) {
                                return SpanOf.span(item.region(), definition);
                            }
                        }
                    }
                    case ImportedTypeVisitor.SourceLocation.ClassToken(var ignored, var path, var ignored2) -> {
                        var item = KTree.findAbsolutItem(root, path);
                        if (item != null) {
                            return SpanOf.span(item.region(), definition);
                        }
                    }
                    case ImportedTypeVisitor.SourceLocation.ImportPathRegion(var ignored, var path) -> {
                        var unit = root.findUnit(path);
                        if (unit != null) {
                            return SpanOf.span(unit.region(), definition);
                        }
                    }
                    case ImportedTypeVisitor.SourceLocation.GenericLinkToken direct -> {
                        return SpanOf.span(direct.linked(), definition);
                    }
                }
            }
        }
        return null;
    }


    //region is linked to where the type is defined
    //referred is linked to the position where the type token is
    public record TypeDefinitionSite(KarinaFile file, Span region, Span referred) {}
}
