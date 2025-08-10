package org.karina.lang.compiler.logging;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleErrorInformation implements ErrorInformation {
    private String name = "";
    private final List<StringBuilder> lines = new ArrayList<>();

    @Override
    @Contract(mutates = "this")
    public void setTitle(String title) {
        this.name = title;
    }

    @Override
    @Contract(mutates = "this")
    public void setPrimarySource(Region region) {
        append("");
        appendRegionFile(null, region);
        appendRegion(region);
    }

    @Override
    @Contract(mutates = "this")
    public void addSecondarySource(Region region, String message) {
        var builder = append(" " +message);
        appendRegionFile(builder, region);
    }

    @Override
    @Contract(mutates = "this")
    public StringBuilder append(String line) {
        var builder = new StringBuilder(line);
        this.lines.add(builder);
        return builder;
    }

    public String readable() {
        var builder = new StringBuilder();
        builder.append(this.name).append("\n");
        for (var line : this.lines) {
            builder.append("    ").append(line).append("\n");
        }
        builder.append("\n");
        return builder.toString();
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
        ErrorInformation.getCodeOfRegion(region, true).forEach(this::append);
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
