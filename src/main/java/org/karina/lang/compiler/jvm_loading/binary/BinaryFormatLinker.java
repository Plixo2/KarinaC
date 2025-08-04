package org.karina.lang.compiler.jvm_loading.binary;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.jvm_loading.binary.in.ClassReader;
import org.karina.lang.compiler.jvm_loading.binary.in.ModelReader;
import org.karina.lang.compiler.jvm_loading.binary.out.ModelWriter;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryFormatLinker {

    public static @Nullable Model readBinary(IntoContext c, String resource) {

        try (var resourceStream = ModelLoader.class.getResourceAsStream("/" + resource)) {

            if (resourceStream == null) {
                Log.fileError(c, new FileLoadError.Resource(new FileNotFoundException(
                        "Could not find resource: '" + resource + "'"
                )));
                throw new Log.KarinaException();
            }

            try (var stream = new GZIPInputStream(resourceStream)) {
                var reader = new ModelReader(stream);
                return reader.read(c.intoContext());
            }

        } catch (IOException e) {
            Log.warn(c, Objects.requireNonNullElse(e.getMessage(), "An error occurred while reading the binary model."));
            return null;
        }
    }

    public static boolean canReadCache(String resource) {
        Log.begin("can-read-cache");
        try (var resourceStream = ModelLoader.class.getResourceAsStream("/" + resource)) {
            if (resourceStream == null) {
                Log.end("can-read-cache", "Resource not found");
                return false;
            }
            try (var stream = new GZIPInputStream(resourceStream)){
                var outerReader = new ClassReader(stream);
                var magicNumber = outerReader.readInt();
                if (magicNumber != KarinaCompiler.BINARY_MAGIC_NUMBER) {
                    Log.end("can-read-cache", "Wrong magic number: " + magicNumber);
                    return false;
                }
                var formatVersion = outerReader.readInt();
                if (formatVersion != KarinaCompiler.BINARY_VERSION) {
                    var formatMajor = formatVersion >> 16;
                    var formatMinor = formatVersion & 0xFFFF;
                    Log.end("can-read-cache", "Wrong format version: " + formatMajor + "." + formatMinor);
                    return false;
                }
            }
            Log.end("can-read-cache", "Cache is valid");
            return true;

        } catch (IOException e) {
            Log.end("can-read-cache", "Error: " + e.getMessage());
            return false;
        }
    }

    public static void writeBinary(IntoContext c, Model model, Path path) {
        path = path.toAbsolutePath().normalize();
        var asFile = path.toFile();
        try {
            if (!asFile.exists()) {
                var _ = asFile.getParentFile().mkdirs();
                var _ = asFile.createNewFile();
            }

            try (var fos = new FileOutputStream(asFile);
                 var gzipOut = new GZIPOutputStream(fos)) {

                var writer = new ModelWriter(gzipOut);
                writer.write(model);

            }
        } catch(IOException e) {
            Log.fileError(c, new FileLoadError.IO(asFile, e));
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
