package org.karina.lang.lsp.lib.provider;

import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;

public interface CompileProvider {

    String compile(ProviderArgs index, URI main);
    String compile(ProviderArgs index, ObjectPath main);

}
