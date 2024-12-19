package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileLoader {

    //#region Load UTF-8 File
    public static TextSource loadUTF8(String path) throws Log.KarinaException {
        var file = new File(path);
        var lines = loadUTF8File(file);
        return new TextSource.DefaultFile(new TextSource.FileResource(file), lines);
    }

    public static String loadUTF8FiletoString(File file) throws Log.KarinaException {
        if (!file.exists()) {
            Log.fileError(new FileLoadError.NotFound(file));
            throw new Log.KarinaException();
        }
        if (!file.isFile()) {
            Log.fileError(new FileLoadError.NotAFile(file));
            throw new Log.KarinaException();
        }
        if (!file.canRead()) {
            Log.fileError(new FileLoadError.NOPermission(file));
            throw new Log.KarinaException();
        }
        try {
            var path = file.getAbsoluteFile().toPath().normalize();
            var charset = StandardCharsets.UTF_8;
            return Files.readString(path, charset);
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private static List<String> loadUTF8File(File file) throws Log.KarinaException {

        if (!file.exists()) {
            Log.fileError(new FileLoadError.NotFound(file));
            throw new Log.KarinaException();
        }
        if (!file.isFile()) {
            Log.fileError(new FileLoadError.NotAFile(file));
            throw new Log.KarinaException();
        }
        if (!file.canRead()) {
            Log.fileError(new FileLoadError.NOPermission(file));
            throw new Log.KarinaException();
        }
        try {
            var path = file.getAbsoluteFile().toPath().normalize();
            var charset = StandardCharsets.UTF_8;
            return Files.readAllLines(path, charset);
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }

    }
    //#endregion

    //#region Load File Tree
    public static FileTreeNodeImpl loadTree(
            @Nullable ObjectPath objectPath,
            String path,
            Predicate<String> filePredicate
    ) throws Log.KarinaException {

        var file = new File(path);
        var folderName = getFileNameWithoutExtension(file.getName());
        if (objectPath == null) {
            objectPath = new ObjectPath(folderName);
        }
        if (!file.exists()) {
            Log.fileError(new FileLoadError.NotFound(file));
            throw new Log.KarinaException();
        }
        if (!file.isDirectory()) {
            Log.fileError(new FileLoadError.NotAFolder(file));
            throw new Log.KarinaException();
        }
        if (!file.canRead()) {
            Log.fileError(new FileLoadError.NOPermission(file));
            throw new Log.KarinaException();
        }
        var files = file.listFiles();
        if (files == null) {
            Log.fileError(new FileLoadError.IO(file, new IOException("Cannot list files")));
            throw new Log.KarinaException();
        }
        return loadTreeFiles(files, objectPath, folderName, filePredicate);

    }

    private static FileTreeNodeImpl loadTreeFiles(
            File[] files,
            ObjectPath objectPath,
            String folderName,
            Predicate<String> filePredicate
    ) {

        var children = new ArrayList<FileTreeNodeImpl>();
        var leafs = new ArrayList<FileTreeNode.FileNode>();
        for (var subFile : files) {
            if (!subFile.exists()) {
                Log.fileError(new FileLoadError.NotFound(subFile));
                throw new Log.KarinaException();
            }
            var name = getFileNameWithoutExtension(subFile.getName());
            if (name.isEmpty()) {
                continue;
            }
            var childPath = objectPath.append(name);
            if (subFile.isDirectory()) {
                var child = loadTree(childPath, subFile.getAbsolutePath(), filePredicate);
                children.add(child);
            } else if (subFile.isFile() && filePredicate.test(subFile.getName())) {
                var lines = loadUTF8File(subFile);
                var src = new TextSource.DefaultFile(new TextSource.FileResource(subFile), lines);
                leafs.add(new FileTreeNodeImpl.FileNodeImpl(childPath, name, src));
            }
        }
        return new FileTreeNodeImpl(objectPath, folderName, children, leafs);

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

    public static List<Path> getFiles(Path directory) {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
