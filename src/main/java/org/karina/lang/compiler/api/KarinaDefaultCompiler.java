package org.karina.lang.compiler.api;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.ModelLoader;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.parser.TextToClassVisitor;

import java.lang.ref.Cleaner;
import java.nio.file.Path;

public class KarinaDefaultCompiler {
    private final ModelLoader modelLoader;
    private final TextToClassVisitor parser;
    private final ImportProcessor importProcessor;
    private final AttributionProcessor attributionProcessor;
    private final BytecodeProcessor backend;

    private final Path outPath;
    private final String mainClass;

    public KarinaDefaultCompiler(String mainClass, Path outPath) {
        this.mainClass = mainClass;
        this.outPath = outPath;
        this.modelLoader = new ModelLoader();
        this.parser = new TextToClassVisitor();
        this.importProcessor = new ImportProcessor();
        this.attributionProcessor = new AttributionProcessor();
        this.backend = new BytecodeProcessor();
    }

    public boolean compile(FileTreeNode<TextSource> files, DiagnosticCollection collection, DiagnosticCollection warnings, @Nullable FlightRecordCollection recorder) {
        Log.begin("compile");
        try {
            Log.begin("jar-load");

            Log.begin("java-base");
            var javaBase = this.modelLoader.loadJavaBase();
            Log.end("java-base", "with " + javaBase.getClassCount() + " classes");

            Log.begin("karina-base");
            var karinaBase = this.modelLoader.loadKarinaBase();
            Log.end("karina-base", "with " + karinaBase.getClassCount() + " classes");

            Log.end("jar-load");


            var bytecodeClasses = JKModel.merge(javaBase, karinaBase);
            KType.validateBuildIns(bytecodeClasses);


            Log.begin("parsing");
            var userModel = this.parser.textIntoClasses(files);
            Log.end("parsing");

            Log.begin("merging");
            var languageModel = JKModel.merge(userModel, bytecodeClasses);
            Log.end("merging", "with " + languageModel.getClassCount() + " classes");

            Log.begin("importing");
            var importedTree = this.importProcessor.importTree(languageModel);
            Log.end("importing", "with " + importedTree.getClassCount() + " classes");

            Log.begin("attribution");
            var attributedTree = this.attributionProcessor.attribTree(importedTree);
            Log.end("attribution", "with " + attributedTree.getClassCount() + " classes");



            warnings.addAll(Log.getWarnings());
            Log.end("compile");
            if (recorder != null) {
                recorder.add(Log.getRecordedLogs());
            }
            if (Log.hasErrors()) {
                Log.internal(new IllegalStateException("Errors in log, this should not happen"));
            }
            return true;
        } catch (Exception error) {
            if (!(error instanceof Log.KarinaException)) {
                Log.internal(error);
            }

            Log.end("compile");
            if (recorder != null) {
                recorder.add(Log.getRecordedLogs());
            }

            warnings.addAll(Log.getWarnings());
            if (!Log.hasErrors()) {
                Log.internal(new IllegalStateException("An exception was thrown, but no errors were logged"));
            }
            collection.addAll(Log.getEntries());


            return false;
        } finally {
            Log.clearLogs();
        }


    }

}
