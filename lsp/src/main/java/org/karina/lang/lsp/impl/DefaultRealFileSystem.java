package org.karina.lang.lsp.impl;

import org.karina.lang.lsp.lib.RealFileSystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
