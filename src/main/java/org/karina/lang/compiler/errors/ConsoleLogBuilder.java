package org.karina.lang.compiler.errors;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.TextSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class ConsoleLogBuilder implements LogBuilder {
    private String name = "";
    private final List<StringBuilder> lines = new ArrayList<>();

    @Override
    public LogBuilder setTitle(String title) {
        this.name = title;
        return this;
    }

    @Override
    public void setPrimarySource(Span region) {
        append("");
        appendRegionFile(region);
        appendRegion(region);
    }

    @Override
    public void addSecondarySource(Span region) {
        append("");
        appendRegionFile(region);
        appendRegion(region);
    }

    public StringBuilder append(String line) {

        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;

    }

    private void appendRegionFile(Span region) {

        region = region.reorder();
        var path = region.source().resource().errorString();
        var column = region.start().column() + 1;
        var line = region.start().line() + 1;
        append("File: ").append(path).append(":").append(line).append(":").append(column);

    }

    private void appendRegion(Span region) {

        region = region.reorder();
        LogBuilder.getCodeOfRegion(region, true).forEach(this::append);
        append("");

    }


    public void appendStack(StackTraceElement[] stack) {

        if (stack.length >= 3) {
            stack = Arrays.copyOfRange(stack, 3, stack.length);
        }

        append(Log.ERROR_TRACE);
        for (var element : stack) {
            append("$TRACE at " + element);
        }
        append(Log.ERROR_MESSAGE_COLOR);

    }

}