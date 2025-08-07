package org.karina.lang.lsp.lib;

import org.karina.lang.lsp.KarinaLanguageServer;

import java.io.IOException;
import java.net.URI;

public interface RealFileSystem {

    IOResult<String> readFileFromDisk(URI uri);


    sealed interface IOResult<T> {


        /// Sends a message with the error and returns null.
        default T orMessageAndNull(KarinaLanguageServer kls) {
            return switch (this) {
                case IOResult.Success<T> v -> v.value();
                case IOResult.Error<T> v -> {
                    kls.errorMessage(v.exception());
                    yield null;
                }
            };
        }


        record Success<T>(T value) implements IOResult<T> {}
        record Error<T>(IOException exception) implements IOResult<T> {}
    }

}
