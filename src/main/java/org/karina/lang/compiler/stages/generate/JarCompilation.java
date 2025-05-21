package org.karina.lang.compiler.stages.generate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.FileLoadError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Result of the Generation Stage.
 * Represents a jar file.
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class JarCompilation {
    List<JarOutput> files;
    Manifest manifest;

    private static void removePreviousFiles(File file) {
        if (file.isDirectory()) {
            var list = file.listFiles();
            if (list != null) {
                for (var subFile : list) {
                    removePreviousFiles(subFile);
                }
            }
            var ignored = file.delete();
        }
        if (file.getName().endsWith(".class")) {
            var ignored = file.delete();
        }
    }

    public void writeClasses(Path output) {
        var file = output.toFile();
        removePreviousFiles(file);
        var ignored = file.mkdirs();
        if (!Files.isDirectory(output)) {
            Log.fileError(new FileLoadError.NotAFolder(file));
            throw new Log.KarinaException();
        }
        var absolutePath = file.getAbsolutePath();
        try (var collector = new ErrorCollector()) {
            for (var jarOutput : this.files) {
                var path = absolutePath + "/" + jarOutput.path();
                var subFile = new File(path);
                collector.collect(() -> {
                    try {
                        writeByteArrayToFile(subFile, jarOutput.data());
                    } catch (IOException e) {
                        Log.fileError(new FileLoadError.IO(subFile, e));
                        throw new Log.KarinaException();
                    }
                });
            }
        }
    }

    private void writeByteArrayToFile(File file, byte[] data) throws IOException {
        var ignored = file.getParentFile().mkdirs();
        var ignored1 = file.createNewFile();
        try (var stream = new FileOutputStream(file)) {
            stream.write(data);
        }
    }

    public void writeJar(Path output) {
        var file = output.toFile();
        var writeTime = System.currentTimeMillis();
        try {
            var ignored = file.getParentFile().mkdirs();
            var ignored1 = file.createNewFile();

            try (var stream = new FileOutputStream(file)) {
                write(writeTime, stream, this.files, this.manifest);
            }
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private static void write(long writeTime, OutputStream out, List<JarOutput> files, Manifest manifest) throws IOException {
        try (var target = new JarOutputStream(out, manifest)){
            for (var jarOutput : files) {
                var entry = new JarEntry(jarOutput.path());
                target.putNextEntry(entry);
                var data = jarOutput.data();
                target.write(data, 0, data.length);
                entry.setTime(writeTime);
                target.closeEntry();
            }
        }
    }

    public record JarOutput(String path, byte[] data) {}
}
