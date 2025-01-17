package org.karina.lang.lsp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.FileResource;
import org.karina.lang.compiler.api.Resource;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.lsp.fs.ConfigFile;
import org.karina.lang.lsp.fs.KarinaFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLoading {

    public static FileLoadResult<List<Path>> getAllKarinaFiles(Path directory) {
        if (!Files.exists(directory)) {
            return new FileLoadResult.FileLoadNotFound<>(directory);
        }
        if (!Files.isDirectory(directory)) {
            return new FileLoadResult.NotAFileLoad<>(directory);
        }
        if (!Files.isReadable(directory)) {
            return new FileLoadResult.NoPermission<>(directory);
        }
        try (Stream<Path> stream = Files.walk(directory)) {
            var karinaFiles = stream.filter(Files::isRegularFile)
                                    .filter(path -> path.toString().endsWith(".krna"))
                                    .collect(Collectors.toList());
            return new FileLoadResult.Success<>(karinaFiles);
        } catch (IOException e) {
            return new FileLoadResult.IO<>(directory, e);
        }
    }

    public static FileLoadResult<ConfigFile> loadConfig(Path root) {
        var loaded = load(null, root.resolve("karina-build.json"));
        return loaded.flatMap(ref -> parseString(root, root, ref));
    }

    public static FileLoadResult<TextSource> load(@Nullable KarinaFile karinaFile, Path root) {
        var file = root.toFile();
        if (!file.exists()) {
            return new FileLoadResult.FileLoadNotFound<>(root);
        }
        if (!file.isFile()) {
            return new FileLoadResult.NotAFileLoad<>(root);
        }
        if (!file.canRead()) {
            return new FileLoadResult.NoPermission<>(root);
        }
        try {
            var charset = StandardCharsets.UTF_8;
            Resource fileResource = new FileResource(file);
            if (karinaFile != null) {
                fileResource = karinaFile;
            }

            var lines = Files.readAllLines(root, charset);
            return new FileLoadResult.Success<>(new TextSource(fileResource, lines));
        } catch (IOException e) {
            return new FileLoadResult.IO<>(root, e);
        }

    }

    private static FileLoadResult<ConfigFile> parseString(Path root, Path path, TextSource source) {

        var str = String.join("\n", source.lines());
        var gson = new Gson();
        try {
            var jsonObject = gson.fromJson(str, JsonObject.class);
            if (!jsonObject.has("build")) {
                return new FileLoadResult.ParseError<>(path, "Missing 'build' object");
            } else if (!jsonObject.get("build").isJsonObject()) {
                return new FileLoadResult.ParseError<>(path, "'build' field must be a object");
            }
            var buildObject = jsonObject.getAsJsonObject("build");

            if (!buildObject.has("src")) {
                return new FileLoadResult.ParseError<>(path, "Missing 'src' in build object");
            } else if (!buildObject.get("src").isJsonPrimitive()) {
                return new FileLoadResult.ParseError<>(path, "'src' field must be a string");
            } else if (!buildObject.get("src").getAsJsonPrimitive().isString()) {
                return new FileLoadResult.ParseError<>(path, "'src' field must be a string");
            }

            var src = buildObject.get("src").getAsString();
            var toDirectory = root.resolve(src);
            if (!Files.exists(toDirectory)) {
                return new FileLoadResult.ParseError<>(path, "Directory specified in 'src' does not exist");
            } else if (!Files.isDirectory(toDirectory)) {
                return new FileLoadResult.ParseError<>(path, "Directory specified in 'src' is not a directory");
            }
            var directoryName = toDirectory.getFileName().toString();
            var configFile = new ConfigFile(Path.of(src).normalize(), directoryName);
            return new FileLoadResult.Success<>(configFile);
        } catch(JsonSyntaxException e) {
            return new FileLoadResult.ParseError<>(path, e.getMessage());
        }

    }

    public sealed interface FileLoadResult<T> {
        record Success<T>(T content) implements FileLoadResult<T> {
        }
        record FileLoadNotFound<T>(Path path) implements FileLoadResult<T> {
        }
        record NotAFileLoad<T>(Path path) implements FileLoadResult<T> {
        }
        record NoPermission<T>(Path path) implements FileLoadResult<T> {
        }
        record IO<T>(Path path, IOException e) implements FileLoadResult<T> {
        }
        record ParseError<T>(Path path, String message) implements FileLoadResult<T> {
        }

        default <R> FileLoadResult<R> map(Function<T, R> function) {

            return switch (this) {
                case FileLoadResult.FileLoadNotFound<T> v -> new FileLoadResult.FileLoadNotFound<>(v.path);
                case FileLoadResult.IO<T> v -> new FileLoadResult.IO<>(v.path, v.e);
                case FileLoadResult.NoPermission<T> v -> new FileLoadResult.NoPermission<>(v.path);
                case FileLoadResult.NotAFileLoad<T> v -> new FileLoadResult.NotAFileLoad<>(v.path);
                case FileLoadResult.ParseError<T> v -> new FileLoadResult.ParseError<>(v.path, v.message);
                case FileLoadResult.Success<T> v -> {
                    var result = function.apply(v.content);
                    yield new FileLoadResult.Success<>(result);
                }
            };

        }

        default <R> FileLoadResult<R> flatMap(Function<T, FileLoadResult<R>> function) {

            return switch (this) {
                case FileLoadResult.FileLoadNotFound<T> v -> new FileLoadResult.FileLoadNotFound<>(v.path);
                case FileLoadResult.IO<T> v -> new FileLoadResult.IO<>(v.path, v.e);
                case FileLoadResult.NoPermission<T> v -> new FileLoadResult.NoPermission<>(v.path);
                case FileLoadResult.NotAFileLoad<T> v -> new FileLoadResult.NotAFileLoad<>(v.path);
                case FileLoadResult.ParseError<T> v -> new FileLoadResult.ParseError<>(v.path, v.message);
                case FileLoadResult.Success<T> v -> function.apply(v.content);
            };

        }

        default String getErrorMessage() {

            switch (this) {
                case FileLoadNotFound<T> fileNotFound -> {
                    return "File not found: " + fileNotFound.path;
                }
                case IO<T> io -> {
                    return "IO error: " + io.e.getMessage();
                }
                case NoPermission<T> noPermission -> {
                    return "No permission to read file: " + noPermission.path;
                }
                case NotAFileLoad<T> notAFile -> {
                    return "Not a file: " + notAFile.path;
                }
                case ParseError<T> parseError -> {
                    return parseError.message;
                }
                case Success<T> ignored -> {
                    return "";
                }
            }

        }
    }
}
