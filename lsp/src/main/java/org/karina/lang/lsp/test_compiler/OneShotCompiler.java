package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.lsp4j.MessageType;
import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.Config;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.stages.writing.WritingProcessor;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.generate.GenerationProcessor;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.stages.parser.ParseProcessor;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.lsp.impl.ClientConfiguration;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.process.Job;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class OneShotCompiler {
    private final EventService service;

    public static Option<Model> cache = Option.none();

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
        var killed = new AtomicBoolean(false);
        this.currentProcess = Option.some(this.service.createJob(
                "Compilation", progress -> {
                    var start = System.currentTimeMillis();
                    compileNonBlocking(treeCopy, progress, loggingLevel, runSettings, killed);
                    var end = System.currentTimeMillis();
                    this.service.send(
                            new ClientEvent.Log(
                                    "Compilation took " + (end - start) + "ms", MessageType.Log)
                    );
                    return "done";
                }, () -> {
                    killed.set(true);
                    return true;
                }
        ));

    }

    private void compileNonBlocking(
            FileTreeNode treeCopy,
            JobProgress workProgress,
            ClientConfiguration.LoggingLevel loggingLevel,
            Option<RunSettings> runSettings,
            AtomicBoolean killed
    ) throws IOException {

        workProgress.notify("compiling files", 10);

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var flight = new FlightRecordCollection();

        var config = Context.ContextHandling.of(
                true,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var toFill = new CompiledMutableModelIndex();
        var latestModel = Context.run(
                config,
                errors,
                warnings,
                flight,
                (c) -> runCompilationSteps(c, treeCopy, runSettings, toFill)
        );
        if (killed.get()) {
            this.service.send(new ClientEvent.Log("Compilation cancelled.", MessageType.Warning));
            return;
        }
        synchronized (this) {
            this.lastestCompiledModel = new CompiledModelIndex(
                    toFill.userModel,
                    toFill.languageModel,
                    toFill.importedModel,
                    toFill.attributedModel,
                    toFill.loweredModel
            );
        }
        workProgress.notify("publishing errors", 80);

        if (latestModel == null) {
            if (errors.getTraces().isEmpty()) {
                this.service.send(new ClientEvent.Log(
                        "Compilation failed, but no errors were reported.",
                        MessageType.Warning
                ));
            }
        }
        if (killed.get()) {
            this.service.send(new ClientEvent.Log("Compilation cancelled.", MessageType.Warning));
            return;
        }
        CompiledHelper.pushErrors(errors, warnings, this.service);

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
        cache = Option.some(bytecodeClasses);

        ImportHelper.logFullModel(bytecodeClasses);
        KType.validateBuildIns(c, bytecodeClasses);

        Model userModel;
        try (var _ = c.section(Logging.Parsing.class,"parsing")) {
            userModel = parser.parseTree(c, files);
            if (c.log(Logging.Parsing.class)) {
                c.tag("number of files", files.leafCount());
                c.tag("number of classes", userModel.getUserClasses().size());
            }
        }
        toFill.userModel = Option.some(userModel);

        Model languageModel;
        try (var _ = c.section(Logging.Merging.class,"merging")) {
            languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
        }
        toFill.languageModel = Option.some(languageModel);

        Model importedTree;
        try (var _ = c.section(Logging.Importing.class,"importing")) {
            importedTree = importProcessor.importTree(c, languageModel);
        }
        toFill.importedModel = Option.some(importedTree);

        Model attributedTree;
        try (var _ = c.section(Logging.Attribution.class,"attributing")) {
            attributedTree = attributionProcessor.attribTree(c, importedTree);
        }
        toFill.attributedModel = Option.some(attributedTree);

        Model loweredTree;
        try (var _ = c.section(Logging.Lowering.class,"lowering")) {
            loweredTree = lowering.lowerTree(c, attributedTree);
            var added = loweredTree.getUserClasses().size() - attributedTree.getUserClasses().size();
            if (c.log(Logging.Lowering.class)) {
                c.tag("number of generated classes", added);
                c.tag("total number of classes", loweredTree.getUserClasses().size());
                c.tag("number of classes in scope", loweredTree.getBinaryClasses().size() + loweredTree.getUserClasses().size());
            }
        }
        toFill.loweredModel = Option.some(loweredTree);

        if (runSettings instanceof Option.Some(var settings)) {
            GenerationProcessor backend = new GenerationProcessor();
            WritingProcessor writing = new WritingProcessor();

            JarCompilation compiled;
            try (var _ = c.section(Logging.Generation.class,"generation")) {
                compiled = backend.compileTree(c, loweredTree, settings.main.mkString("."));
            }

            try (var _ = c.section(Logging.Writing.class,"writing")) {
                var out = Path.of("build/");
                var config = new Config.OutputConfig() {

                    @Override
                    public Path outputFile() {
                        return out.resolve("out/build.jar");
                    }

                    @Override
                    public boolean emitClassFiles() {
                        return true;
                    }
                };

                writing.writeCompilation(c, compiled, config);
                try {
                    putKarinaLib(out);
                    putScript(out, WINDOWS_COMMAND, "run.bat");
                    putScript(out, LINUX_COMMAND, "run");
                } catch (IOException e) {
                    Log.internal(c, e);
                    throw new Log.KarinaException();
                }
            }
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




    public static final String WINDOWS_COMMAND =
            """
            @echo off
            pushd "%~dp0"
            java -cp "out/build.jar;libs/karina.base.jar" main
            popd
            """;

    public static final String LINUX_COMMAND =
            """
            #!/bin/bash
            
            pushd "$(dirname "$0")" > /dev/null
            java -cp "out/build.jar:libs/karina.base.jar" main
            popd > /dev/null
            """;

    public static void putScript(Path buildDir, String content, String file) throws IOException {
        var script = buildDir.resolve(file);

        Files.write(script, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public static void putKarinaLib(Path buildDir) throws IOException {
        var jarDest = buildDir.resolve("libs/karina.base.jar");

        Path destinationDir = jarDest.getParent();
        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        try (var resourceStream = OneShotCompiler.class.getResourceAsStream("/karina.base.jar")) {
            if (resourceStream == null) {
                System.out.println("Could not find karina.base.jar");
                return;
            }

            try (var outputStream = new FileOutputStream(jarDest.toFile())){
                outputStream.write(resourceStream.readAllBytes());
            }
        }
    }

}
