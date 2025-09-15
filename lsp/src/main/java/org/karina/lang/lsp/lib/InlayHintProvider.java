package org.karina.lang.lsp.lib;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.net.URI;
import java.util.List;

public interface InlayHintProvider {

    List<InlayHint> getHints(
            OneShotCompiler.CompiledModelIndex index,
            URI uri,
            Range range
    );
}
