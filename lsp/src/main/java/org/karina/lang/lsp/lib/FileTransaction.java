package org.karina.lang.lsp.lib;

/// Transaction to keep the compiler in sync with the file system.
public sealed interface FileTransaction {
    boolean isObjectNew();

    record UpdateFile(VirtualFile file, boolean isObjectNew) implements FileTransaction {}
    record RemovedFile(VirtualFile file) implements FileTransaction {
        @Override
        public boolean isObjectNew() {
            return false;
        }
    }

//    record OpenFile(VirtualFile file, boolean isObjectNew) implements FileTransaction {}
//    record UpdateFile(VirtualFile file, boolean isObjectNew) implements FileTransaction {}
//    record LoadFile(VirtualFile file, boolean isObjectNew) implements FileTransaction {}
//
//    record CloseFile(VirtualFile file) implements FileTransactionNoNew {}
//    record DeleteFile(VirtualFile file) implements FileTransactionNoNew {}
//
//
//    sealed interface FileTransactionNoNew extends FileTransaction {
//        @Override
//        default boolean isObjectNew() {
//            return false;
//        }
//    }
}
