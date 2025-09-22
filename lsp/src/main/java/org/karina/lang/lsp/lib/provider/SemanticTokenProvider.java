package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.SemanticTokens;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;

public interface SemanticTokenProvider {

    SemanticTokens getTokens(ProviderArgs index, URI uri);

}
