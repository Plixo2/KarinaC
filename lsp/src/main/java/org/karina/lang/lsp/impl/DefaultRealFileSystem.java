package org.karina.lang.lsp.impl;

import org.karina.lang.lsp.lib.IOResult;
import org.karina.lang.lsp.lib.RealFileSystem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultRealFileSystem implements RealFileSystem {

    public IOResult<String> readFileFromDisk(URI uri) {

        try {
            return new IOResult.Success<>(loadFileContent(uri));
        } catch(IOException e) {
            return new IOResult.Error<>(e);
        }

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
    public IOResult<List<URI>> listAllFilesRecursively(URI uri, Predicate<String> filter) {
        var root = Path.of(uri);

        if (!Files.exists(root) || !Files.isDirectory(root)) {
            return new IOResult.Error<>(new IOException("Path does not exist or is not a directory: " + uri));
        }

        try (var fileStream = Files.walk(root)) {
            var result = fileStream
                    .filter(Files::isRegularFile)
                    .filter(p -> filter.test(p.getFileName().toString())) // apply name filter
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .map(Path::toUri).toList();
            return new IOResult.Success<>(result);
        } catch (IOException e) {
            return new IOResult.Error<>(e);
        }

    }

}
