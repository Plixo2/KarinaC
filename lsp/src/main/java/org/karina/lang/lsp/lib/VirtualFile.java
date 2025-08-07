package org.karina.lang.lsp.lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.compiler.utils.TextSource;

import java.net.URI;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class VirtualFile implements Resource, TextSource {
    private final URI uri;
    private String content;
    private int version;
    private boolean isOpen;


    @Contract(mutates = "this")
    public void updateContent(String newContent, int version) {
        this.content = newContent;
        this.version = version;
    }
    @Contract(mutates = "this")
    public void open() { this.isOpen = true; }
    @Contract(mutates = "this")
    public void close() { this.isOpen = false; }

    @Override
    public String identifier() {
        return this.uri.toString();
    }

    @Override
    public Resource resource() {
        return this;
    }
}
