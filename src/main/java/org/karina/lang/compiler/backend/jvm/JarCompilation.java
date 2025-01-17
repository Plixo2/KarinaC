package org.karina.lang.compiler.backend.jvm;


import lombok.AllArgsConstructor;

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

    public void dump(Path output) throws IOException {
        var file = new File(output.toAbsolutePath().normalize().toString()).getParentFile();
        var ignored = file.mkdirs();
        if (!file.isDirectory()) {
            throw new IOException("file is not a directory");
        }
        var absolutePath = file.getAbsolutePath();
        for (var jarOutput : this.files) {
            var path = absolutePath + "/" + jarOutput.path();
            writeByteArrayToFile(new File(path), jarOutput.data());
        }
    }

    private void writeByteArrayToFile(File file, byte[] data) throws IOException {
        var ignored = file.getParentFile().mkdirs();
        var ignored1 = file.createNewFile();
        try (var stream = new FileOutputStream(file)) {
            stream.write(data);
        }
    }

    public void write(Path output) throws IOException {
        var file = new File(output.toAbsolutePath().normalize().toString());
        var ignored = file.getParentFile().mkdirs();
        var ignored1 = file.createNewFile();

        try (var stream = new FileOutputStream(file)) {
            write(stream, this.files, this.manifest);
        }
    }

    private static void write(OutputStream out, List<JarOutput> files, Manifest manifest) throws IOException {
        var target = new JarOutputStream(out, manifest);
        var now = System.currentTimeMillis();
        for (var jarOutput : files) {
            var entry = new JarEntry(jarOutput.path());
            target.putNextEntry(entry);
            var data = jarOutput.data();
            target.write(data, 0, data.length);
            entry.setTime(now);
            target.closeEntry();
        }
        target.close();
    }

}
