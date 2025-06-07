package org.karina.lang.compiler;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.stages.generate.GenerationProcessor;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.stages.parser.ParseProcessor;

import java.nio.file.Path;


/**
 * Main Compiler class
 * Responsible for passing the source files through the different stages of the compiler
 * Parser -> Import -> Attribution -> Lowering -> Generation
 */

public class KarinaCompiler {
    public static final String VERSION = "v0.6";

    /// 16 bits for the version, 16 bits for the binary format
    public static final int BINARY_VERSION = 6 << 16 | 2;
    public static final int BINARY_MAGIC_NUMBER = 20000411;

    public static final boolean useThreading = false;
    public static final boolean allowMultipleErrors = false;

    /**
     * Cache for faster testing
     */
    public static Model cache = null;



    // Configuration
    @Setter private @Nullable String outputFile;
    @Setter private boolean emitClasses;

    // Logging
    @Setter private @Nullable DiagnosticCollection errorCollection;
    @Setter private @Nullable DiagnosticCollection warningCollection;
    @Setter private @Nullable FlightRecordCollection flightRecordCollection;

    @Getter private @Nullable JarCompilation jarCompilation;

    /**
     * Compiles the given files.
     * This method cannot throw exceptions.
     * @param files the file tree to compile
     * @return true if the compilation was successful, false otherwise
     */
    public boolean compile(FileTreeNode<TextSource> files) {
        // Context is always referred to as 'c' in the codebase
        var c = Log.emptyContext();


        // The 5 stages of the compiler:
        final ParseProcessor parser = new ParseProcessor();
        final ImportProcessor importProcessor = new ImportProcessor();
        final AttributionProcessor attributionProcessor = new AttributionProcessor();
        final LoweringProcessor lowering = new LoweringProcessor();
        final GenerationProcessor backend = new GenerationProcessor();

        Log.begin("compilation");
        try {

            Model bytecodeClasses;
            if (cache != null) {
                bytecodeClasses = cache;
            } else {
                var modelLoader = new ModelLoader();
                bytecodeClasses = cache = modelLoader.getJarModel(c);
            }

            ImportHelper.logFullModel(bytecodeClasses);
            KType.validateBuildIns(c, bytecodeClasses);


            Log.begin("parsing");
            var userModel = parser.parseTree(c, files);
            Log.end("parsing");

            Log.begin("merging");
            var languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
            Log.end("merging", "with " + languageModel.getClassCount() + " classes");

            Log.begin("importing");
            var importedTree = importProcessor.importTree(c, languageModel);
            Log.end("importing", "with " + importedTree.getClassCount() + " classes");

            Log.begin("attribution");
            var attributedTree = attributionProcessor.attribTree(c, importedTree);
            Log.end("attribution", "with " + attributedTree.getClassCount() + " classes");

            if (this.outputFile != null) {
                Log.begin("lowering");
                var loweredTree = lowering.lowerTree(c, attributedTree);
                Log.end("lowering", "with " + loweredTree.getClassCount() + " classes");


                Log.begin("generation");
                var compiled = backend.compileTree(c, loweredTree, "main");
                var path = Path.of(this.outputFile);
                Log.record("compiled to " + path.toAbsolutePath());
                Log.end("generation");

                this.jarCompilation = compiled;

                Log.begin("write");
                Log.begin("jar");
                compiled.writeJar(c, path);
                Log.end("jar");

                if (this.emitClasses) {
                    Log.begin("classes");
                    compiled.writeClasses(c, path.getParent().resolve("classes"));
                    Log.end("classes");
                }
                Log.end("write");



                var amountFiles = files.leafCount();
                var amountDefined = userModel.getUserClasses().size();
                var amountCompiled = loweredTree.getUserClasses().size();
                var amountMessage = "with " + amountFiles + " files, " + amountDefined + " defined classes and " + amountCompiled + " compiled classes";
                Log.record(amountMessage);
            }

            if (c.hasErrors()) {
                Log.internal(c,new IllegalStateException("Errors in log, this should not happen"));
                throw new Log.KarinaException();
            }

            return true;
        } catch (Exception error) {
            if (!(error instanceof Log.KarinaException)) {
                Log.internal(c, error);
            }
            if (!c.hasErrors()) {
                Log.internal(c, new IllegalStateException("An exception was thrown, but no errors were logged"));
            }

            return false;
        } finally {
            Log.end("compilation");

            if (this.warningCollection != null) {
                this.warningCollection.addAll(c.getWarnings());
            }
            if (this.errorCollection != null) {
                this.errorCollection.addAll(c.getErrors());
            }
            if (this.flightRecordCollection != null) {
                this.flightRecordCollection.add(Log.getRecordedLogs());
            }

            Log.clearAllLogs();
        }


    }





}
