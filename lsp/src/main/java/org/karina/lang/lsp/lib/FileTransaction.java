package org.karina.lang.lsp.lib;

/// Transaction to keep the compiler in sync with the file system.
public sealed interface FileTransaction {

    VirtualFile file();

    default boolean isObjectNew() {
        return true;
    }

    record UpdateFile(VirtualFile file, boolean isObjectNew) implements FileTransaction {}
    record RemovedFile(VirtualFile file) implements FileTransaction {}

}
