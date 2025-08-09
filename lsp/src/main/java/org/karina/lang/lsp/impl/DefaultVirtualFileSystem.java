package org.karina.lang.lsp.impl;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
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
    public synchronized @Nullable FileTransaction openFile(URI uri, String content, int version) {
        var existing = this.files.get(uri);
        if (existing != null) {
            existing.open();
            if (existing.content().equals(content)) {
                return null;
            } else {
                existing.updateContent(content, version);
                return new FileTransaction.UpdateFile(existing, false);
            }
        } else {
            var newFile = new VirtualFile(uri, content, version, true);
            this.files.put(uri, newFile);
            return new FileTransaction.UpdateFile(newFile, true);
        }
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized @Nullable FileTransaction updateFile(URI uri, String content, int version) {
        var file = this.files.get(uri);
        if (file != null) {
            file.updateContent(content, version);
            return new FileTransaction.UpdateFile(file, false);
        }
        else {
            var newFile = new VirtualFile(uri, content, version, false);
            this.files.put(uri, newFile);
            return new FileTransaction.UpdateFile(newFile, true);
        }
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized @Nullable FileTransaction closeFile(URI uri) {
        var file = this.files.get(uri);
        if (file != null) {
            file.close();
        }
        return null;
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized @Nullable FileTransaction saveFile(URI uri) {
        // do nothing
        return null;
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized @Nullable FileTransaction deleteFile(URI uri) {
        var prev = this.files.remove(uri);
        if (prev != null) {
            return new FileTransaction.RemovedFile(prev);
        }
        return null;
    }

    @Override
    @CheckReturnValue
    @Contract(mutates = "this")
    public synchronized @Nullable FileTransaction reloadFromDisk(URI uri, String diskContent) {
        var existing = this.files.get(uri);
        if (existing != null) {
            if (existing.isOpen()) {
                return null;
            }
            if (existing.content().equals(diskContent)) {
                return null;
            } else {
                existing.updateContent(diskContent, 0);
                return new FileTransaction.UpdateFile(existing, false);
            }
        } else {
            var newFile = new VirtualFile(uri, diskContent, 0, false);
            this.files.put(uri, newFile);
            return new FileTransaction.UpdateFile(newFile, true);
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
    public synchronized @Nullable String getContent(URI uri) {
        var file = this.files.get(uri);
        if (file == null) {
            return null;
        }
        return file.content();
    }

    @Override
    public List<VirtualFile> files() {
        return List.copyOf(this.files.values());
    }


}
