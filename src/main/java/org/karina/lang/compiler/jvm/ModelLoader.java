package org.karina.lang.compiler.jvm;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.jvm.binary.in.ModelReader;
import org.karina.lang.compiler.jvm.binary.out.ModelWriter;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ModelLoader {
    private final InterfaceBuilder interfaceBuilder;

    public ModelLoader() {
        this.interfaceBuilder = new InterfaceBuilder();
    }

    public static final String RESOURCES_JDK_BIN_GZ = "resources/jdk.bin";

    private URL getJarURL() {
        var classLoader = InterfaceBuilder.class.getClassLoader();
        var resource = classLoader.getResource("jdk.jar");
        if (resource == null) {
            Log.fileError(new FileLoadError.NotFound(new File("jdk.jar")));
            throw new Log.KarinaException();
        }
        return resource;
    }

    private void writeJar() {
        var startTime = System.currentTimeMillis();
        var modelBuilder = new JKModelBuilder();
        var maxClassVersion = 0;
        var file = new File(getJarURL().getFile());
        try {
            var classes = this.interfaceBuilder.loadJarFile(file);
            for (var node : classes) {
                modelBuilder.addClass(node);
                maxClassVersion = Math.max(maxClassVersion, node.version());
            }
        } catch(IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
        var model = modelBuilder.build();
        var hash = file.lastModified();
        var time = System.currentTimeMillis() - startTime;
        var message = """
                Loaded JDK with %d classes in %dms
                Bytecode %d, Java %d
                Hash: %d
                """.formatted(model.classModels().size(), time, maxClassVersion, (maxClassVersion - 44),
                hash
        );
        System.out.println(message);

        writeBinary(model, hash, RESOURCES_JDK_BIN_GZ);

    }

    public JKModel loadJDK() {
        var file = new File(getJarURL().getFile());
        var expectedHash = file.lastModified();
        var binHash = readHash(RESOURCES_JDK_BIN_GZ);
        if (binHash != expectedHash) {
            System.out.println("JDK Binary out of date, rebuilding");
            writeJar();
        }

        var startTime = System.currentTimeMillis();
        var model = readBinary(RESOURCES_JDK_BIN_GZ);
        System.out.println("Reading JDK (" + (System.currentTimeMillis() - startTime) + "ms)");
        System.out.println("Loaded " + model.classModels().size() + " classes");

        return model;
    }


    private void writeBinary(JKModel model, long hash, String path) {
        var file = new java.io.File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                Log.fileError(new FileLoadError.IO(file, e));
                throw new Log.KarinaException();
            }
        }

        try (var fos = new FileOutputStream(path);
             var gzipOut = new GZIPOutputStream(fos)) {

            var writer = new ModelWriter(gzipOut);
            writer.writeHash(hash);
            writer.write(model);

        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private JKModel readBinary(String path) {
        var file = new File(path);
        if (!file.exists()) {
            Log.fileError(new FileLoadError.NotFound(file));
            throw new Log.KarinaException();
        }

        try (var fos = new FileInputStream(path);
             var gzipOut = new GZIPInputStream(fos);
             var buffered = new BufferedInputStream(gzipOut)) {

            var reader = new ModelReader(buffered);
            var ignored = reader.readHash();

            return reader.read(file);
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

    private long readHash(String path) {
        var file = new File(path);
        if (!file.exists()) {
            return -1;
        }

        try (var fos = new FileInputStream(path);
             var gzipOut = new GZIPInputStream(fos);
             var buffered = new BufferedInputStream(gzipOut)) {

            var reader = new ModelReader(buffered);
            return reader.readHash();
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
    }

}
