package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import karina.lang.Result;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageType;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.logging.Log;
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
import org.karina.lang.lsp.base.Process;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.impl.CodeDiagnosticInformation;
import org.karina.lang.lsp.lib.ClientConfiguration;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
public class OneShotCompiler {
    private final EventService service;

    public static Option<Model> cache = Option.none();

    private Option<Model> lastestCompiledModel = Option.none();

    private Option<Process> currentProcess = Option.none();

    public void run(VirtualFileTreeNode treeNode, List<VirtualFile> files, URI mainFile) {
        if (!(getObjectPathOfURI(treeNode, mainFile) instanceof Option.Some(var mainPath))) {
            this.service.send(new ClientEvent.Log("Main file not found: " + mainFile, MessageType.Warning));
            return;
        }

        this.service.send(new ClientEvent.Log("Running " + mainPath.mkString("::"), MessageType.Log));
        var runSettings = Option.some(new RunSettings(mainPath));
        compile(treeNode, files, ClientConfiguration.LoggingLevel.NONE, runSettings);

    }

    public void build(FileTreeNode treeNode, List<VirtualFile> files, ClientConfiguration.LoggingLevel loggingLevel) {
        compile(treeNode, files, loggingLevel, Option.none());
    }

    private void compile(
            FileTreeNode treeNode,
            List<VirtualFile> files,
            ClientConfiguration.LoggingLevel loggingLevel,
            Option<RunSettings> runSettings
    ) {
        for (var file : files) {
            this.service.send(new ClientEvent.PublishDiagnostic(file.uri(), List.of()));
        }
        var treeCopy = FileTreeNode.copyTree(treeNode);

        if (this.currentProcess instanceof Option.Some(var prevProcess)) {
            prevProcess.cancel();
        }

        this.currentProcess = Option.some(this.service.createProgress(
                "Compilation", progress -> {
                    var start = System.currentTimeMillis();
                    compileAsync(treeCopy, progress, loggingLevel, runSettings);
                    var end = System.currentTimeMillis();
                    this.service.send(
                            new ClientEvent.Log(
                                    "Compilation took " + (end - start) + "ms", MessageType.Log));
                    return "done";
                }
        ));

    }

