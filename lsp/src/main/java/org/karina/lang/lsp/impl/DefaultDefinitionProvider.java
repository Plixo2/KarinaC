package org.karina.lang.lsp.impl;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.lib.DefinitionProvider;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;

public class DefaultDefinitionProvider implements DefinitionProvider {
    @Override
    public @Nullable Location getDefinition(
            OneShotCompiler.CompiledModelIndex index, URI uri,
            Position position
    ) {
        return null;
    }
}
