package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageType;
import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.generate.GenerationProcessor;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.stages.parser.ParseProcessor;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.lsp.impl.CodeDiagnosticInformationBuilder;
import org.karina.lang.lsp.impl.ClientConfiguration;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class OneShotCompiler {
    private final EventService service;

    public static final Option<Model> cache = Option.none();

    @Getter
    @Accessors(fluent = true)
    private CompiledModelIndex lastestCompiledModel = new CompiledModelIndex();

    private Option<Job> currentProcess = Option.none();

    public void run(VirtualFileTreeNode treeNode, List<VirtualFile> files, URI mainFile) {
        if (!(CompiledHelper.getObjectPathOfURI(treeNode, mainFile) instanceof Option.Some(var mainPath))) {
            this.service.send(new ClientEvent.Log("Main file not found: " + mainFile, MessageType.Warning));
            return;
        }
        run(treeNode, files, mainPath);
    }

    public void run(VirtualFileTreeNode treeNode, List<VirtualFile> files, ObjectPath mainPath) {
        this.service.clearTerminal();
        this.service.send(new ClientEvent.Log("Running " + mainPath.mkString("::"), MessageType.Log));
        var runSettings = Option.some(new RunSettings(mainPath));
        startCompileTask(treeNode, files, ClientConfiguration.LoggingLevel.NONE, runSettings);
    }

    public void build(FileTreeNode treeNode, List<VirtualFile> files, ClientConfiguration.LoggingLevel loggingLevel) {
        startCompileTask(treeNode, files, loggingLevel, Option.none());
    }

    private void startCompileTask(
            FileTreeNode treeNode,
            List<VirtualFile> files,
            ClientConfiguration.LoggingLevel loggingLevel,
            Option<RunSettings> runSettings
    ) {
        CompiledHelper.clearErrors(files, this.service);
        var treeCopy = FileTreeNode.copyTree(treeNode);

        if (this.currentProcess instanceof Option.Some(var prevProcess)) {
            prevProcess.cancel();
        }

        this.currentProcess = Option.some(this.service.createJob(
                "Compilation", progress -> {
                    var start = System.currentTimeMillis();
                    compileNonBlocking(treeCopy, progress, loggingLevel, runSettings);
                    var end = System.currentTimeMillis();
                    this.service.send(
                            new ClientEvent.Log(
                                    "Compilation took " + (end - start) + "ms", MessageType.Log)
                    );
                    return "done";
                }
        ));

    }

    private void compileNonBlocking(
            FileTreeNode treeCopy,
            JobProgress workProgress,
            ClientConfiguration.LoggingLevel loggingLevel,
            Option<RunSettings> runSettings
    ) throws IOException {

        workProgress.notify("compiling files", 10);

        var errors = new DiagnosticCollection();
        var flight = new FlightRecordCollection();

        var config = Context.ContextHandling.of(
                false,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var toFill = new CompiledMutableModelIndex();
        var latestModel = Context.run(
                config,
                errors,
                null,
                flight,
                (c) -> runCompilationSteps(c, treeCopy, runSettings, toFill)
        );
        synchronized (this) {
            this.lastestCompiledModel = new CompiledModelIndex(
                    toFill.userModel,
                    toFill.languageModel,
                    toFill.importedModel,
                    toFill.attributedModel,
                    toFill.loweredModel
            );
        }

//        Log.updateLogLevel(loggingLevel.internalLogName());
        workProgress.notify("publishing errors", 80);

        if (latestModel == null) {
            if (errors.getTraces().isEmpty()) {
                this.service.send(new ClientEvent.Log(
                        "Compilation failed, but no errors were reported.",
                        MessageType.Warning
                ));
            }
            CompiledHelper.pushErrors(errors, this.service);
        }

        workProgress.notify("sending flight record", 90);

        if (loggingLevel != ClientConfiguration.LoggingLevel.NONE) {
            try (var baos = new ByteArrayOutputStream(); var filePrintStream = new PrintStream(baos)) {
                FlightRecordCollection.print(flight, false, filePrintStream);
                this.service.send(new ClientEvent.Log(
                        baos.toString(),
                        MessageType.Log
                ));
            }
        }


    }



    ///
    /// Compiles the given files.
    /// @param files the file tree to compile
    /// @return the compiled jar
    ///
    @Contract(mutates = "param4")
    private Model runCompilationSteps(Context c, FileTreeNode files, Option<RunSettings> runSettings, CompiledMutableModelIndex toFill) {


        ParseProcessor parser = new ParseProcessor();
        ImportProcessor importProcessor = new ImportProcessor();
        AttributionProcessor attributionProcessor = new AttributionProcessor();
        LoweringProcessor lowering = new LoweringProcessor();

        var bytecodeClasses = cache.orElseGet(() ->  ModelLoader.getJarModel(c, true));

        ImportHelper.logFullModel(bytecodeClasses);
        KType.validateBuildIns(c, bytecodeClasses);

        var userModel = parser.parseTree(c, files);
        toFill.userModel = Option.some(userModel);
        var languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
        toFill.languageModel = Option.some(languageModel);
        var importedTree = importProcessor.importTree(c, languageModel);
        toFill.importedModel = Option.some(importedTree);
        var attributedTree = attributionProcessor.attribTree(c, importedTree);
        toFill.attributedModel = Option.some(attributedTree);
        var loweredTree = lowering.lowerTree(c, attributedTree);
        toFill.loweredModel = Option.some(loweredTree);

        if (runSettings instanceof Option.Some(var settings)) {
            GenerationProcessor backend = new GenerationProcessor();

            var compiled = backend.compileTree(c, loweredTree, settings.main.mkString("."));

            var jobName = "Executing '" + settings.main.mkString("::") + "'";
            this.service.createJob(jobName, progress -> {
                        progress.notify(10);

                        return switch (AutoRun.runWithPrints(compiled, false, new String[]{})) {
                            case null -> "done";
                            case AutoRun.MainInvocationResult.MainError(var e) -> {
                                progress.notify(100);
                                e.printStackTrace(System.out);
                                yield "done with error: " + e.getMessage();
                            }
                            case AutoRun.MainInvocationResult.OtherError(var other) -> {
                                progress.notify(100);
                                var message = "Error while running the compiled code: " + other;
                                this.service.send(new ClientEvent.Log(message, MessageType.Error));
                                this.service.send(new ClientEvent.Popup(message, MessageType.Error));
                                yield message;
                            }
                        };
            });
        }

        return loweredTree;
    }



    private record RunSettings(
            ObjectPath main
    ) {}

    private static class CompiledMutableModelIndex {
        private Option<Model> userModel = Option.none();
        private Option<Model> languageModel = Option.none();
        private Option<Model> importedModel = Option.none();
        private Option<Model> attributedModel = Option.none();
        private Option<Model> loweredModel = Option.none();
    }

    public static class CompiledModelIndex {
        private CompiledModelIndex(
                Option<Model> userModel,
                Option<Model> languageModel,
                Option<Model> importedModel,
                Option<Model> attributedModel,
                Option<Model> loweredModel
        ) {
            this.userModel = userModel;
            this.languageModel = languageModel;
            this.importedModel = importedModel;
            this.attributedModel = attributedModel;
            this.loweredModel = loweredModel;
        }

        private CompiledModelIndex() {
            this(Option.none(), Option.none(), Option.none(), Option.none(), Option.none());
        }

        public final Option<Model> userModel;
        public final Option<Model> languageModel; // when existing, userModel also exists
        public final Option<Model> importedModel; // when existing, userModel and languageModel also exists
        public final Option<Model> attributedModel; // when existing, userModel, languageModel and importedModel also exist
        public final Option<Model> loweredModel; // when existing, userModel, languageModel and importedModel also exist
    }
}
