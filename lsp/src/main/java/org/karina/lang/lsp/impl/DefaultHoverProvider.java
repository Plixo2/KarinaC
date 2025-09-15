package org.karina.lang.lsp.impl;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.lsp.lib.HoverProvider;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;

public class DefaultHoverProvider implements HoverProvider {
    @Override
    public @Nullable Hover getHover(OneShotCompiler.CompiledModelIndex index, URI uri, Position position) {
        return null;
    }
}
