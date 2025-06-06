package org.karina.lang.compiler.utils;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileLoader {

    //#region Load UTF-8 File
    public static TextSource loadUTF8(String path) throws IOException {
        var file = new File(path);
        var lines = loadUTF8File(file);
        return new TextSource(new FileResource(file), lines);
    }

    public static String loadUTF8FiletoString(File file) throws IOException {
        testValidity(file);
        var path = file.getAbsoluteFile().toPath().normalize();
        var charset = StandardCharsets.UTF_8;
        return Files.readString(path, charset);
    }

    private static String loadUTF8File(File file) throws IOException {
        testValidity(file);
        var path = file.getAbsoluteFile().toPath().normalize();
        var charset = StandardCharsets.UTF_8;
        return Files.readString(path, charset);
    }

    private static void testValidity(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        if (!file.isFile()) {
            throw new NoSuchFileException(file.toString(), null, "not a file");
        }
        if (!file.canRead()) {
            throw new AccessDeniedException(file.toString());
        }
    }
    //#endregion

    //#region Load File Tree
    public static DefaultFileTree loadTree(
            Path path
    ) throws IOException {
        path = path.toAbsolutePath().normalize();
        return loadTree(null, path.toString(), new FilePredicate("krna"));
    }


    private static DefaultFileTree loadTree(
            @Nullable ObjectPath objectPath,
            String path,
            Predicate<String> filePredicate
    ) throws IOException {

        var file = new File(path);
        var folderName = getFileNameWithoutExtension(file.getName());
        if (!file.exists()) {
            throw new FileNotFoundException(
                    file.toString()
            );
        }
        if (!file.isDirectory()) {
            throw new NotDirectoryException(
                file.toString()
            );
        }
        if (!file.canRead()) {
            throw new AccessDeniedException(
                    file.toString()
            );
        }
        var files = file.listFiles();
        if (files == null) {
            throw new IOException("Can't list files");
        }
        var tree = loadTreeFiles(files, objectPath, folderName, filePredicate);
        return tree;

    }

    private static DefaultFileTree loadTreeFiles(
            File[] files,
            @Nullable ObjectPath objectPath,
            String folderName,
            Predicate<String> filePredicate
    ) throws IOException {

        var children = new ArrayList<DefaultFileTree>();
        var leafs = new ArrayList<FileNode<TextSource>>();
        for (var subFile : files) {
            if (!subFile.exists()) {
                throw new FileNotFoundException(
                        subFile.toString()
                );
            }
            var name = getFileNameWithoutExtension(subFile.getName());
            if (name.isEmpty()) {
                continue;
            }
            var childPath = objectPath == null ? new ObjectPath(name): objectPath.append(name);
            if (subFile.isDirectory()) {
                var child = loadTree(childPath, subFile.getAbsolutePath(), filePredicate);
                children.add(child);
            } else if (subFile.isFile() && filePredicate.test(subFile.getName())) {
                var lines = loadUTF8File(subFile);
                var src = new TextSource(new FileResource(subFile), lines);
                leafs.add(new DefaultFileTree.DefaultFileNode(childPath, name, src));
            }
        }
        return new DefaultFileTree(objectPath, folderName, children, leafs);

    }

    //#endregion

    public static String getFileNameWithoutExtension(String filePath) {

        int lastSeparatorIndex = filePath.lastIndexOf('/');
        String fileName;
        if (lastSeparatorIndex == -1) {
            fileName = filePath;
        } else {
            fileName = (filePath.substring(lastSeparatorIndex + 1));
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        } else {
            return fileName.substring(0, lastDotIndex);
        }

    }

    public static String getFileExtension(String filePath) {

        if (filePath.isEmpty()) {
            return "";
        }
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        } else {
            return filePath.substring(lastDotIndex + 1);
        }

    }

    @AllArgsConstructor
    public static class FilePredicate implements Predicate<String> {
        private final String extension;
        @Override
        public boolean test(String path) {

            var extension = FileLoader.getFileExtension(path);
            return this.extension.equals(extension);

        }
    }

}
