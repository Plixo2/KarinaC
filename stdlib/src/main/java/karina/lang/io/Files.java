package karina.lang.io;

import com.sun.tools.javac.Main;
import karina.lang.Option;
import karina.lang.Result;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Files {

    public static Result<String, IOException> readString(Path path) {
        try {
            return Result.ok(java.nio.file.Files.readString(path));
        } catch(IOException e) {
            return Result.err(e);
        }
    }


    public static Result<List<String>, IOException> readAllLines(Path path) {
        try {
            return Result.ok(java.nio.file.Files.readAllLines(path));
        } catch(IOException e) {
            return Result.err(e);
        }
    }

    public static Option<IOException> write(Path path, String content) {
        try {
            java.nio.file.Files.writeString(path, content);
            return Option.none();
        } catch(IOException e) {
            return Option.some(e);
        }
    }

    public static Option<IOException> write(Path path, String content, StandardOpenOption... options) {
        try {
            java.nio.file.Files.writeString(path, content, options);
            return Option.none();
        } catch(IOException e) {
            return Option.some(e);
        }
    }

    public static Option<IOException> createDirectory(Path path) {
        try {
            java.nio.file.Files.createDirectories(path);
            return Option.none();
        } catch(IOException e) {
            return Option.some(e);
        }
    }

    public static Option<IOException> createFile(Path path) {
        try {
            java.nio.file.Files.createFile(path);
            return Option.none();
        } catch(IOException e) {
            return Option.some(e);
        }
    }


    public static Result<Path, InvalidPathException> path(String path) {
        try {
            return Result.ok(Path.of(path));
        } catch(InvalidPathException e) {
            return Result.err(e);
        }
    }

    public static boolean doesExist(Path path) {
        return java.nio.file.Files.exists(path);
    }


    public static boolean isReadable(Path path) {
        return java.nio.file.Files.isReadable(path);
    }


    public static boolean isFile(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }


    public static boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }


    public static boolean isWritable(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }



}
