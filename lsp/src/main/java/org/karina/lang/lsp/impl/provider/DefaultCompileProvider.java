package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.MessageType;
import org.karina.lang.compiler.Config;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.generate.GenerationProcessor;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.stages.parser.ParseProcessor;
import org.karina.lang.compiler.stages.writing.WritingProcessor;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.provider.CompileProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
public class DefaultCompileProvider implements CompileProvider {
    private final EventService eventService;

    @Override
    public String compile(ProviderArgs index, URI main) {
        if (!(index.getObjectPathOfURI(main) instanceof Option.Some(var path))) {
            this.eventService.send(new ClientEvent.Log(
                    "Main file not found: " + main,
                    MessageType.Error
            ));
            throw new CompilationFailedException();
        }
        return compile(index, path);
    }

    @Override
    public String compile(ProviderArgs index, ObjectPath main) {

        var jobObj = this.eventService.createJob(
                "Compiling '" + main.mkString("::") + "'",
                job -> run(job, index.fileTree().root(), main)
        );

        return jobObj.awaitResult();

    }

    private String run(JobProgress job, FileTreeNode files, ObjectPath main) {
        job.notify("Starting", 10);

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var flight = new FlightRecordCollection();

        var config = Context.ContextHandling.of(
                true,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var fileTree = FileTreeNode.copyTree(files);

        if (job.isCancelled()) {
            return "Cancelled";
        }

        var latestModel = Context.run(
                config,
                errors,
                warnings,
                flight,
                (c) -> runInContext(job, c, fileTree, main)
        );
        job.notify("Finished", 100);

        if (latestModel == null) {
            for (var error : errors) {
                this.eventService.send(new ClientEvent.Log(
                        error.mkString(false),
                        MessageType.Error
                ));
            }
            for (var error : warnings) {
                this.eventService.send(new ClientEvent.Log(
                        error.mkString(false),
                        MessageType.Warning
                ));
            }
            this.eventService.send(new ClientEvent.Popup(
                    "Compilation failed with " + errors.getTraces().size() + " errors and " + warnings.getTraces().size() + " warnings",
                    MessageType.Error
            ));
            throw new CompilationFailedException();
        } else {
            String message;
            if (warnings.getTraces().isEmpty()) {
                message = "Compilation successful";
            } else {
                message = "Compilation successful with " + warnings.getTraces().size() + " warnings";
            }
            return message;
        }
    }

    private static String runInContext(JobProgress job, Context c, FileTreeNode fileTree, ObjectPath main) {

        ParseProcessor parser = new ParseProcessor();
        ImportProcessor importProcessor = new ImportProcessor();
        AttributionProcessor attributionProcessor = new AttributionProcessor();
        LoweringProcessor lowering = new LoweringProcessor();
        GenerationProcessor backend = new GenerationProcessor();
        WritingProcessor writing = new WritingProcessor();

        job.notify("Reading Binary", 20);
        var jarModel = ModelLoader.getJarModel(c, true);
        KType.validateBuildIns(c, jarModel);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Parsing", 30);

        var userModel = parser.parseTree(c, fileTree);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Merge", 40);

        var languageModel = ModelBuilder.merge(c, userModel, jarModel);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Importing", 50);

        var importedTree = importProcessor.importTree(c, languageModel);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Attribution", 60);

        var attributedTree = attributionProcessor.attribTree(c, importedTree);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Lowering", 70);

        var loweredTree = lowering.lowerTree(c, attributedTree);
        if (job.isCancelled()) return "Cancelled";
        job.notify("Generating", 80);

        var compiled = backend.compileTree(c, loweredTree, main.mkString("."));
        if (job.isCancelled()) return "Cancelled";
        job.notify("Writing", 90);

        var out = Path.of("build/");
        record SimpleOutConfig(Path outputFile, boolean emitClassFiles) implements Config.OutputConfig {}
        var config = new SimpleOutConfig(out.resolve("out/build.jar"), true);
        writing.writeCompilation(c, compiled, config);
        if (job.isCancelled()) return "Cancelled";
        try {
            putKarinaLib(out);
            putScript(out, WINDOWS_COMMAND, "run.bat");
            putScript(out, LINUX_COMMAND, "run");
        } catch (IOException e) {
            Log.internal(c, e);
            throw new Log.KarinaException();
        }

        return "OK";
    }

    private static final String WINDOWS_COMMAND =
            """
            @echo off
            pushd "%~dp0"
            java -cp "out/build.jar;libs/karina.base.jar" main %*
            popd
            """;

    private static final String LINUX_COMMAND =
            """
            #!/bin/bash
            
            pushd "$(dirname "$0")" > /dev/null
            java -cp "out/build.jar:libs/karina.base.jar" main "$@"
            popd > /dev/null
            """;

    private static void putScript(Path buildDir, String content, String file) throws IOException {
        var script = buildDir.resolve(file);

        Files.write(script, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void putKarinaLib(Path buildDir) throws IOException {
        var jarDest = buildDir.resolve("libs/karina.base.jar");

        Path destinationDir = jarDest.getParent();
        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        try (var resourceStream = DefaultCompileProvider.class.getResourceAsStream("/karina.base.jar")) {
            if (resourceStream == null) {
                System.out.println("Could not find karina.base.jar");
                return;
            }

            try (var outputStream = new FileOutputStream(jarDest.toFile())){
                outputStream.write(resourceStream.readAllBytes());
            }
        }
    }

    public static class CompilationFailedException extends RuntimeException {
        private CompilationFailedException() {
            super("Compilation failed");
        }
    }
}
