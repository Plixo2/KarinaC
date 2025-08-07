package org.karina.lang.compiler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.stages.writing.WritingProcessor;
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
import java.util.Objects;
import java.util.function.Function;


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

    public static final boolean useThreading = false;
    public static final boolean allowMultipleErrors = true;

    private KarinaCompiler() {}

    /**
     * Cache for faster testing
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



    ///
    /// Compiles the given files.
    /// @param files the file tree to compile
    /// @return the compiled jar
    ///
    private JarCompilation run(Context c, FileTreeNode<TextSource> files) {

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


        Log.begin("parsing");
        var userModel = parser.parseTree(c, files);
        Log.end("parsing", "with " + userModel.getClassCount() + " classes");

        Log.begin("merging");
        var languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
        Log.end("merging", "with " + languageModel.getClassCount() + " classes");

        Log.begin("importing");
        var importedTree = importProcessor.importTree(c, languageModel);
        Log.end("importing", "with " + importedTree.getClassCount() + " classes");

        Log.begin("attribution");
        var attributedTree = attributionProcessor.attribTree(c, importedTree);
        Log.end("attribution", "with " + attributedTree.getClassCount() + " classes");

        Log.begin("lowering");
        var loweredTree = lowering.lowerTree(c, attributedTree);
        Log.end("lowering", "with " + loweredTree.getClassCount() + " classes");

        Log.begin("generation");
        var compiled = backend.compileTree(c, loweredTree, "main");
        Log.end("generation");

        Log.begin("write");
        writing.writeCompilation(c, compiled, this.outputConfig);
        Log.end("write");

        var amountFiles = files.leafCount();
        var amountDefined = userModel.getUserClasses().size();
        var amountCompiled = loweredTree.getUserClasses().size();
        var amountMessage = "with " + amountFiles + " files, " + amountDefined + " defined classes and " + amountCompiled + " compiled classes";
        Log.record(amountMessage);

        return compiled;

    }

    /// call [#run] to compile the given files in the context of the compiler.
    /// @see #run
    /// @see #runInContext
    /// @return the compiled jar, or null if the compilation failed
    public @Nullable JarCompilation compile(FileTreeNode<TextSource> files) {

        Log.begin("compilation");
        var result = runInContext(
                this.errorCollection,
                this.warningCollection,
                this.flightRecordCollection,
                c -> run(c, files)
        );
        Log.end("compilation");

        return result;
    }


    /// Wrapper for running a function in the context of the compiler.
    /// This method cannot throw exceptions, but will log them and return null when an exception occurs.
    @Contract(mutates = "param1, param2, param3")
    public static <T> @Nullable T runInContext(
            @Nullable DiagnosticCollection errors,
            @Nullable DiagnosticCollection warnings,
            @Nullable FlightRecordCollection recordings,
            Function<Context, T> function
    ) {
        // Context is always referred to as 'c' in the codebase
        var c = Context.empty();

        try {
            var object = function.apply(c);

            if (c.hasErrors()) {
                Log.internal(c,new IllegalStateException("Errors in log, this should not happen"));
                throw new Log.KarinaException(); // trigger error handling
            }

            return object;
        } catch (Exception error) {
            if (!(error instanceof Log.KarinaException)) {
                Log.internal(c, error);
            }
            if (!c.hasErrors()) {
                error.printStackTrace();
                Log.internal(c, new IllegalStateException("An exception was thrown, but no errors were logged"));
            }

            return null;
        } finally {

            if (warnings != null) {
                warnings.addAll(c.getWarnings());
            }
            if (errors != null) {
                errors.addAll(c.getErrors());
            }
            if (recordings != null) {
                recordings.add(Log.getRecordedLogs());
            }

            Log.clearAllLogs();
        }

    }


    public static KarinaCompilerBuilder builder() {
        return new KarinaCompilerBuilder();
    }


    public static class KarinaCompilerBuilder {
        // Configuration
        private @Nullable Config.OutputConfig outputConfig;
        private boolean useBinaryFormat = false;

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
            this.outputConfig = new SimpleOutConfig(outputFile, false);
            return this;
        }


        public KarinaCompilerBuilder useBinaryFormat(boolean useBinaryFormat) {
            this.useBinaryFormat = useBinaryFormat;
            return this;
        }


        public KarinaCompilerBuilder enableBinaryFormat() {
            this.useBinaryFormat = true;
            return this;
        }


        public KarinaCompilerBuilder disableBinaryFormat() {
            this.useBinaryFormat = false;
            return this;
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
            var compiler = new KarinaCompiler();
            compiler.outputConfig = this.outputConfig;
            compiler.useBinaryFormat = this.useBinaryFormat;
            compiler.errorCollection = this.errorCollection;
            compiler.warningCollection = this.warningCollection;
            compiler.flightRecordCollection = this.flightRecordCollection;
            return compiler;
        }


    }


}
