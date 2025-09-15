package org.karina.lang.lsp.impl;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.Range;
import org.karina.lang.lsp.lib.InlayHintProvider;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;
import java.util.List;

public class DefaultInlayHintProvider implements InlayHintProvider {
    @Override
    public List<InlayHint> getHints(
            OneShotCompiler.CompiledModelIndex index, URI uri,
            Range range
    ) {
        return List.of();
    }
}
