package org.karina.lang.lsp.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.ErrorInformation;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.lsp.lib.VirtualFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class CodeDiagnosticInformation implements ErrorInformation {
    private final List<StringBuilder> lines = new ArrayList<>();
    private final List<RegionOf<String>> related = new ArrayList<>();
    private @Nullable Region region;

    @Override
    @Contract(mutates = "this")
    public void setTitle(String title) {
//        append(title);
    }

    @Override
    @Contract(mutates = "this")
    public void setPrimarySource(Region region) {
        this.region = region;
    }

    @Override
    @Contract(mutates = "this")
    public void addSecondarySource(Region region, String message) {
        related.add(new RegionOf<>(region, message));
    }

    @Override
    @Contract(mutates = "this")
    public StringBuilder append(String line) {
        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;
    }

    private String getMessageString() {
        var builder = new StringBuilder();
        for (var line : this.lines) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }


    public static @Nullable DiagnosticForFile toDiagnosticAndFile(CodeDiagnosticInformation information) {
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


    private static String regionToString(Region region) {
        return String.join("\n", ErrorInformation.getCodeOfRegion(region, false));
    }

//    private static Location regionToLocation(Region region) {
//        return new Location(
//                regionToURI(region).toString(),
//                regionToLSPRange(region)
//        );
//    }
//
//    private static URI regionToURI(Region region) {
//        return resourceToFile(region.source().resource()).uri();
//    }


    /// Returns null, when the resource is not a VirtualFile
    private static @Nullable VirtualFile resourceToFile(Resource resource) {
        if (resource instanceof VirtualFile virtualFile) {
//            throw new IllegalArgumentException(
//                    "Region's Resource source must be a VirtualFile" +
//                            ", got " + resource.getClass().getName() + " instead: " +
//                            "'" + resource.identifier() + "'"
//            );
            return virtualFile;
        }
        return null;

    }

    private static Range regionToLSPRange(Region region) {
        var ordered = region.reorder();
        return new Range(
                new Position(ordered.start().line(), ordered.start().column()),
                new Position(ordered.end().line(), ordered.end().column())
        );
    }
}
