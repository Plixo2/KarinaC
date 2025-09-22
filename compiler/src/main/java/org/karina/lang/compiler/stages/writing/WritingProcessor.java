package org.karina.lang.compiler.stages.writing;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Config;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.FileLoadError;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.utils.Context;

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

public class WritingProcessor {

   public void writeCompilation(Context c, JarCompilation compilation, @Nullable Config.OutputConfig outputConfig) throws Log.KarinaException {

       if (outputConfig == null) {
           return;
       }
       var outputPath = outputConfig.outputFile();

       writeJar(c, compilation, outputPath);

       if (outputConfig.emitClassFiles()) {
           writeClasses(c, compilation, outputPath.getParent().resolve("classes"));
       }

   }


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

    private void writeClasses(Context c, JarCompilation compilation, Path output) {
        var file = output.toFile();
        removePreviousFiles(file);
        var ignored = file.mkdirs();
        if (!Files.isDirectory(output)) {
            Log.fileError(c, new FileLoadError.NotAFolder(file));
            throw new Log.KarinaException();
        }
        var absolutePath = file.getAbsolutePath();
        try (var fork = c.fork()) {
            for (var jarOutput : compilation.files()) {
                var path = absolutePath + "/" + jarOutput.path();
                var subFile = new File(path);
                fork.collect(subC -> {
                    try {
                        writeByteArrayToFile(subFile, jarOutput.data());
                    } catch (IOException e) {
                        Log.fileError(subC, new FileLoadError.IO(subFile, e));
                        throw new Log.KarinaException();
                    }
                    // yield nothing
                    return null;
                });
            }
            var _ = fork.dispatchParallel();
        }
    }

    private void writeByteArrayToFile(File file, byte[] data) throws IOException {
        var ignored = file.getParentFile().mkdirs();
        var ignored1 = file.createNewFile();
        try (var stream = new FileOutputStream(file)) {
            stream.write(data);
        }
    }

    private void writeJar(Context c, JarCompilation compilation, Path output) {
        var file = output.toFile();
        var writeTime = System.currentTimeMillis();
        try {
            var ignored = file.getParentFile().mkdirs();
            var ignored1 = file.createNewFile();

            try (var stream = new FileOutputStream(file)) {
                write(writeTime, stream, compilation.files(), compilation.manifest());
            }
        } catch (IOException e) {
            Log.fileError(c, new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private static void write(long writeTime, OutputStream out, List<JarCompilation.JarOutput> files, Manifest manifest) throws IOException {
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

}
