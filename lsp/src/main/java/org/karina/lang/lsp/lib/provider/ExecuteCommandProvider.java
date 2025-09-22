package org.karina.lang.lsp.lib.provider;

import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.util.List;

public interface ExecuteCommandProvider {
    Object executeCommand(ProviderArgs index, CompileProvider compileProvider, String command, List<Object> args);
}
