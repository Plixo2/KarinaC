package org.karina.lang.compiler.jvm_loading.loading;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.jvm_loading.binary.BinaryFormatLinker;
import org.karina.lang.compiler.logging.ColorOut;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.LogColor;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarInputStream;

public class ModelLoader {
    private static final List<ResourceLibrary> RESOURCE_LIBRARIES = List.of(
            new ResourceLibrary("java-base", "java_base.jar"),
            new ResourceLibrary("karina-base", "karina_base.jar")
    );

    private static final String BIN_FILE = "/base.bin.gz";



    public Model getJarModel(Context c) {

        if (!System.getProperty("karina.binary", "false").equals("true")) {
            if (System.getProperty("karina.cli", "false").equals("true")) {
                ColorOut.begin(LogColor.GRAY)
                        .append("- not using binary format, use '-b'/'--binary' to enable it")
                        .out(System.out);
            } else {
                ColorOut.begin(LogColor.GRAY)
                        .append("- not using binary format, set '-Dkarina.binary=true' to enable it")
                        .out(System.out);
            }
            return modelFromResource(c, RESOURCE_LIBRARIES);
        }

        if (binFileExist()) {
            ColorOut.begin(LogColor.GRAY)
                    .append("- using cache '")
                    .append(BIN_FILE)
                    .append("'")
                    .out(System.out);

            Log.begin("load-cache");
            var binary = BinaryFormatLinker.readBinary(c, BIN_FILE);
            if (binary == null) {
                Log.end("load-cache", "cannot load cache, rebuilding ...");
                return rebuildCache(c);
            }
            Log.end("load-cache", "with " + binary.getClassCount() + " classes");
            return binary;
        }
        if (System.getProperty("karina.cli", "false").equals("true")) {
            ColorOut.begin(LogColor.GRAY)
                    .append("- no cache found")
                    .out(System.out);

            return modelFromResource(c, RESOURCE_LIBRARIES);
        }


        return rebuildCache(c);
    }

    private static Model rebuildCache(Context c) {
        Log.begin("rebuild-cache");
        var loadedModel = modelFromResource(c, RESOURCE_LIBRARIES);
        //TODO add condition to only generate binary cache when needed
        BinaryFormatLinker.writeBinary(c, loadedModel, Path.of("src/main/resources/", BIN_FILE));

        Log.end("rebuild-cache");
        return loadedModel;
    }


    private static boolean binFileExist() {
        try (var resourceStream = ModelLoader.class.getResourceAsStream(BIN_FILE)) {
            return resourceStream != null;
        } catch (IOException e) {
            return false;
        }
    }

    private static Model modelFromResource(Context c, List<ResourceLibrary> libraries) {
        Log.begin("jar-load");

        var models = new Model[libraries.size()];
        for (var i = 0; i < libraries.size(); i++) {
            var library = libraries.get(i);
            Log.begin(library.name);
            var lib = loadFromResource(c, library);
            Log.end(library.name, "with " + lib.getClassCount() + " classes");
            models[i] = lib;
        }
        var merged = ModelBuilder.merge(c, models);
        Log.end("jar-load", "with " + merged.getClassCount() + " classes");
        return merged;
    }


    private static Model loadFromResource(Context c, ResourceLibrary library) {
        Log.begin("read-jar");
        var jdkSet = new OpenSet();

        var resource = "/" + library.resource;
        try (var resourceStream = ModelLoader.class.getResourceAsStream(resource)) {

            if (resourceStream == null) {
                Log.fileError(c, new FileLoadError.Resource(new FileNotFoundException(
                        "Could not find resource: '" + resource + "'"
                )));
                throw new Log.KarinaException();
            }

            try (var jarInputStream = new JarInputStream(resourceStream)){
                BytecodeLoading.loadJarFile(jarInputStream, jdkSet);
            }

        } catch (IOException e) {
            Log.fileError(c, new FileLoadError.Resource(e));
            throw new Log.KarinaException();
        }
        Log.end("read-jar");

        Log.begin("link-jar");
        var builder = new ModelBuilder();
        var linker = new InterfaceLinker();

        for (var topClass : jdkSet.removeTopClasses()) {
            var _ = linker.createClass(c, null, topClass, jdkSet, new HashSet<>(), builder, null);
        }

        for (var value : jdkSet.getOpenSet().values()) {
            if (value.node().outerClass == null) {
                continue;
            }
            Log.bytecode(c, value.getSource(), value.node().name, "Could not be linked");
            throw new Log.KarinaException();
        }


        var build = builder.build(c);
        Log.end("link-jar");
        return build;
    }


    private record ResourceLibrary(String name, String resource) { }

}
