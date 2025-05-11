package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.DiagnosticCollection;
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
    public static final String VERSION = "0.4";

    /**
     * Cache for faster testing
     */
    private static Model cache = null;

    // The 5 stages of the compiler:
    private final ParseProcessor parser;
    private final ImportProcessor importProcessor;
    private final AttributionProcessor attributionProcessor;
    private final LoweringProcessor lowering;
    private final GenerationProcessor backend;

    // The directory to emit the compiled files to
    private final @Nullable String emitDirectory;
    private final boolean emitClasses;

    public KarinaCompiler(@Nullable String emitDirectory, boolean emitClasses) {
        this.emitDirectory = emitDirectory;
        this.emitClasses = emitClasses;
        this.parser = new ParseProcessor();
        this.importProcessor = new ImportProcessor();
        this.attributionProcessor = new AttributionProcessor();
        this.lowering = new LoweringProcessor();
        this.backend = new GenerationProcessor();
    }

    /**
     * Compiles the given files.
     * This method cannot throw exceptions.
     * All Logs will be put into the given collection.
     * @param files the file tree to compile
     * @param collection the collection to add errors to
     * @param warnings the collection to add warnings to
     * @return true if the compilation was successful, false otherwise
     */
    public boolean compile(FileTreeNode<TextSource> files, DiagnosticCollection collection, DiagnosticCollection warnings, @Nullable FlightRecordCollection recorder) {
        Log.begin("compile");
        try {
            Log.begin("jar-load");


            Model bytecodeClasses;
            if (cache != null) {
                bytecodeClasses = cache;
            } else {
                var modelLoader = new ModelLoader();

                Log.begin("java-base");
                var javaBase = modelLoader.loadJavaBase();
                Log.end("java-base", "with " + javaBase.getClassCount() + " classes");

                Log.begin("karina-base");
                var karinaBase = modelLoader.loadKarinaBase();
                Log.end("karina-base", "with " + karinaBase.getClassCount() + " classes");

                Log.end("jar-load");
                bytecodeClasses = cache = ModelBuilder.merge(javaBase, karinaBase);
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

            if (this.emitDirectory != null) {
                Log.begin("lowering");
                var loweredTree = this.lowering.lowerTree(attributedTree);
                Log.end("lowering", "with " + loweredTree.getClassCount() + " classes");


                Log.begin("generation");
                var compiled = this.backend.compileTree(loweredTree, "main");
                var path = Path.of(this.emitDirectory);

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
            warnings.addAll(Log.getWarnings());
            if (recorder != null) {
                recorder.add(Log.getRecordedLogs());
            }
            collection.addAll(Log.getEntries());

            Log.end("compile");
            Log.clearLogs();
        }


    }





}
