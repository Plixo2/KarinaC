package org.karina.lang.compiler.api;

import org.karina.lang.compiler.jvm.InterfaceBuilder;
import org.karina.lang.compiler.jvm.ModelLoader;
import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.parser.TextProcessor;
import org.karina.lang.compiler.stages.postprocess.PostProcessor;
import org.karina.lang.compiler.stages.preprocess.PreProcessor;

import java.nio.file.Path;

public class KarinaDefaultCompiler {
    private final ModelLoader modelLoader;
    private final TextProcessor parser;
    private final PreProcessor preprocessor;
    private final ImportProcessor importProcessor;
    private final AttributionProcessor attributionProcessor;
    private final PostProcessor postProcessor;
    private final BytecodeProcessor backend;

    private final Path outPath;
    private final String mainClass;
    public KarinaDefaultCompiler(String mainClass, Path outPath) {
        this.mainClass = mainClass;
        this.outPath = outPath;
        this.modelLoader = new ModelLoader();
        this.parser = new TextProcessor();
        this.preprocessor = new PreProcessor();
        this.importProcessor = new ImportProcessor();
        this.attributionProcessor = new AttributionProcessor();
        this.postProcessor = new PostProcessor();
        this.backend = new BytecodeProcessor();
    }

    public boolean compile(FileTreeNode<TextSource> files, DiagnosticCollection collection, DiagnosticCollection warnings) {

        try {
            var byteCodeModel = this.modelLoader.loadJDK();
            var userModel = this.parser.parseFiles(files);
            var languageModel = byteCodeModel.merge(userModel);
           // var desugaredTree = this.preprocessor.desugarTree(languageModel);
            var importedTree = this.importProcessor.importTree(languageModel);
         //   var attribTree = this.attributionProcessor.attribTree(importedTree);
//               var postTree = this.postProcessor.rewrite(attribTree);

          //  var result = this.backend.accept(attribTree, this.mainClass);
        //    result.write(this.outPath);
         //   result.dump(this.outPath);

            if (Log.hasErrors()) {
                System.err.println("Errors in log, this should not happen");
            }
            warnings.addAll(Log.getWarnings());
            return true;
        } catch (Log.KarinaException ignored) {
            warnings.addAll(Log.getWarnings());
            if (!Log.hasErrors()) {
                System.err.println("An exception was thrown, but no errors were logged");
            } else {
                collection.addAll(Log.getEntries());
            }
            return false;
        } finally {
            Log.clearLogs();
        }

    }


}
