package org.karina.lang.compiler.jvm_loading.loading;

import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.jvm_loading.binary.BinaryFormatLinker;
import org.karina.lang.compiler.logging.*;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarInputStream;

public class ModelLoader {
    private static final List<ResourceLibrary> RESOURCE_LIBRARIES = List.of(
            new ResourceLibrary("java-base", "java_base.jar"),
            new ResourceLibrary("karina-base", "karina_base.jar")
    );

    private static final String BIN_FILE = "base.bin.gz";

    // Whether to fallback to resources if the binary cache is not valid
    private static final boolean FALLBACK_TO_RESOURCES = false;


    public static Model getJarModel(IntoContext c, boolean useBinaryFormat) {

        if (useBinaryFormat) {
            Log.begin("read-binary");
            var binModel = getBinaryModel(c);
            Log.end("read-binary", "with " + binModel.getClassCount() + " classes");
            return binModel;
        } else {
            return modelFromResource(c, RESOURCE_LIBRARIES);
        }

    }


    /// Returns a model from the binary cache or from the resources if not valid
    private static Model getBinaryModel(IntoContext c) {
        if (FALLBACK_TO_RESOURCES) {
            if (binFileExist()) {
                 return BinaryFormatLinker.readBinary(c, BIN_FILE);
            }
            Log.record("Binary cache file '" + BIN_FILE + "' does not exist, falling back to resources.");
            return modelFromResource(c, RESOURCE_LIBRARIES);
        } else {
            if (!binFileExist()) {
                Log.fileError(c, new FileLoadError.BinaryFile(
                        "Binary cache file '" + BIN_FILE + "' does not exist. "
                ));
                throw new Log.KarinaException();
            }
            return BinaryFormatLinker.readBinary(c, BIN_FILE);
        }
    }



    private static boolean binFileExist() {
        try (var resourceStream = ModelLoader.class.getResourceAsStream("/" + BIN_FILE)) {
            return resourceStream != null;
        } catch (IOException e) {
            return false;
        }
    }

    private static Model modelFromResource(IntoContext c, List<ResourceLibrary> libraries) {
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


    private static Model loadFromResource(IntoContext c, ResourceLibrary library) {
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


    /// Rebuild cache
    public static void main(String[] args) {

        ColorOut.begin(LogColor.GRAY)
                .append("> Rebuilding cache")
                .out(System.out);

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();

        Log.begin("rebuild-cache");

        var startTime = System.currentTimeMillis();
        var result = KarinaCompiler.runInContext(
                errors,
                warnings,
                recordings,
                ModelLoader::rebuildModel
        );
        var deltaTime = System.currentTimeMillis() - startTime;

        System.out.println();
        FlightRecordCollection.printColored(recordings, true, System.out);
        System.out.println();

        if (result == null) {
            ColorOut.begin(LogColor.RED).append("Cache building failed").out(System.out);
            System.out.println();
            System.out.flush();

            DiagnosticCollection.print(errors, true, System.err);
        } else {
            ColorOut.begin(LogColor.GRAY)
                    .append("Finished in ")
                    .append(deltaTime)
                    .append("ms")
                    .out(System.out);

            LogColor.YELLOW.out(System.out);
            DiagnosticCollection.print(warnings, true, System.out);
            LogColor.NONE.out(System.out);
        }

        Log.end("rebuild-cache");

    }

    private static Object rebuildModel(Context c) {
        var folder = Path.of("src/main/resources");
        var destination = folder.resolve(BIN_FILE);

        if (!Files.exists(folder)) {
            Log.fileError(c, new FileLoadError.NotFound(
                    folder.toFile()
            ));
            throw new Log.KarinaException();
        }

        var loadedModel = modelFromResource(c, RESOURCE_LIBRARIES);
        BinaryFormatLinker.writeBinary(c, loadedModel, destination);


        return "OK";
    }
}
