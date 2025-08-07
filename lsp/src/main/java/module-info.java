module lsp {
    requires org.eclipse.lsp4j;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires static lombok;

    requires compiler;
    requires org.eclipse.lsp4j.jsonrpc;
    requires com.google.errorprone.annotations;

}