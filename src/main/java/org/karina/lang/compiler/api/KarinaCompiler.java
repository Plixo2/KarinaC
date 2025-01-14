package org.karina.lang.compiler.api;

public interface KarinaCompiler {
    <T> T compile(FileTreeNode files, DiagnosticCollection collection, Backend<T> backend);
}
