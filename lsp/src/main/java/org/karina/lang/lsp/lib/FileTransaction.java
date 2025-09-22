package org.karina.lang.lsp.lib;

/// Transaction to keep the compiler in sync with the file system.
public sealed interface FileTransaction {

    VirtualFile file();

    boolean objectChange();

    record UpdateFile(VirtualFile file, boolean objectChange) implements FileTransaction {}
    record RemovedFile(VirtualFile file) implements FileTransaction {
        @Override
        public boolean objectChange() {
            return true;
        }
    }

}
