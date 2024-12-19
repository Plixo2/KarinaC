package org.karina.lang.lsp;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.LogBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class DiagnosticLogBuilder implements LogBuilder {
    private String title;
    @Nullable private Span code = null;
    private final List<Span> related = new ArrayList<>();
    private final List<StringBuilder> lines = new ArrayList<>();

    @Override
    public LogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void setPrimarySource(Span region) {
        this.code = region;
    }

    @Override
    public void addSecondarySource(Span region) {
        this.related.add(region);
    }

    public StringBuilder append(String initial) {
        var builder = new StringBuilder(initial);
        this.lines.add(builder);
        return builder;
    }
    public void setCode(Span region) {
        if (this.code != null) {
            this.code = region;
        }
    }
    public void addRelated(Span span) {
        this.related.add(span);
    }


    public String getMessage() {
        var lower = String.join("\n", this.lines.stream().map(StringBuilder::toString).toList());
        if (this.title == null) {
            return lower;
        }
        var upper = this.title + "\n";
        return upper + lower;
    }
}
