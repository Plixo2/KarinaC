package org.karina.lang.lsp.lib;

import org.jetbrains.annotations.UnmodifiableView;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

public interface RealFileSystem {

    IOResult<String> readFileFromDisk(URI uri);
    boolean exists(URI uri);
    boolean isDirectory(URI uri);

    /// @return a list of absolute and normalized URIs
    @UnmodifiableView
    IOResult<List<URI>> listAllFilesRecursively(URI uri, Predicate<String> filter);

}
