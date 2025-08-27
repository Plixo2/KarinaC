package org.karina.lang.lsp.lib;

import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.compiler.utils.TextSource;

import java.net.URI;

public interface VirtualFile extends Resource, TextSource {

    /// Always absolut and normalized
    URI uri();

    String content();

    int version();

    boolean isOpen();

    @Contract(mutates = "this")
    void updateContent(String newContent, int version);

    @Contract(mutates = "this")
    void open();

    @Contract(mutates = "this")
    void close();


    @Override
    default String identifier() {
        return uri().toString();
    }

    @Override
    default Resource resource() {
        return this;
    }
}
