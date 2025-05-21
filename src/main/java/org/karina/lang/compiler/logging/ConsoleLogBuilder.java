package org.karina.lang.compiler.logging;

import jdk.jfr.StackTrace;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
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
        appendRegionFile(null, region);
        appendRegion(region);

    }

    @Override
    public void addSecondarySource(Region region, String... message) {
        var builder = append("");
        for (var s : message) {
            builder.append(s);
        }
        appendRegionFile(builder, region);
//        appendRegion(region);
    }

    public StringBuilder append(String line) {

        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;

    }

    private void appendRegionFile(@Nullable StringBuilder builder, Region region) {

        region = region.reorder();
        var path = region.source().resource().identifier();
        var column = region.start().column() + 1;
        var line = region.start().line() + 1;
        if (builder == null) {
            builder = append("");
        }

        builder.append("file: ").append(path).append(":").append(line).append(":").append(column);

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
