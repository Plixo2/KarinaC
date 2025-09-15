package org.karina.lang.compiler.jvm_loading.loading;

import org.karina.lang.compiler.jvm_loading.binary.BinaryFormatLinker;
import org.karina.lang.compiler.utils.logging.*;
import org.karina.lang.compiler.utils.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarInputStream;

public class ModelLoader {
    private static final List<ResourceLibrary> RESOURCE_LIBRARIES = List.of(
            new ResourceLibrary("java-base",        "java.base.jar"        ),
            new ResourceLibrary("java-desktop",     "java.desktop.jar"     ),
            new ResourceLibrary("java-instrument",  "java.instrument.jar"  ),
            new ResourceLibrary("java-logging",     "java.logging.jar"     ),
            new ResourceLibrary("java-management",  "java.management.jar"  ),
            new ResourceLibrary("java-naming",      "java.naming.jar"      ),
            new ResourceLibrary("java-net-http",    "java.net.http.jar"    ),
            new ResourceLibrary("java-scripting",   "java.scripting.jar"   ),
            new ResourceLibrary("java-sql",         "java.sql.jar"         ),
            new ResourceLibrary("java-xml",         "java.xml.jar"         ),

            new ResourceLibrary("karina-base",      "karina.base.jar"      )
    );


    private static final String BIN_FILE = "base.bin.gz";

    // Whether to fallback to resources if the binary cache is not valid
    private static final boolean FALLBACK_TO_RESOURCES = false;


    public static Model getJarModel(IntoContext c, boolean useBinaryFormat) {

        if (useBinaryFormat) {
            return getBinaryModel(c);
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
            if (c.log(Logging.ReadJar.class)) {
                c.tag("problem reading cache",
                        "Binary cache file '",
                        BIN_FILE,
                        "'",
                        "does not exist (generate it with the buildCache gradle task), ",
                        "falling back to resources."
                );
            }
            return modelFromResource(c, RESOURCE_LIBRARIES);
        } else {
            if (!binFileExist()) {
                Log.fileError(
                        c, new FileLoadError.BinaryFile(
                                "Binary cache file '" + BIN_FILE + "' does not exist. " +
                                        "Generate it with the buildCache gradle task")
                );
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
        try (var _ = c.section(Logging.ReadJar.class,"loading jar files")){

            var models = new Model[libraries.size()];
            for (var i = 0; i < libraries.size(); i++) {
                var library = libraries.get(i);
                try (var _ = c.section(Logging.ReadJar.class, library.name)){
                    var lib = loadFromResource(c, library);
                    models[i] = lib;
                    if (c.log(Logging.ReadJar.class)) {
                        c.tag("number of classes", lib.getClassCount());
                    }
                }

            }
            return ModelBuilder.merge(c, models);
        }
    }


    private static Model loadFromResource(IntoContext c, ResourceLibrary library) {
        var jdkSet = new OpenSet();

        try (var _ = c.section(Logging.ReadJar.class,"reading")) {
            var resource = "/" + library.resource;
            try (var resourceStream = ModelLoader.class.getResourceAsStream(resource)) {

                if (resourceStream == null) {
                    Log.fileError(
                            c, new FileLoadError.Resource(new FileNotFoundException(
                                    "Could not find resource: '" + resource + "'"))
                    );
                    throw new Log.KarinaException();
                }

                try (var jarInputStream = new JarInputStream(resourceStream)) {
                    BytecodeLoading.loadJarFile(jarInputStream, jdkSet);
                }

            } catch (IOException e) {
                Log.fileError(c, new FileLoadError.Resource(e));
                throw new Log.KarinaException();
            }
        }

        try (var _ = c.section(Logging.ReadJar.class,"linking")) {
            var builder = new ModelBuilder();
            var linker = new InterfaceLinker();

            for (var topClass : jdkSet.removeTopClasses()) {
                var _ = linker.createClass(
                        c, null, topClass, jdkSet, new HashSet<>(), builder,
                        null
                );
            }

            for (var value : jdkSet.getOpenSet().values()) {
                if (value.node().outerClass == null) {
                    continue;
                }
                Log.bytecode(c, value.getSource(), value.node().name, "Could not be linked");
                throw new Log.KarinaException();
            }
            return builder.build(c);
        }


    }


    private record ResourceLibrary(String name, String resource) { }


    /// Rebuild cache
    public static void main(String[] args) {

        Colored.begin(ConsoleColor.GRAY)
               .append("> Rebuilding cache")
               .println(System.out);

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var recordings = new FlightRecordCollection();


        var startTime = System.currentTimeMillis();
        var config = Context.ContextHandling.of(true, false, false);
        var result = Context.run(
                config,
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
            Colored.begin(ConsoleColor.RED).append("Cache building failed").println(System.out);
            System.out.println();
            System.out.flush();

            DiagnosticCollection.print(errors, true, System.err);
        } else {
            Colored.begin(ConsoleColor.GRAY)
                   .append("Finished in ")
                   .append(deltaTime)
                   .append("ms")
                   .println(System.out);

            ConsoleColor.YELLOW.out(System.out);
            DiagnosticCollection.print(warnings, true, System.out);
            ConsoleColor.RESET.out(System.out);
        }


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
        if (c.log(Logging.ReadJar.class)) {
            c.tag("Final number of classes", loadedModel.getClassCount());
        }

        BinaryFormatLinker.writeBinary(c, loadedModel, destination);


        return "OK";
    }
}
