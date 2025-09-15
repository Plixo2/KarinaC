package org.karina.lang.lsp.lib;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;

public interface DefinitionProvider {

    @Nullable Location getDefinition(
            OneShotCompiler.CompiledModelIndex index,
            URI uri,
            Position position
    );
}
