package org.karina.lang.lsp.lib;

import karina.lang.Option;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.lsp.lib.events.EventService;

import java.net.URI;

public interface VirtualFile extends Resource, TextSource {

    /// Always absolut and normalized
    URI uri();

    String content();

    int version();

    boolean isOpen();

    @Contract(mutates = "this")
    void updateContent(String newContent, int version);

    @Contract(mutates = "this")
    void updateContent(Range range, String newContent, int version, EventService eventService);

    @Contract(mutates = "this")
    void open();

    @Contract(mutates = "this")
    void close();

    @Override
    default String identifier() {
        return uri().toString();
    }

    @Override
    default Resource resource() {
        return this;
    }

    sealed interface CompiledFileCacheState {

        KarinaParser.UnitContext parsedUnit();
        DiagnosticCollection errors();
        DiagnosticCollection warnings();

        record ParseError(
                KarinaParser.UnitContext parsedUnit,
                DiagnosticCollection errors,
                DiagnosticCollection warnings
        ) implements CompiledFileCacheState {}
        record ConversionError(
                KarinaParser.UnitContext parsedUnit,
                DiagnosticCollection errors,
                DiagnosticCollection warnings
        ) implements CompiledFileCacheState {}
        record Success(
                KarinaParser.UnitContext parsedUnit,
                Model allClasses,
                DiagnosticCollection errors, // should be empty
                DiagnosticCollection warnings
        ) implements CompiledFileCacheState {}

    }
}
