package org.karina.lang.compiler.api;

public interface KarinaCompiler {
    <T> CompilationResult<T> compile(FileTreeNode files, DiagnosticCollection collection, Backend<T> backend);
}
