package org.karina.lang.compiler.jvm_loading.binary;

import org.karina.lang.compiler.jvm_loading.binary.in.ModelReader;
import org.karina.lang.compiler.jvm_loading.binary.out.ModelWriter;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.Model;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryFormatLinker {

    public static void load() {

    }

    public static Model readBinary(String resource) {

        try (var resourceStream = ModelLoader.class.getResourceAsStream(resource)) {

            if (resourceStream == null) {
                Log.fileError(new FileLoadError.Resource(new FileNotFoundException(
                        "Could not find resource: '" + resource + "'"
                )));
                throw new Log.KarinaException();
            }

            try (var stream = new GZIPInputStream(resourceStream)){
                var reader = new ModelReader(stream);
                return reader.read();
            }

        } catch (IOException e) {
            Log.fileError(new FileLoadError.Resource(e));
            throw new Log.KarinaException();
        }
    }

    public static void writeBinary(Model model, Path path) {
        path = path.toAbsolutePath().normalize();
        var asFile = path.toFile();
        if (!asFile.exists()) {
            try {
                var _ = asFile.getParentFile().mkdirs();
                var _ = asFile.createNewFile();
            } catch (IOException e) {
                Log.fileError(new FileLoadError.IO(asFile, e));
                throw new Log.KarinaException();
            }
        }

        try (var fos = new FileOutputStream(asFile);
             var gzipOut = new GZIPOutputStream(fos)) {

            var writer = new ModelWriter(gzipOut);
            writer.write(model);

        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(asFile, e));
            throw new Log.KarinaException();
        }

    }

    public static long readHash(InputStream stream) throws IOException {
        return (
                (long) stream.read() << 56) |
                ((long) stream.read() << 48) |
                ((long) stream.read() << 40) |
                ((long) stream.read() << 32) |
                ((long) stream.read() << 24) |
                ((long) stream.read() << 16) |
                ((long) stream.read() << 8) |
                (long) stream.read();
    }

    public static void writeHash(OutputStream stream, long hash) throws IOException {
        stream.write((int) (hash >> 56));
        stream.write((int) (hash >> 48));
        stream.write((int) (hash >> 40));
        stream.write((int) (hash >> 32));
        stream.write((int) (hash >> 24));
        stream.write((int) (hash >> 16));
        stream.write((int) (hash >> 8));
        stream.write((int) hash);
    }

}
