package org.karina.lang.lsp.lib.provider;

import org.eclipse.lsp4j.CodeLens;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface CodeLensProvider {

    List<CodeLens> getLenses(ProviderArgs index, URI uri);
}
