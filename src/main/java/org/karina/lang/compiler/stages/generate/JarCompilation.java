package org.karina.lang.compiler.stages.generate;


import lombok.AllArgsConstructor;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.FileLoadError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@AllArgsConstructor
public class JarCompilation {
    List<JarOutput> files;
    Manifest manifest;

    public void dump(Path output) {
        var file = new File(output.toAbsolutePath().normalize().toString()).getParentFile();
        var ignored = file.mkdirs();
        if (!file.isDirectory()) {
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

    public void write(Path output) {
        var file = new File(output.toAbsolutePath().normalize().toString());
        try {
            var ignored = file.getParentFile().mkdirs();
            var ignored1 = file.createNewFile();

            try (var stream = new FileOutputStream(file)) {
                write(stream, this.files, this.manifest);
            }
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private static void write(OutputStream out, List<JarOutput> files, Manifest manifest) throws IOException {
        try (var target = new JarOutputStream(out,manifest)){
            for (var jarOutput : files) {
                var entry = new JarEntry(jarOutput.path());
                target.putNextEntry(entry);
                var data = jarOutput.data();
                target.write(data, 0, data.length);
                entry.setTime(System.currentTimeMillis());
                target.closeEntry();
            }
        }
    }

    public record JarOutput(String path, byte[] data) {
    }
}
