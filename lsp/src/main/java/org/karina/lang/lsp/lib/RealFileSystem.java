package org.karina.lang.lsp.lib;

import karina.lang.Result;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

public interface RealFileSystem {

    Result<String, IOException> readFileFromDisk(URI uri);
    boolean exists(URI uri);
    boolean isDirectory(URI uri);

    /// @return a list of absolute and normalized URIs
    @UnmodifiableView
    Result<List<URI>, IOException> listAllFilesRecursively(URI uri, Predicate<String> filter);

}
