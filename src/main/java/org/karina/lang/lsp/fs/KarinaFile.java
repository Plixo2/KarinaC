package org.karina.lang.lsp.fs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.Diagnostic;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.TextSource;
import org.karina.lang.compiler.objects.KTree;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class KarinaFile implements TextSource.AbstractResource {
    @Getter
    @Accessors(fluent = true)
    private final String name;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private @Nullable KarinaFileState state;

    @Getter
    @Accessors(fluent = true)
    private final ObjectPath path;

    @Getter
    @Accessors(fluent = true)
    private final Path uriPath;

    public boolean dirtyDiagnosticsFlag = true;

    private final List<Diagnostic> diagnostics = new ArrayList<>();
    public void clearDiagnostics() {
        if (this.diagnostics.isEmpty()) {
            return;
        }
        this.dirtyDiagnosticsFlag = true;
        this.diagnostics.clear();
    }

    public void setDiagnostics(List<Diagnostic> diagnostics) {
        if (diagnostics.isEmpty() && this.diagnostics.isEmpty()) {
            return;
        }
        this.dirtyDiagnosticsFlag = true;
        this.diagnostics.clear();
        this.diagnostics.addAll(diagnostics);
    }

    public List<Diagnostic> getAndClearDiagnostics() {
        this.dirtyDiagnosticsFlag = false;
        return new ArrayList<>(this.diagnostics);
    }

    public boolean isDirtyDiagnostic() {
        return this.dirtyDiagnosticsFlag;
    }

    @Override
    public String errorString() {
        return this.path.mkString(".");
    }

    public boolean isTyped() {
        return this.state() instanceof KarinaFileState.Typed;
    }

    public sealed interface KarinaFileState {
        TextSource source();

        record Raw(TextSource source) implements KarinaFileState {}
        record Typed(TextSource source, KTree.KUnit unit) implements KarinaFileState {}
    }
}