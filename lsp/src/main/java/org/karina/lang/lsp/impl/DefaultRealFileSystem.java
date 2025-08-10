package org.karina.lang.lsp.impl;

import karina.lang.Result;
import org.karina.lang.lsp.lib.RealFileSystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class DefaultRealFileSystem implements RealFileSystem {

    @Override
    public Result<String, IOException> readFileFromDisk(URI uri) {
        return Result.safeCallExpect(IOException.class, () -> loadFileContent(uri));
    }
    private static String loadFileContent(URI uri) throws IOException {
        var path = Path.of(uri);
        return Files.readString(path);
    }

    @Override
    public boolean exists(URI uri) {
        return Files.exists(Path.of(uri));
    }

    @Override
    public boolean isDirectory(URI uri) {
        return Files.isDirectory(Path.of(uri));
    }

    @Override
    public Result<List<URI>, IOException> listAllFilesRecursively(URI uri, Predicate<String> filter) {
        var root = Path.of(uri);


        if (!Files.exists(root)) {
            return Result.err(new IOException("Path does not exist: " + uri));
        }
        if (!Files.isDirectory(root)) {
            return Result.err(new IOException("Path is not a directory: " + uri));
        }
        return Result.safeCallWithResource(IOException.class, () -> Files.walk(root),
                res -> res
                    .filter(Files::isRegularFile)
                    .filter(p -> filter.test(p.getFileName().toString())) // apply name filter
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .map(Path::toUri).toList()
        );

    }

}
