package org.karina.lang.compiler.jvm_loading.loading;

import org.karina.lang.compiler.jvm_loading.binary.BinaryFormatLinker;
import org.karina.lang.compiler.logging.ColorOut;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.LogColor;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

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

    public ModelLoader() {
    }


    public Model getJarModel() {

        if (binFileExist()) {
            ColorOut.begin(LogColor.WHITE)
                    .append("using cache '")
                    .append(BIN_FILE)
                    .append("'")
                    .out(System.out);

            Log.begin("load-cache");
            var binary = BinaryFormatLinker.readBinary(BIN_FILE);
            Log.end("load-cache", "with " + binary.getClassCount() + " classes");

            return binary;
        }

        return rebuildCache();
    }

    private Model rebuildCache() {
        Log.begin("rebuild-cache");
        var loadedModel = modelFromResource(RESOURCE_LIBRARIES);
        //TODO add condition to only generate binary cache when needed
        BinaryFormatLinker.writeBinary(loadedModel, Path.of("src/main/resources/", BIN_FILE));

        Log.end("rebuild-cache");
        return loadedModel;
    }


    private boolean binFileExist() {
        try (var resourceStream = ModelLoader.class.getResourceAsStream(BIN_FILE)) {
            return resourceStream != null;
        } catch (IOException e) {
            return false;
        }
    }

    private Model modelFromResource(List<ResourceLibrary> libraries) {
        Log.begin("jar-load");

        var models = new Model[libraries.size()];
        for (var i = 0; i < libraries.size(); i++) {
            var library = libraries.get(i);
            Log.begin(library.name);
            var lib = loadFromResource(library);
            Log.end(library.name, "with " + lib.getClassCount() + " classes");
            models[i] = lib;
        }
        var merged = ModelBuilder.merge(models);
        Log.end("jar-load", "with " + merged.getClassCount() + " classes");
        return merged;
    }


    private Model loadFromResource(ResourceLibrary library) {
        Log.begin("read-jar");
        var jdkSet = new OpenSet();

        var resource = "/" + library.resource;
        try (var resourceStream = ModelLoader.class.getResourceAsStream(resource)) {

            if (resourceStream == null) {
                Log.fileError(new FileLoadError.Resource(new FileNotFoundException(
                        "Could not find resource: '" + resource + "'"
                )));
                throw new Log.KarinaException();
            }

            try (var jarInputStream = new JarInputStream(resourceStream)){
                BytecodeLoading.loadJarFile(jarInputStream, jdkSet);
            }

        } catch (IOException e) {
            Log.fileError(new FileLoadError.Resource(e));
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


    private record ResourceLibrary(String name, String resource) { }

}
