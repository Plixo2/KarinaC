package org.karina.lang.compiler.logging.errors;

import java.io.File;

public sealed interface FileLoadError extends Error {
    File file();


    record NotFound(File file) implements FileLoadError {
    }

    record NotAFile(File file) implements FileLoadError {
    }

    record NotAFolder(File file) implements FileLoadError {
    }

    record NOPermission(File file) implements FileLoadError {
    }

    record IO(File file, Exception exception) implements FileLoadError {
    }

    record Generic(File file, String message) implements FileLoadError {
    }

    default String errorMessage() {
        return switch (this) {
            case NotFound ignored -> "Not found";
            case NotAFile ignored -> "Not a file";
            case NotAFolder ignored -> "Not a folder";
            case NOPermission ignored -> "No permission";
            case Generic json -> json.message();
            case IO io -> io.exception().getMessage();
        };
    }
}
