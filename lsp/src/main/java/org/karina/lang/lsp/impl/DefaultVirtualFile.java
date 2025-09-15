package org.karina.lang.lsp.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.lsp.lib.VirtualFile;

import java.net.URI;

@Getter
@AllArgsConstructor
public class DefaultVirtualFile implements VirtualFile {

    @Accessors(fluent = true)
    private final URI uri;

    @Accessors(fluent = true)
    private String content;

    @Accessors(fluent = true)
    private int version;

    private boolean isOpen;

    @Override
    @Contract(mutates = "this")
    public void updateContent(String newContent, int version) {
        this.content = newContent;
        this.version = version;
    }

    @Override
    @Contract(mutates = "this")
    public void open() { this.isOpen = true; }


    @Override
    @Contract(mutates = "this")
    public void close() { this.isOpen = false; }


    @Override
    public String toString() {
        return "VirtualFile{" + "uri=" + this.uri + '}';
    }
}
