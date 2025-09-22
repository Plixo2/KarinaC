package org.karina.lang.lsp.lib.provider;

import com.google.gson.JsonObject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Range;
import org.karina.lang.compiler.utils.logging.errors.Error;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

public interface CodeActionProvider {
    List<CodeAction> getAction(ProviderArgs index, URI uri, Range range, CodeActionContext context);

    JsonObject getCodeActionFromError(Error error, URI uri);
}
