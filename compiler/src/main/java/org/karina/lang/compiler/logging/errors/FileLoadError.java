package org.karina.lang.compiler.logging.errors;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.ErrorInformation;

import java.io.File;

public sealed interface FileLoadError extends Error {
    @Nullable File file();

    @Override
    default void addInformation(ErrorInformation builder) {
        builder.setTitle("File Handling");
        var file = this.file();
        if (file != null) {
            var path = file.getAbsolutePath().replace("\\", "/");
            builder.append("File: file:///").append(path);
        }
        builder.append("Type: ").append(this.errorMessage());
    }

    record NotFound(File file) implements FileLoadError { }

    record NotAFile(File file) implements FileLoadError { }

    record NotAFolder(File file) implements FileLoadError { }

    record NOPermission(File file) implements FileLoadError { }

    record IO(File file, Exception exception) implements FileLoadError { }

    record Resource(Exception exception) implements FileLoadError {
        @Override
        public @Nullable File file() {
            return null;
        }
    }

    record BinaryFile(String message) implements FileLoadError {
        @Override
        public @Nullable File file() {
            return null;
        }
    }

    record Generic(File file, String message) implements FileLoadError { }

    default String errorMessage() {
        return switch (this) {
            case NotFound ignored -> "Not found";
            case NotAFile ignored -> "Not a file";
            case NotAFolder ignored -> "Not a folder";
            case NOPermission ignored -> "No permission";
            case Generic json -> json.message();
            case IO io -> io.exception().getMessage();
            case Resource resource -> resource.exception().getMessage();
            case BinaryFile binary -> binary.message();
        };
    }
}
