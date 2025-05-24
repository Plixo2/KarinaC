package org.karina.lang.compiler;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.KType;
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
    public static final String VERSION = "v0.4";

    /// 16 bits for the version, 16 bits for the binary format
    public static final int BINARY_VERSION = 4 << 16 | 0b1;
    public static final int BINARY_MAGIC_NUMBER = 20000411;

    /**
     * Cache for faster testing
     */
    private static Model cache = null;

    // The 5 stages of the compiler:
    private final ParseProcessor parser = new ParseProcessor();
    private final ImportProcessor importProcessor = new ImportProcessor();
    private final AttributionProcessor attributionProcessor = new AttributionProcessor();
    private final LoweringProcessor lowering = new LoweringProcessor();
    private final GenerationProcessor backend = new GenerationProcessor();

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

        Log.begin("compilation");
        try {

            Model bytecodeClasses;
            if (cache != null) {
                bytecodeClasses = cache;
            } else {
                var modelLoader = new ModelLoader();
                bytecodeClasses = cache = modelLoader.getJarModel();
            }

            ImportHelper.logFullModel(bytecodeClasses);
            KType.validateBuildIns(bytecodeClasses);


            Log.begin("parsing");
            var userModel = this.parser.parseTree(files);
            Log.end("parsing");

            Log.begin("merging");
            var languageModel = ModelBuilder.merge(userModel, bytecodeClasses);
            Log.end("merging", "with " + languageModel.getClassCount() + " classes");

            Log.begin("importing");
            var importedTree = this.importProcessor.importTree(languageModel);
            Log.end("importing", "with " + importedTree.getClassCount() + " classes");

            Log.begin("attribution");
            var attributedTree = this.attributionProcessor.attribTree(importedTree);
            Log.end("attribution", "with " + attributedTree.getClassCount() + " classes");

            if (this.outputFile != null) {
                Log.begin("lowering");
                var loweredTree = this.lowering.lowerTree(attributedTree);
                Log.end("lowering", "with " + loweredTree.getClassCount() + " classes");


                Log.begin("generation");
                var compiled = this.backend.compileTree(loweredTree, "main");
                this.jarCompilation = compiled;
                var path = Path.of(this.outputFile);

                compiled.writeJar(path);

                if (this.emitClasses) {
                    compiled.writeClasses(path.getParent().resolve("classes"));
                }

                Log.record("compiled to " + path.toAbsolutePath());
                Log.end("generation");


                var amountFiles = files.leafCount();
                var amountDefined = userModel.getUserClasses().size();
                var amountCompiled = loweredTree.getUserClasses().size();
                var amountMessage = "with " + amountFiles + " files, " + amountDefined + " defined classes and " + amountCompiled + " compiled classes";
                Log.record(amountMessage);
            }

            if (Log.hasErrors()) {
                Log.internal(new IllegalStateException("Errors in log, this should not happen"));
                throw new Log.KarinaException();
            }

            return true;
        } catch (Exception error) {
            if (!(error instanceof Log.KarinaException)) {
                Log.internal(error);
            }
            if (!Log.hasErrors()) {
                Log.internal(new IllegalStateException("An exception was thrown, but no errors were logged"));
            }

            return false;
        } finally {
            Log.end("compilation");

            if (this.warningCollection != null) {
                this.warningCollection.addAll(Log.getWarnings());
            }
            if (this.errorCollection != null) {
                this.errorCollection.addAll(Log.getEntries());
            }
            if (this.flightRecordCollection != null) {
                this.flightRecordCollection.add(Log.getRecordedLogs());
            }

            Log.clearAllLogs();
        }


    }





}