    private void compileAsync(
            FileTreeNode treeCopy,
            Process.Progress progress,
            ClientConfiguration.LoggingLevel loggingLevel,
            Option<RunSettings> runSettings
    ) throws IOException {

        progress.notify("compiling files", 10);

        var errors = new DiagnosticCollection();
        var flight = new FlightRecordCollection();

        var latestModel = KarinaCompiler.runInContext(
                errors,
                null,
                flight,
                (c) -> run(c, treeCopy, runSettings)
        );
        synchronized (this) {
            this.lastestCompiledModel = Option.fromNullable(latestModel).or(this.lastestCompiledModel);
        }

        Log.updateLogLevel(loggingLevel.internalLogName());
        progress.notify("publishing errors", 80);

        if (latestModel == null) {
            if (errors.getTraces().isEmpty()) {
                this.service.send(new ClientEvent.Log(
                        "Compilation failed, but no errors were reported.",
                        MessageType.Warning
                ));
            } else {
//                send("Compilation failed with " + errors.getTraces().size() + " errors.");
            }
            pushErrors(errors);
        }

        progress.notify("sending flight record", 90);

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

    private void pushErrors(DiagnosticCollection logs) {
        Map<VirtualFile, List<Diagnostic>> diagnostics = new HashMap<>();

        for (var log : logs) {
            var information = new CodeDiagnosticInformation();
            log.entry().addInformation(information);

            var diagnosticAndFile = CodeDiagnosticInformation.toDiagnosticAndFile(information);
            if (diagnosticAndFile == null) {
                send("Could not convert diagnostic: " + information.getMessageString());
                continue;
            }
//            send(
//                    diagnosticAndFile.diagnostic().getRange(),
//                    diagnosticAndFile.diagnostic().getMessage()
//            );

            var diagnosticList =
                    diagnostics.computeIfAbsent(diagnosticAndFile.file(), _ -> new ArrayList<>());
            diagnosticList.add(diagnosticAndFile.diagnostic());
        }

        for (var value : diagnostics.entrySet()) {
            var file = value.getKey();
            var diagnosticsList = value.getValue();

            if (diagnosticsList.isEmpty()) {
                continue;
            }
//            send("Publishing " + diagnosticsList.size() + " diagnostics for " + file.uri());
            this.service.send(new ClientEvent.PublishDiagnostic(file.uri(), diagnosticsList));
        }
    }

    private void send(Object... obj) {
        var message = String.join(", ", Arrays.stream(obj)
                .map(Object::toString)
                .toList());
        this.service.send(new ClientEvent.Log(message, MessageType.Log));
    }

    private Option<ObjectPath> getObjectPathOfURI(VirtualFileTreeNode treeNode, URI uri) {
        var nodes = VirtualFileTreeNode.flatten(treeNode);

        for (var node : nodes) {
            if (node.content().uri().equals(uri)) {
                return Option.some(node.path());
            }
        }
        return Option.none();
    }

    public Option<MethodModel> findMain(VirtualFileTreeNode treeNode, URI uri) {
        if (!(lastestCompiledModel() instanceof Option.Some(var model))) {
            this.service.send(new ClientEvent.Log(
                    "no model: " + uri,
                    MessageType.Info
            ));
            return Option.none();
        }
        if (!(getObjectPathOfURI(treeNode, uri) instanceof Option.Some(var mainPath))) {
            this.service.send(new ClientEvent.Log(
                    "No path" + uri,
                    MessageType.Log
            ));
            return Option.none();
        }

        var classPointer = model.getClassPointer(KType.KARINA_LIB, mainPath);
        if (classPointer == null) {
            this.service.send(new ClientEvent.Log(
                    "No class: " + mainPath,
                    MessageType.Log
            ));
            return Option.none();
        }

        var currentClassModel = model.getClass(classPointer);

        for (var method : currentClassModel.methods()) {
            if (!Modifier.isStatic(method.modifiers()) || !Modifier.isPublic(method.modifiers())) {
                continue;
            }
            if (!method.name().equals("main")) {
                continue;
            }
            if (method.erasedParameters().size() != 1) {
                continue;
            }
            if (!method.signature().returnType().isVoid()) {
                continue;
            }
            var firstParam = method.erasedParameters().getFirst();
            if (!Types.erasedEquals(new KType.ArrayType(KType.STRING), firstParam)) {
                continue;
            }
            return Option.some(method);
        }
        return Option.none();
    }

    ///
    /// Compiles the given files.
    /// @param files the file tree to compile
    /// @return the compiled jar
    ///
    private Model run(Context c, FileTreeNode files, Option<RunSettings> runSettings) {

        // The 6 stages of the compiler:
        ParseProcessor parser = new ParseProcessor();
        ImportProcessor importProcessor = new ImportProcessor();
        AttributionProcessor attributionProcessor = new AttributionProcessor();
        LoweringProcessor lowering = new LoweringProcessor();

        var bytecodeClasses = cache.orElseGet(() ->  ModelLoader.getJarModel(c, true));

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

        if (runSettings instanceof Option.Some(var settings)) {
            GenerationProcessor backend = new GenerationProcessor();

            Log.begin("generation");
            var compiled = backend.compileTree(c, loweredTree, settings.main.mkString("."));
            Log.end("generation");

            var error = Result.safeCall(() -> {
                AutoRun.runWithPrints(compiled, false);
               return null;
            }).asError();
            if (error instanceof Option.Some(var e)) {
                this.service.send(new ClientEvent.Log(
                        "Error while running the compiled code: " + e.getMessage(),
                        MessageType.Error
                ));
            }
        }

        return loweredTree;
    }

    public Option<Model> lastestCompiledModel() {
        synchronized (this) {
            return Option.fromNullable(this.lastestCompiledModel.orElse(null));
        }
    }


    private record RunSettings(
            ObjectPath main
    ) {}
}
