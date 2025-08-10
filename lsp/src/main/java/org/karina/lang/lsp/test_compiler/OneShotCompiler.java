package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageType;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.logging.DiagnosticCollection;
import org.karina.lang.compiler.logging.FlightRecordCollection;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.utils.DefaultTextSource;
import org.karina.lang.compiler.utils.FileNode;
import org.karina.lang.compiler.utils.FileTreeNode;
import org.karina.lang.lsp.base.Process;
import org.karina.lang.lsp.events.ClientEvent;
import org.karina.lang.lsp.events.EventService;
import org.karina.lang.lsp.impl.CodeDiagnosticInformation;
import org.karina.lang.lsp.lib.ClientConfiguration;
import org.karina.lang.lsp.lib.VirtualFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OneShotCompiler {
    private final EventService service;

    private Option<Process> prev = Option.none();

    public void compile(FileTreeNode treeNode, List<VirtualFile> files, ClientConfiguration.LoggingLevel loggingLevel) {
        for (var file : files) {
            this.service.send(new ClientEvent.PublishDiagnostic(file.uri(), List.of()));
        }
        var treeCopy = copyTree(treeNode);

        if (this.prev instanceof Option.Some(var prevProcess)) {
            prevProcess.cancel();
        }

        this.prev = Option.some(this.service.createProgress(
                "Compilation", progress -> {
                    var start = System.currentTimeMillis();
                    compileAsync(treeCopy, progress, loggingLevel);
                    var end = System.currentTimeMillis();
                    this.service.send(
                            new ClientEvent.Log(
                                    "Compilation took " + (end - start) + "ms", MessageType.Log));
                    return "done";
                }
        ));

    }

    private void compileAsync(FileTreeNode treeCopy, Process.Progress progress, ClientConfiguration.LoggingLevel loggingLevel) throws IOException {
        progress.notify("prepare compiler", 10);

        var builder = KarinaCompiler.builder();
        builder.outputConfig(null);
        builder.useBinaryFormat(true);
        var errors = new DiagnosticCollection();
        var flight = new FlightRecordCollection();
        builder.errorCollection(errors);
        builder.flightRecordCollection(flight);
        var compiler = builder.build();

        progress.notify("compiling files", 20);
        Log.updateLogLevel(loggingLevel.internalLogName());
        var result = compiler.compile(treeCopy);

        progress.notify("publishing errors", 80);

        if (result == null) {
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
                continue;
            }
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

            this.service.send(new ClientEvent.PublishDiagnostic(file.uri(), diagnosticsList));
        }
    }

    private static DefaultFileTree copyTree(FileTreeNode tree) {
        return new DefaultFileTree(
                tree.path(),
                tree.name(),
                tree.children().stream()
                        .map(OneShotCompiler::copyTree)
                        .toList(),
                tree.leafs().stream()
                        .map(OneShotCompiler::copyFile)
                        .toList()
        );
    }
    private static FileNode copyFile(FileNode file) {
        return new DefaultFileTree.DefaultFileNode(
                file.path(),
                file.name(),
                new DefaultTextSource(file.content().resource(), file.content().content())
        );
    }


}
