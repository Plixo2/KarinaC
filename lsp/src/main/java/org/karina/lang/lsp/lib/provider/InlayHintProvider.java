package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.Range;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface InlayHintProvider {

    List<InlayHint> getHints(
            ProviderArgs index,
            URI uri,
            Range range
    );
}
