package org.karina.lang.compiler.errors;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.Region;

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
    public void setPrimarySource(Region region) {

        append("");
        appendRegionFile(region);
        appendRegion(region);

    }

    @Override
    public void addSecondarySource(Region region) {
        append("");
        appendRegionFile(region);
        appendRegion(region);
    }

    public StringBuilder append(String line) {

        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;

    }

    private void appendRegionFile(Region region) {

        region = region.reorder();
        var path = region.source().resource().identifier();
        var column = region.start().column() + 1;
        var line = region.start().line() + 1;
        append("File: ").append(path).append(":").append(line).append(":").append(column);

    }

    private void appendRegion(Region region) {

        region = region.reorder();
        LogBuilder.getCodeOfRegion(region, true).forEach(this::append);
        append("");

    }


    public void appendStack(StackTraceElement[] stack) {

        append("");
        if (stack.length >= 3) {
            stack = Arrays.copyOfRange(stack, 3, stack.length);
        }

        for (var element : stack) {
            append("$TRACE at " + element);
        }

    }

}
