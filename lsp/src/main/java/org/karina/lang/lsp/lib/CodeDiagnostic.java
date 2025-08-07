package org.karina.lang.lsp.lib;

import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CodeDiagnostic {
    Region region;
    DiagnosticSeverity severity;
    String message;

    @Nullable String code;
    /// href link
    @Nullable String errorLink;

    boolean deprecated;
    boolean unnecessary;


    List<RegionOf<String>> relatedInformation;


    public static Diagnostic toLspDiagnostic(CodeDiagnostic diagnostic) {
        var lspDia = new Diagnostic();
        lspDia.setRange(
                regionToLSPRange(diagnostic.region)
        );
        lspDia.setSeverity(diagnostic.severity);
        lspDia.setMessage(diagnostic.message);
        lspDia.setCode(diagnostic.code);
        if (diagnostic.errorLink != null) {
            lspDia.setCodeDescription(new DiagnosticCodeDescription(diagnostic.errorLink));
        }
        lspDia.setTags(new ArrayList<>());
        if (diagnostic.deprecated) {
            lspDia.getTags().add(DiagnosticTag.Deprecated);
        }
        if (diagnostic.unnecessary) {
            lspDia.getTags().add(DiagnosticTag.Unnecessary);
        }
        var relatedInfo = new ArrayList<DiagnosticRelatedInformation>();
        for (var extra : diagnostic.relatedInformation) {
            var relatedInfoItem = new DiagnosticRelatedInformation(
                    regionToLocation(extra.region()), extra.value()
            );
            relatedInfo.add(relatedInfoItem);
        }
        lspDia.setRelatedInformation(relatedInfo);
        lspDia.setSource("karina");

        return lspDia;
    }

    public static Location regionToLocation(Region region) {
        return new Location(
                regionToURI(region).toString(),
                regionToLSPRange(region)
        );
    }

    public static URI regionToURI(Region region) {
        return resourceToFile(region.source().resource()).uri();
    }

    public static VirtualFile resourceToFile(Resource resource) {
        if (!(resource instanceof VirtualFile virtualFile)) {
            throw new IllegalArgumentException(
                    "Region's Resource source must be a VirtualFile" +
                            ", got " + resource.getClass().getName() + " instead: " +
                            "'" + resource.identifier() + "'"
            );
        }

        return virtualFile;
    }

    public static Range regionToLSPRange(Region region) {
        var ordered = region.reorder();
        return new Range(
                new Position(ordered.start().line(), ordered.start().column()),
                new Position(ordered.end().line(), ordered.end().column())
        );
    }
}
