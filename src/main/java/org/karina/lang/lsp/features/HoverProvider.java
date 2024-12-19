package org.karina.lang.lsp.features;

import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.LogBuilder;
import org.karina.lang.compiler.stages.imports.ImportResolver;
import org.karina.lang.lsp.EventHandler;
import org.karina.lang.lsp.fs.KarinaFile;
import org.karina.lang.lsp.fs.SyncFileTree;

public class HoverProvider extends AbstractTypeTokenProvider<Hover> {


    @Override
    protected @Nullable Hover getType(
            SyncFileTree root, KarinaFile requestFile,
            Span.Position position
    ) {
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
            var range = EventHandler.regionToRange(definitionSite.referred());
            var code = String.join("\n", LogBuilder.getCodeOfRegion(definitionSite.region(), false));
            var markdown = """
                    ```karina
                    %s
                    ```
                    """.formatted(code);
            var markedString = new MarkupContent(MarkupKind.MARKDOWN, markdown);
            return new Hover(markedString, range);

        }
    }
}
