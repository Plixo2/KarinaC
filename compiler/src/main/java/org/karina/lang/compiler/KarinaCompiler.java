package org.karina.lang.compiler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.generate.GenerationProcessor;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.stages.parser.ParseProcessor;
import org.karina.lang.compiler.stages.writing.WritingProcessor;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.KType;

import java.nio.file.Path;
import java.util.Objects;


///
/// Main Compiler class
/// Responsible for passing the source files through the different stages of the compiler \
/// `Parser` -> `Import` -> `Attribution` -> `Lowering` -> `Generation` -> `Writing`
///
/// Use [KarinaCompiler#builder()] to create a new instance of the compiler.
///

public class KarinaCompiler {
    public static final String VERSION = "v0.6";

    /// 16 bits for the version, 16 bits for iteration
    public static final int BINARY_VERSION = 6 << 16 | 4;
    public static final int BINARY_MAGIC_NUMBER = 20000411;

    /**
     * Cache for faster testing & faster lsp
     */
    @VisibleForTesting
    public static Model cache = null;

    // Configuration
    private @Nullable Config.OutputConfig outputConfig;
    private boolean useBinaryFormat = false;


    // Logging
    private @Nullable DiagnosticCollection errorCollection;
    private @Nullable DiagnosticCollection warningCollection;
    private @Nullable FlightRecordCollection flightRecordCollection;

    private final Context.ContextHandling contextHandling;

    private KarinaCompiler(Context.ContextHandling contextHandling) {
        this.contextHandling = contextHandling;
    }



    ///
    /// Compiles the given files.
    /// @param files the file tree to compile
    /// @return the compiled jar
    ///
    private JarCompilation run(Context c, FileTreeNode files) {


        // The 6 stages of the compiler:
        ParseProcessor parser = new ParseProcessor();
        ImportProcessor importProcessor = new ImportProcessor();
        AttributionProcessor attributionProcessor = new AttributionProcessor();
        LoweringProcessor lowering = new LoweringProcessor();
        GenerationProcessor backend = new GenerationProcessor();
        WritingProcessor writing = new WritingProcessor();

        var bytecodeClasses = cache = Objects.requireNonNullElseGet(
                cache, () -> ModelLoader.getJarModel(c, this.useBinaryFormat)
        );

        ImportHelper.logFullModel(bytecodeClasses);
        KType.validateBuildIns(c, bytecodeClasses);


        Model userModel;
        try (var _ = c.section(Logging.Parsing.class,"parsing")) {
            userModel = parser.parseTree(c, files);
            c.tag("number of files", files.leafCount());
            c.tag("number of classes", userModel.getUserClasses().size());
        }

        Model languageModel;
        try (var _ = c.section(Logging.Merging.class,"merging")) {
            languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
        }

        Model importedTree;
        try (var _ = c.section(Logging.Importing.class,"importing")) {
            importedTree = importProcessor.importTree(c, languageModel);
        }

        Model attributedTree;
        try (var _ = c.section(Logging.Attribution.class,"attributing")) {
            attributedTree = attributionProcessor.attribTree(c, importedTree);
        }

        Model loweredTree;
        try (var _ = c.section(Logging.Lowering.class,"lowering")) {
            loweredTree = lowering.lowerTree(c, attributedTree);
            var added = loweredTree.getUserClasses().size() - attributedTree.getUserClasses().size();
            c.tag("number of generated classes", added);
            c.tag("total number of classes", loweredTree.getUserClasses().size());
        }

        JarCompilation compiled;
        try (var _ = c.section(Logging.Generation.class,"generation")) {
            compiled = backend.compileTree(c, loweredTree, "main");
        }

        try (var _ = c.section(Logging.Writing.class,"writing")) {
            writing.writeCompilation(c, compiled, this.outputConfig);
        }

        return compiled;

    }

    /// call [#run] to compile the given files in the context of the compiler.
    /// @see #run
    /// @return the compiled jar, or null if the compilation failed
    @Contract(mutates = "this")
    public @Nullable JarCompilation compile(FileTreeNode files) {


        return Context.run(
                this.contextHandling,
                this.errorCollection,
                this.warningCollection,
                this.flightRecordCollection,
                c -> run(c, files)
        );
    }




    public static KarinaCompilerBuilder builder() {
        return new KarinaCompilerBuilder();
    }


    public final static class KarinaCompilerBuilder {
        // Configuration
        private @Nullable Config.OutputConfig outputConfig;
        private boolean useBinaryFormat = false;
        private boolean allowMultipleErrors = true;
        private boolean threading = true;

        // Logging
        private @Nullable DiagnosticCollection errorCollection;
        private @Nullable DiagnosticCollection warningCollection;
        private @Nullable FlightRecordCollection flightRecordCollection;


        private KarinaCompilerBuilder() {}

        public KarinaCompilerBuilder outputConfig(@Nullable Config.OutputConfig outputConfig) {
            this.outputConfig = outputConfig;
            return this;
        }


        public KarinaCompilerBuilder setOutputFile(Path outputFile) {
            record SimpleOutConfig(
                Path outputFile,
                boolean emitClassFiles
            ) implements Config.OutputConfig {}
            return outputConfig(new SimpleOutConfig(outputFile, false));
        }



        public KarinaCompilerBuilder binaryFormat(boolean useBinaryFormat) {
            this.useBinaryFormat = useBinaryFormat;
            return this;
        }


        public KarinaCompilerBuilder enableBinaryFormat() {
            return binaryFormat(true);
        }


        public KarinaCompilerBuilder disableBinaryFormat() {
            return binaryFormat(false);
        }

        public KarinaCompilerBuilder allowMultipleErrors(boolean allowMultipleErrors) {
            this.allowMultipleErrors = allowMultipleErrors;
            return this;
        }

        public KarinaCompilerBuilder allowMultipleErrors() {
            return allowMultipleErrors(true);
        }

        public KarinaCompilerBuilder disallowMultipleErrors() {
            return allowMultipleErrors(false);
        }

        public KarinaCompilerBuilder threading(boolean useThreading) {
            this.threading = useThreading;
            return this;
        }

        public KarinaCompilerBuilder enableThreading() {
            return threading(true);
        }
        public KarinaCompilerBuilder disableThreading() {
            return threading(false);
        }

        public KarinaCompilerBuilder errorCollection(@Nullable DiagnosticCollection errorCollection) {
            this.errorCollection = errorCollection;
            return this;
        }


        public KarinaCompilerBuilder warningCollection(@Nullable DiagnosticCollection warningCollection) {
            this.warningCollection = warningCollection;
            return this;
        }


        public KarinaCompilerBuilder flightRecordCollection(@Nullable FlightRecordCollection flightRecordCollection) {
            this.flightRecordCollection = flightRecordCollection;
            return this;
        }


        public KarinaCompiler build() {
            var contextHandling = Context.ContextHandling.of(true, this.threading, this.allowMultipleErrors);
            var compiler = new KarinaCompiler(contextHandling);
            compiler.outputConfig = this.outputConfig;
            compiler.useBinaryFormat = this.useBinaryFormat;
            compiler.errorCollection = this.errorCollection;
            compiler.warningCollection = this.warningCollection;
            compiler.flightRecordCollection = this.flightRecordCollection;
            return compiler;
        }


    }


}
