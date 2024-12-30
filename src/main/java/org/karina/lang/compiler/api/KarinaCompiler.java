package org.karina.lang.compiler.api;

public interface KarinaCompiler {
    boolean compile(FileTreeNode files, DiagnosticCollection collection);
}
