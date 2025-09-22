package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface DefinitionProvider {

    @Nullable List<LocationLink> getDefinition(
            ProviderArgs index,
            URI uri,
            Position position,
            boolean typeDefinition
    );
}
