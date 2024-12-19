package org.karina.lang.lsp;

import lombok.AllArgsConstructor;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.LogBuilder;
import org.karina.lang.compiler.errors.LogCollector;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.lsp.fs.KarinaFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ErrorHandler {
    private List<Error> errors;

    //updates all diagnostics in a file
    public void pushErrorsToFile() {
        var diagnostics = new HashMap<KarinaFile, List<Diagnostic>>();
        for (var error : this.errors) {
            var diagnosticAndFile = convertError(error);
            if (diagnosticAndFile == null) {
                continue;
            }
            diagnostics.putIfAbsent(diagnosticAndFile.file, new ArrayList<>());
            diagnostics.get(diagnosticAndFile.file).add(diagnosticAndFile.diagnostic);
        }
        diagnostics.forEach(KarinaFile::setDiagnostics);

    }

    public static @Nullable ErrorHandler tryInternal(Runnable runnable) {
        try {
            runnable.run();
            return null;
        } catch (Log.KarinaException ignored) {
            return new ErrorHandler(Log.getLogs());
        } finally {
            Log.clearLogs();
        }
    }

    public static <T> Result<T> mapInternal(Supplier<T> supplier) {
        try {
            return new Result.onSuccess<>(supplier.get());
        } catch (Log.KarinaException e) {
            return new Result.onFailure<>(new ErrorHandler(Log.getLogs()));
        } finally {
            Log.clearLogs();
        }
    }


    public sealed interface Result<T> {

        record onSuccess<T>(T result) implements Result<T> {}
        record onFailure<T>(ErrorHandler error) implements Result<T> {}

    }


    private @Nullable DiagnosticForFile convertError(Error error) {
        var collector = new LogCollector<DiagnosticLogBuilder>();

        var builder = collector.populate(error, new DiagnosticLogBuilder());

        var primaryRegion = builder.code();
        if (primaryRegion == null) {
            return null;
        }
        if (!(primaryRegion.source().resource() instanceof KarinaFile file)) {
            return null;
        }

        var primaryRange = EventHandler.regionToRange(primaryRegion);
        var message = builder.getMessage();
        var code = String.join("\n", LogBuilder.getCodeOfRegion(primaryRegion, true));

        var secondarySources = builder.related();
        var relatedInfos = secondarySources.stream().map(ref -> {
            var relatedRange = EventHandler.regionToRange(ref);
            if (!(ref.source().resource() instanceof KarinaFile relatedFile)) {
                return null;
            }
            var relatedLocation = new Location(relatedFile.uriPath().toUri().toString(), relatedRange);
            return new DiagnosticRelatedInformation(relatedLocation, "Related");
        }).filter(Objects::nonNull).toList();

        var formatted = """
                %s
                %s
                
                """.formatted(message, code);

        var diagnostic = new Diagnostic(primaryRange, formatted, DiagnosticSeverity.Error, "Karina");
        diagnostic.setRelatedInformation(relatedInfos);

        return new DiagnosticForFile(file, diagnostic);
    }

    private record DiagnosticForFile(KarinaFile file, Diagnostic diagnostic) {
    }
}
