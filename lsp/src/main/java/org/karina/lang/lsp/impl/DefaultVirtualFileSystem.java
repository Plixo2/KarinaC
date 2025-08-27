package org.karina.lang.lsp.impl;

import com.google.errorprone.annotations.CheckReturnValue;
import karina.lang.Option;
import org.jetbrains.annotations.Contract;
import org.karina.lang.lsp.lib.FileTransaction;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileSystem;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultVirtualFileSystem implements VirtualFileSystem {
    private final ConcurrentHashMap<URI, VirtualFile> files = new ConcurrentHashMap<>();

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> openFile(URI uri, String content, int version) {
        var existing = this.files.get(uri);
        if (existing != null) {
            existing.open();
            if (existing.content().equals(content)) {
                return Option.none();
            } else {
                existing.updateContent(content, version);
                return Option.some(new FileTransaction.UpdateFile(existing, false));
            }
        } else {
            var newFile = new DefaultVirtualFile(uri, content, version, true);
            this.files.put(uri, newFile);
            return Option.some(new FileTransaction.UpdateFile(newFile, true));
        }
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> updateFile(URI uri, String content, int version) {
        var existing = this.files.get(uri);
        if (existing != null) {
            if (existing.content().equals(content)) {
                return Option.none();
            } else {
                existing.updateContent(content, version);
                return Option.some(new FileTransaction.UpdateFile(existing, false));
            }
        }
        else {
            var newFile = new DefaultVirtualFile(uri, content, version, false);
            this.files.put(uri, newFile);
            return Option.some(new FileTransaction.UpdateFile(newFile, true));
        }
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> closeFile(URI uri) {
        var file = this.files.get(uri);
        if (file != null) {
            file.close();
        }
        return Option.none();
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> saveFile(URI uri) {
        // do nothing
        return Option.none();
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> deleteFile(URI uri) {
        var prev = this.files.remove(uri);
        if (prev == null) {
            return Option.none();
        }
        return Option.some(new FileTransaction.RemovedFile(prev));
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized Option<FileTransaction> reloadFromDisk(URI uri, String diskContent) {
        var existing = this.files.get(uri);
        if (existing != null) {
            if (existing.isOpen()) {
                return Option.none();
            }
            if (existing.content().equals(diskContent)) {
                return Option.none();
            } else {
                existing.updateContent(diskContent, 0);
                return Option.some(new FileTransaction.UpdateFile(existing, false));
            }
        } else {
            var newFile = new DefaultVirtualFile(uri, diskContent, 0, false);
            this.files.put(uri, newFile);
            return Option.some(new FileTransaction.UpdateFile(newFile, true));
        }
    }


    @Override
    @Contract(pure = true)
    public synchronized boolean isFileOpen(URI uri) {
        var file = this.files.get(uri);
        return file != null && file.isOpen();
    }

    /// @return null if the file does not exist, otherwise the content of the file
    @Override
    @Contract(pure = true)
    public synchronized Option<String> getContent(URI uri) {
        var file = this.files.get(uri);
        return Option.fromNullable(file).map(VirtualFile::content);
    }

    @Override
    public List<VirtualFile> files() {
        return List.copyOf(this.files.values());
    }


}
