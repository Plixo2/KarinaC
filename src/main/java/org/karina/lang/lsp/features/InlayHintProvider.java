package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.InlayHintKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionResolver;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.compiler.stages.sugar.SugarResolver;
import org.karina.lang.lsp.*;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class InlayHintProvider {

    public List<InlayHint> from(
            Workspace workspace, URI uri, Range position, ClientSettings settings) {
        if (workspace.getRoot().getContent() == null) {
            return List.of();
        }
        if (!workspace.isFullyTyped() && !settings.excludeErrorFiles) {
            return List.of();
        }
        var loadedFile = workspace.getOrLoadFile(Path.of(uri).normalize());
        if (loadedFile == null) {
            return List.of();
        }
        if (!(loadedFile instanceof FileLoading.FileLoadResult.Success(var karinaFile))) {
            EventHandler.INSTANCE.errorMessage(loadedFile.getErrorMessage());
            return List.of();
        }
        if (karinaFile.state() == null) {
            return List.of();
        }
        if (!karinaFile.isTyped()) {
            EventHandler.INSTANCE.errorMessage("This file has errors, please fix them first.");
            return List.of();
        }
        var internalRegion = new Span(
                null,
                new Span.Position(position.getStart().getLine(), position.getStart().getCharacter()),
                new Span.Position(position.getEnd().getLine(), position.getEnd().getCharacter())
        );

        return getType(workspace.getRoot().getContent().sourceTree(), karinaFile, internalRegion);
    }

    private List<InlayHint> getType(SyncFileTree root, KarinaFile requestFile, Span region) {
        if (!(requestFile.state() instanceof KarinaFile.KarinaFileState.Typed(var ignored, var typed))) {
            return List.of();
        }
        var virtualPackageRoot = TypeInfoProvider.packageFromVirtualTree(root);
        var sugarResolver = new SugarResolver();
        var importer = new ImportResolver();
        var attributes = new AttributionResolver();

        var result = ErrorHandler.mapInternal(() -> {
            var desugared = sugarResolver.desugarUnit(virtualPackageRoot, typed);
            var replacedTreeSugar = HoverProvider.replaceUnit(virtualPackageRoot, desugared.path(), desugared);
            var imported = importer.importUnit(replacedTreeSugar, desugared);
            var replacedTreeImport = HoverProvider.replaceUnit(replacedTreeSugar, imported.path(), imported);
            return attributes.attribUnit(replacedTreeImport, imported);
        });

        if (!(result instanceof ErrorHandler.Result.onSuccess<KTree.KUnit>(var unit))) {
            return List.of();
        }

        var locations = new ArrayList<AttribProvider.AttribLocation>();
        var attribProvider = new AttribProvider(locations, region);
        var error = ErrorHandler.tryInternal(() -> attribProvider.populateList(unit));
        if (error != null) {
            error.pushErrorsToFile();
            EventHandler.INSTANCE.publishDiagnostics();
            return List.of();
        }
        var attribLocations = findAttribLocation(locations, region);

        var hints = new ArrayList<InlayHint>();
        for (var attribLocation : attribLocations) {
            if (attribLocation instanceof AttribProvider.AttribLocation.InlayHint hint) {
                var externalPosition = new Position(
                        hint.defined().end().line(),
                        hint.defined().end().column()
                );


                InlayHint inlayHint = new InlayHint();
                inlayHint.setPosition(externalPosition);
                inlayHint.setLabel(": " + getShortString(hint.type()));
                inlayHint.setKind(InlayHintKind.Type);
                inlayHint.setPaddingLeft(false);
                inlayHint.setPaddingRight(false);
                hints.add(inlayHint);
            }
        }

        return hints;
    }



    public static List<AttribProvider.AttribLocation> findAttribLocation(
            List<AttribProvider.AttribLocation> available,
            Span region
    ) {
        return available.stream().filter(ref -> ref.defined().overlaps(region)).toList();
    }

    public String getShortString(KType type) {
        switch (type) {
            case KType.ArrayType arrayType -> {
                return "[" + getShortString(arrayType.elementType()) + "]";
            }
            case KType.ClassType classType -> {
                String path;
                if (classType.path().value().isEmpty()) {
                    path = "";
                } else {
                    path = classType.path().value().last();
                }
                String generics;
                if (classType.generics().isEmpty()) {
                    generics = "";
                } else {
                    generics = "<" + String.join(", ", classType.generics().stream().map(this::getShortString).toList()) + ">";
                }

                return path + generics;
            }
            case KType.FunctionType functionType -> {
                var returnType = functionType.returnType() == null ? "" : "-> " + getShortString(functionType.returnType());
                var impls = functionType.interfaces().isEmpty() ? "" : " impl " + String.join(", ", functionType.interfaces().stream().map(this::getShortString).toList());
                return "fn(" + String.join(", ", functionType.arguments().stream().map(this::getShortString).toList()) + ") " + returnType + impls;
            }
            case KType.GenericLink genericLink -> {
                return genericLink.link().name();
            }
            case KType.PrimitiveType primitiveType -> {
                return primitiveType.toString();
            }
            case KType.Resolvable resolvable -> {
                if (resolvable.isResolved()) {
                    assert resolvable.get() != null;
                    return getShortString(resolvable.get());
                } else {
                    return "?";
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                return unprocessedType.toString();
            }
            case KType.AnyClass anyClass -> {
                return "?";
            }
        }
    }
}
