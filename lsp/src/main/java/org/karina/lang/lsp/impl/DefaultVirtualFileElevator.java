package org.karina.lang.lsp.impl;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.*;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileElevator;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class DefaultVirtualFileElevator implements VirtualFileElevator {
    private final EventService eventService;

    private final ConcurrentMap<VirtualFile, Option<VirtualFile.CompiledFileCacheState>>
            cache = new ConcurrentHashMap<>();


    @Override
    public void clearCompiledCache(VirtualFile file) {
        this.cache.remove(file);
    }

    @Override
    public Option<VirtualFile.CompiledFileCacheState> awaitCache(
            VirtualFileTreeNode.NodeMapping mapping,
            VirtualFile file
    ) {
        return this.cache.computeIfAbsent(
                file,
                _ -> elevateFileWithTimings(mapping, file)
        );
    }


    private Option<VirtualFile.CompiledFileCacheState> elevateFileWithTimings(
            VirtualFileTreeNode.NodeMapping mapping,
            VirtualFile file
    ) {
        var start = System.currentTimeMillis();
        var result = elevateFile(mapping, file);
        var state = switch (result) {
            case Option.None()
                    -> "ERROR";
            case Option.Some(VirtualFile.CompiledFileCacheState.ParseError _)
                    -> "PARSE ERROR";
            case Option.Some(VirtualFile.CompiledFileCacheState.ConversionError _)
                    -> "CONVERSION ERROR";
            case Option.Some(VirtualFile.CompiledFileCacheState.Success _)
                    -> "OK";
        };
        this.eventService.timing("UPDATE", file.identifier(), state, start);
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Elevate File">
    private Option<VirtualFile.CompiledFileCacheState> elevateFile(
            VirtualFileTreeNode.NodeMapping mapping,
            VirtualFile file
    ) {
        var virtualFileNode = mapping.fileMapping().get(file);
        if (virtualFileNode == null) {
            return Option.none();
        }
        return elevateFile(virtualFileNode);
    }

    private Option<VirtualFile.CompiledFileCacheState> elevateFile(
            VirtualFileTreeNode.VirtualFileNode file
    ) {

        var errors = new DiagnosticCollection();
        var warnings = new DiagnosticCollection();

        var config = Context.ContextHandling.of(
                true,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        record ParseError(KarinaParser.UnitContext unit) {}
        record ConversionError(KarinaParser.UnitContext unit) {}
        record Success(KarinaParser.UnitContext unit, Model allClasses) {}

        Object[] resultHolder = new Object[1];
        var _ = Context.run(
                config,
                errors,
                warnings,
                null,
                (c) -> {
                    var copy = FileTreeNode.copyFile(file);
                    var unit = parse(c, copy.content());
                    if (c.hasErrors()) {
                        resultHolder[0] = new ParseError(unit);
                        throw new Log.KarinaException();
                    }
                    Model model;
                    try {
                        var modelBuilder = new ModelBuilder();
                        convert(c, unit, copy.content(), file.name(), file.path(), modelBuilder);
                        model = modelBuilder.build(c);
                    } catch(Log.KarinaException e) {
                        resultHolder[0] = new ConversionError(unit);
                        throw e;
                    }
                    resultHolder[0] = new Success(unit, model);

                    return "OK";
                }
        );
        var state = switch (resultHolder[0]) {
            case ParseError(KarinaParser.UnitContext unit) ->
                    new VirtualFile.CompiledFileCacheState.ParseError(
                            unit,
                            errors,
                            warnings
                    );
            case ConversionError(KarinaParser.UnitContext unit) ->
                    new VirtualFile.CompiledFileCacheState.ConversionError(
                            unit,
                            errors,
                            warnings
                    );
            case Success(KarinaParser.UnitContext unit, Model allModels) ->
                    new VirtualFile.CompiledFileCacheState.Success(
                            unit,
                            allModels,
                            errors,
                            warnings
                    );
            case null -> {
                this.eventService.warningMessage("Internal error during parsing/conversion, no result.");
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + resultHolder[0]);
        };
        return Option.fromNullable(state);

    }

    private static KarinaParser.UnitContext parse(
            Context c,
            TextSource source
    ) {
        var errorListener = new JustLogErrorListener(c, source);
        var inputStream = CharStreams.fromString(source.content());
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        var karinaParser = new KarinaParser(tokenStream);
        karinaParser.removeErrorListeners();
        karinaParser.addErrorListener(errorListener);

        return karinaParser.unit();
    }

    private static void convert(
            Context c,
            KarinaParser.UnitContext unit,
            TextSource source,
            String name,
            ObjectPath path,
            ModelBuilder builder
    ) {
        var regionConverter = new RegionContext(source);
        var visitor = new KarinaUnitVisitor(c, regionConverter, name, path);
        visitor.visit(unit, builder);
    }


    private static class JustLogErrorListener extends BaseErrorListener {
        private final TextSource source;
        private final Context c;

        private JustLogErrorListener(Context c, TextSource source) {
            this.source = source;
            this.c = c;
        }


        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e
        ) {
            Region region;
            if (offendingSymbol instanceof Token token) {
                line = token.getLine();
                charPositionInLine = token.getCharPositionInLine();
                var width = token.getText() != null ? token.getText().length() : 1;

                region = new Region(
                        this.source,
                        new Region.Position(line - 1, charPositionInLine),
                        new Region.Position(line - 1, charPositionInLine + width)
                );
            } else {
                region = new Region(
                        this.source,
                        new Region.Position(line - 1, charPositionInLine),
                        new Region.Position(line - 1, charPositionInLine + 1)
                );
            }



            var name = "?";
            if (e != null) {
                name = e.getClass().getSimpleName();
            }
            Log.syntaxError(this.c, region, name + " -> " + msg);

        }
    }

}
