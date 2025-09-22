package org.karina.lang.lsp.impl;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.MessageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.stages.imports.ImportProcessor;
import org.karina.lang.compiler.stages.lower.LoweringProcessor;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileElevator;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.process.Job;
import org.karina.lang.lsp.lib.process.JobProgress;
import org.karina.lang.lsp.lib.provider.CodeActionProvider;
import org.karina.lang.lsp.lib.ModelUpdater;

import org.karina.lang.lsp.test_compiler.CompiledModelIndex;


import java.net.URI;
import java.util.*;


@RequiredArgsConstructor
public class DefaultModelUpdater implements ModelUpdater {
    private final EventService eventService;

    private Option<Model> cache = Option.none();

    @Override
    public Job<@Nullable CompiledModelIndex> update(
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator,
            CodeActionProvider actionProvider,
            VirtualFile file,
            ClientConfiguration.LoggingLevel loggingLevel
    ) {
        this.clearErrors(fileTree.files());
        elevator.clearCompiledCache(file);

        return this.eventService.createJob("Analysis",
                job -> elevateTree(job, fileTree, elevator, actionProvider, loggingLevel)
        );
    }

    //<editor-fold defaultstate="collapsed" desc="Elevate Tree">
    private @Nullable CompiledModelIndex elevateTree(
            JobProgress job,
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator,
            CodeActionProvider actionProvider,
            ClientConfiguration.LoggingLevel loggingLevel
    ) {
        job.notify("Elevating current File", 20);

        if (job.isCancelled()) {
            return null;
        }

        var config = Context.ContextHandling.of(
                loggingLevel != ClientConfiguration.LoggingLevel.NONE,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();
        var flight = new FlightRecordCollection();

        var toFill = new CompiledMutableModelIndex();
        var _ = Context.run(
                config,
                errors,
                warnings,
                flight,
                (c) -> elevateTreeInContext(job, c, fileTree, elevator, toFill)
        );
        if (job.isCancelled()) {
            return null;
        }
        job.notify("Publishing", 100);

        this.pushErrors(
                job,
                errors,
                warnings,
                actionProvider
        );

        if (config.traces()) {
            FlightRecordCollection.print(flight, false, System.out);
        }

        return new CompiledModelIndex(
                this.cache,
                toFill.userModel,
                toFill.languageModel,
                toFill.importedModel,
                toFill.attributedModel,
                toFill.loweredModel
        );
    }

    private @NotNull String elevateTreeInContext(
            JobProgress job,
            Context c,
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator,
            CompiledMutableModelIndex toFill
    ) {
        var importProcessor      = new ImportProcessor();
        var attributionProcessor = new AttributionProcessor();
        var lowering             = new LoweringProcessor();

        Model userModel;
        try (var _ = c.section(Logging.Parsing.class,"parsing")) {
            userModel = createUserModel(c, fileTree, elevator, job);
        }
        toFill.userModel = Option.some(userModel);
        if (job.isCancelled()) return "Cancelled";

        var bytecodeClasses = this.cache.orElseGet(() -> {
            var bytecode = ModelLoader.getJarModel(c, true);
            KType.validateBuildIns(c, bytecode);
            return bytecode;
        });
        this.cache = Option.some(bytecodeClasses);
        if (job.isCancelled()) return "Cancelled";


        Model languageModel;
        try (var _ = c.section(Logging.Merging.class,"merging")) {
            languageModel = ModelBuilder.merge(c, userModel, bytecodeClasses);
        }
        toFill.languageModel = Option.some(languageModel);
        if (job.isCancelled()) return "Cancelled";

        job.notify("Importing", 60);
        Model importedTree;
        try (var _ = c.section(Logging.Importing.class,"importing")) {
            importedTree = importProcessor.importTree(c, languageModel);
        }
        toFill.importedModel = Option.some(importedTree);
        if (job.isCancelled()) return "Cancelled";

        job.notify("Attribution", 80);
        Model attributedTree;
        try (var _ = c.section(Logging.Attribution.class,"attributing")) {
            attributedTree = attributionProcessor.attribTree(c, importedTree);
        }
        toFill.attributedModel = Option.some(attributedTree);
        if (job.isCancelled()) return "Cancelled";

        job.notify("Lowering", 90);
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
        if (job.isCancelled()) return "Cancelled";

        return "Ok";
    }

    private Model createUserModel(
            Context c,
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator,
            JobProgress job
    ) {
        @Nullable ModelBuilder builder = new ModelBuilder();
        int fileCount = fileTree.files().size();
        int currentFile = 0;

        for (var virtualFile : fileTree.files()) {
            var percent = 20 + (int)(((double) currentFile / (double)fileCount) * 40.0);
            job.notify("Elevating " + currentFile + "/" + fileCount + " (" + virtualFile.uri() + ")", percent);
            currentFile++;
            if (job.isCancelled()) {
                return new ModelBuilder().build(c);
            }
            var cache = elevator.awaitCache(fileTree, virtualFile);
            if (!(cache instanceof Option.Some(var cacheState))) {
                this.eventService.warningMessage("File " + virtualFile.uri() + " has no compiled cache.");
                return new ModelBuilder().build(c);
            }
            for (var error : cacheState.errors()) {
                c.addError(error);
            }
            for (var warning : cacheState.warnings()) {
                c.addWarn(warning);
            }

            if (cacheState instanceof VirtualFile.CompiledFileCacheState.Success success) {
                if (builder != null) {
                    builder.add(c, success.allClasses());
                }
            } else {
                builder = null;
            }

        }
        if (builder == null) {
            throw new Log.KarinaException();
        }
        return builder.build(c);
    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Publish Errors">
    private void clearErrors(Collection<VirtualFile> files) {
        for (var file : files) {
            this.eventService.send(new ClientEvent.PublishDiagnostic(file.uri(), List.of()));
        }
    }

    private void pushErrors(
            JobProgress ref,
            DiagnosticCollection errors,
            DiagnosticCollection warnings,
            CodeActionProvider actionProvider
    ) {
        Map<URI, List<Diagnostic>> diagnostics = new HashMap<>();

        addLogs(errors, diagnostics, DiagnosticSeverity.Error, Option.some(actionProvider));
        addLogs(warnings, diagnostics, DiagnosticSeverity.Information, Option.some(actionProvider));

        if (ref.isCancelled()) {
            return;
        }

        for (var value : diagnostics.entrySet()) {
            var file = value.getKey();
            var diagnosticsList = value.getValue();

            if (diagnosticsList.isEmpty()) {
                continue;
            }
            this.eventService.send(new ClientEvent.PublishDiagnostic(file, diagnosticsList));
        }
    }
    //</editor-fold>

    private void addLogs(
            DiagnosticCollection errors,
            Map<URI, List<Diagnostic>> diagnostics,
            DiagnosticSeverity severity,
            Option<CodeActionProvider> actionProvider
    ) {
        for (var log : errors) {
            var information = new CodeDiagnosticInformationBuilder();
            var error = log.entry();
            error.addInformation(information);

            var diagnosticAndFile = CodeDiagnosticInformationBuilder.toDiagnosticAndFile(information, severity);
            if (diagnosticAndFile == null) {
                this.eventService.send(new ClientEvent.Log("Could not convert diagnostic: " + information.getMessageString(), MessageType.Log));
                continue;
            }
            var diagnostic = diagnosticAndFile.diagnostic();

            if (actionProvider instanceof Option.Some(var ap)) {
                var action = ap.getCodeActionFromError(error, diagnosticAndFile.file());
                diagnostic.setData(action);
            }

            var diagnosticList =
                    diagnostics.computeIfAbsent(diagnosticAndFile.file(), _ -> new ArrayList<>());
            diagnosticList.add(diagnostic);
        }
    }

    private static class CompiledMutableModelIndex {
        private Option<Model> userModel       = Option.none();
        private Option<Model> languageModel   = Option.none();
        private Option<Model> importedModel   = Option.none();
        private Option<Model> attributedModel = Option.none();
        private Option<Model> loweredModel    = Option.none();
    }


}
