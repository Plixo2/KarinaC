package org.karina.lang.compiler.api;

import org.karina.lang.compiler.DiagnosticCollection;

public interface KarinaCompiler {
    boolean compile(FileTreeNode files, DiagnosticCollection collection);
}
