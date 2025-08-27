package org.karina.lang.lsp.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.ErrorInformationBuilder;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.lsp.lib.VirtualFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class CodeDiagnosticInformationBuilder implements ErrorInformationBuilder {
    private final List<StringBuilder> lines = new ArrayList<>();
    private final List<RegionOf<String>> related = new ArrayList<>();
    private @Nullable Region region;

    @Override
    @Contract(mutates = "this")
    public void setTitle(String title) {
        append(title);
    }

    @Override
    @Contract(mutates = "this")
    public void setPrimarySource(Region region) {
        this.region = region;
    }

    @Override
    @Contract(mutates = "this")
    public void addSecondarySource(Region region, String message) {
        this.related.add(new RegionOf<>(region, message));
    }

    @Override
    @Contract(mutates = "this")
    public StringBuilder append(String line) {
        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;
    }

    public String getMessageString() {
        var builder = new StringBuilder();
        for (var line : this.lines) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }


    public static @Nullable DiagnosticForFile toDiagnosticAndFile(
            CodeDiagnosticInformationBuilder information) {
        if (information.region == null) {
            return null;
        }
        var file = resourceToFile(information.region.source().resource());
        if (file == null) {
            return null;
        }

        var diagnostic = new Diagnostic();
        diagnostic.setRange(regionToLSPRange(information.region));
        diagnostic.setMessage(information.getMessageString());
        if (diagnostic.getMessage().isEmpty()) {
            diagnostic.setMessage("Unknown error");
        }
        diagnostic.setSource("karina");
        diagnostic.setSeverity(DiagnosticSeverity.Error);

        var relatedInfo = new ArrayList<DiagnosticRelatedInformation>();
        for (var related : information.related) {
            var relatedRegion = related.region();
            var relatedFile = resourceToFile(relatedRegion.source().resource());
            if (relatedFile == null) {
                relatedFile = file;
                relatedRegion = information.region;
            }

            relatedInfo.add(new DiagnosticRelatedInformation(
                            new Location(
                                    relatedFile.uri().toString(),
                                    regionToLSPRange(relatedRegion)
                            ),
                            related.value()
            ));
        }
        diagnostic.setRelatedInformation(relatedInfo);
//        diagnostic.setCode(regionToString(information.region));


        return new DiagnosticForFile(file, diagnostic);
    }
    public record DiagnosticForFile(VirtualFile file, Diagnostic diagnostic) {}



    /// Returns null, when the resource is not a VirtualFile
    public static @Nullable VirtualFile resourceToFile(Resource resource) {
        if (resource instanceof VirtualFile virtualFile) {
            return virtualFile;
        }
        return null;

    }

    private static Range regionToLSPRange(Region region) {
        var ordered = region.reorder();
        int offset = 0;
        var isSame = ordered.start().line() == ordered.start().column()
                && ordered.end().line() == ordered.end().column();
        if (isSame) {
            offset = 1;
        }

        return new Range(
                new Position(ordered.start().line(), ordered.start().column()),
                new Position(ordered.end().line(), ordered.end().column() + offset)
        );
    }
}
