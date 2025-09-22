package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;

public interface HoverProvider {

    @Nullable Hover getHover(
            ProviderArgs index,
            URI uri,
            Position position
    );
}
