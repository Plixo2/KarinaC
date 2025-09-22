package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.lsp.impl.CodeDiagnosticInformationBuilder;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.provider.HoverProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.IdentityHashMap;
import java.util.List;

@RequiredArgsConstructor
public class DefaultHoverProvider implements HoverProvider {
    private final EventService eventService;
    @Override
    public @Nullable Hover getHover(ProviderArgs index, URI uri, Position position) {
        var region_position = new Region.Position(position.getLine(), position.getCharacter());
        if (!(index.models().languageModel() instanceof Option.Some(var languageModel))) {
            return null;
        }
        var pointer = index.getClassPointerOfURI(uri, languageModel);
        if (pointer == null) {
            return null;
        }
        var locationTypes = DefaultDefinitionProvider.getType(index, pointer, region_position);

        var locationType = findExprAtPosition(locationTypes, region_position);
        if (locationType == null) {
            return null;
        }

        var type = locationType.type();
        var range = CodeDiagnosticInformationBuilder.regionToLSPRange(locationType.source());

        var hover = new Hover();

        var typeString = type.toString(
                new IdentityHashMap<>(),
                true
        );
        hover.setContents(new MarkupContent(
                MarkupKind.MARKDOWN,
                "```karina\n" + typeString + "\n```"
        ));
        hover.setRange(range);
        return hover;
    }

    private static @Nullable DefaultDefinitionProvider.ExprLocationType findExprAtPosition(
            List<DefaultDefinitionProvider.ExprLocationType> types,
            Region.Position position
    ) {
        for (var type : types) {
            if (type.type().isVoid()) {
                continue;
            }
            if (type.source().doesContainPosition(position)) {
                return type;
            }
        }
        return null;
    }
}
