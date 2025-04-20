package org.karina.lang.compiler.jvm.loading;

import org.karina.lang.compiler.jvm.binary.out.ModelWriter;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;

public class ModelLoader {
  //  private final InterfaceBuilder interfaceBuilder;

    public ModelLoader() {
       // this.interfaceBuilder = new InterfaceBuilder();
    }

    public static final String RESOURCES_JDK_BIN_GZ = "resources/jdk.bin";

    private URL getJarURL(String resourcePath) {
        var classLoader = ModelLoader.class.getClassLoader();
        var resource = classLoader.getResource(resourcePath);
        if (resource == null) {
            Log.fileError(new FileLoadError.NotFound(new File(resourcePath)));
            throw new Log.KarinaException();
        }
        return resource;
    }

    private Model writeJar() {
//        var startTime = System.currentTimeMillis();
//        var modelBuilder = new JKModelBuilder();
//        var maxClassVersion = 0;
//        var file = new File(getJarURL().getFile());
//        try {
//            var classes = this.interfaceBuilder.loadJarFile(file);
//            for (var node : classes) {
//                modelBuilder.addClass(node);
//                maxClassVersion = Math.max(maxClassVersion, node.version());
//            }
//
//
//        } catch(IOException e) {
//            Log.fileError(new FileLoadError.IO(file, e));
//            throw new Log.KarinaException();
//        }
//        var testA = this.interfaceBuilder.loadClassFile("org","karina", "lang", "compiler" ,"jvm", "test", "ImplType");
//        var testB = this.interfaceBuilder.loadClassFile("org","karina", "lang", "compiler" ,"jvm", "test", "SuperType");
////        modelBuilder.addClass(testA);
////        modelBuilder.addClass(testB);
//
//        var model = modelBuilder.build();
//        var hash = file.lastModified();
//        var time = System.currentTimeMillis() - startTime;
//        var message = """
//                Loaded JDK with %d classes in %dms
//                Bytecode %d, Java %d
//                Hash: %d
//                """.formatted(model.classModels().size(), time, maxClassVersion, (maxClassVersion - 44),
//                hash
//        );
//        System.out.println(message);
//
//        writeBinary(model, hash, RESOURCES_JDK_BIN_GZ);

//        return model;
        throw new NullPointerException("Not implemented");
    }

    public Model loadJavaBase() {
        return loadFromResource("java_base.jar");
    }

    public Model loadKarinaBase() {
        return loadFromResource("karina_base.jar");
    }


//        var file = new File(getJarURL().getFile());
//        var expectedHash = file.lastModified();
//        var binHash = readHash(RESOURCES_JDK_BIN_GZ);
//    //    if (binHash != expectedHash) {
//            System.out.println("JDK Binary out of date, rebuilding");
//            return writeJar();
//       // }

//        var startTime = System.currentTimeMillis();
//        var model = readBinary(RESOURCES_JDK_BIN_GZ);
//        System.out.println("Reading JDK (" + (System.currentTimeMillis() - startTime) + "ms)");
//        System.out.println("Loaded " + model.classModels().size() + " classes");
//
//        return model;


    public Model loadFromResource(String resource) {
        Log.begin("read-jar");
        var jdkSet = new OpenSet();
        var file = new File(getJarURL(resource).getFile());
        try (var jarFile = new JarFile(file)) {
            BytecodeLoading.loadJarFile(jarFile, jdkSet);
        } catch (IOException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }
        Log.end("read-jar");

        Log.begin("link-jar");
        var builder = new ModelBuilder();
        var linker = new InterfaceLinker();


        for (var topClass : jdkSet.removeTopClasses()) {
            var _ = linker.createClass(null, topClass, jdkSet, new HashSet<>(), builder);
        }

        for (var value : jdkSet.getOpenSet().values()) {
            if (value.node().outerClass == null) {
                continue;
            }
            Log.bytecode(value.getSource(), value.node().name, "Could not be linked");
            throw new Log.KarinaException();
        }


        var build = builder.build();
        Log.end("link-jar");
        return build;
    }

    private void writeBinary(Model model, long hash, String path) {
        var file = new java.io.File(path);
        if (!file.exists()) {
            try {
                var _ = file.getParentFile().mkdirs();
                var _ = file.createNewFile();
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
//
//    private Model readBinary(String path) {
//        var file = new File(path);
//        if (!file.exists()) {
//            Log.fileError(new FileLoadError.NotFound(file));
//            throw new Log.KarinaException();
//        }
//
//        try (var fos = new FileInputStream(path);
//             var gzipOut = new GZIPInputStream(fos);
//             var buffered = new BufferedInputStream(gzipOut)) {
//
//            var reader = new ModelReader(buffered);
//            var ignored = reader.readHash();
//
//            return reader.read(file);
//        } catch (IOException e) {
//            Log.fileError(new FileLoadError.IO(file, e));
//            throw new Log.KarinaException();
//        }
//    }
//
//    private long readHash(String path) {
//        var file = new File(path);
//        if (!file.exists()) {
//            return -1;
//        }
//
//        try (var fos = new FileInputStream(path);
//             var gzipOut = new GZIPInputStream(fos);
//             var buffered = new BufferedInputStream(gzipOut)) {
//
//            var reader = new ModelReader(buffered);
//            return reader.readHash();
//        } catch (IOException e) {
//            Log.fileError(new FileLoadError.IO(file, e));
//            throw new Log.KarinaException();
//        }
//    }

}
