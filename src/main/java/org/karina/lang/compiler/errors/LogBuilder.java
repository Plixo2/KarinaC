package org.karina.lang.compiler.errors;

import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.api.TextSource;

import java.util.ArrayList;
import java.util.List;

public interface LogBuilder {
    LogBuilder setTitle(String title);
    void setPrimarySource(Span region);
    void addSecondarySource(Span region);
    StringBuilder append(String s);

    static List<String> getCodeOfRegion(Span region, boolean annotate) {
        region = region.reorder();
        var builder = new ArrayList<StringBuilder>();
        if (region.start().line() == region.end().line()) {
            addSingleLineImpl(region.source(), annotate, region.start().line(), region.start().column(), region.end().column(), builder);
        } else {
            addMultiLineImpl(region, annotate, builder);
        }
        return stripLeadingUniform(builder.stream().map(StringBuilder::toString).toList());
    }

    private static void addMultiLineImpl(Span region, boolean annotate, List<StringBuilder> builder) {

        var lines = region.source().lines();
        var startLine = region.start().line();
        var endLine = region.end().line();
        startLine = Math.min(Math.max(startLine, 0), lines.size() - 1);
        endLine = Math.min(Math.max(endLine, 0), lines.size() - 1);
        var startPad = " ".repeat(region.start().column());
        var startAnnotationSpan = "v";
        if (annotate) {
            builder.add(new StringBuilder(startPad).append(startAnnotationSpan).append(" "));
        }
        for (var i = startLine; i <= endLine; i++) {
            builder.add(new StringBuilder(lines.get(i)));
        }
        if (annotate) {
            var endPad = " ".repeat(region.end().column());
            var endAnnotationSpan = "^";
            builder.add(new StringBuilder(endPad).append(endAnnotationSpan));
        }
    }

    private static void addSingleLineImpl(
            TextSource source,
            boolean annotate,
            int line,
            int from,
            int to,
            List<StringBuilder> builder)
    {

        var lines = source.lines();
        line = Math.min(Math.max(line, 0), lines.size() - 1);
        var lineContent = lines.get(line);
        var padLeft = " ".repeat(from);
        var annotationSpan = "v".repeat(Math.max(to - from, 1));
        if (annotate) {
            builder.add(new StringBuilder(padLeft).append(annotationSpan).append(" "));
        }
        builder.add(new StringBuilder(lineContent));

    }

    //removes the same number of leading spaces from each line
    private static List<String> stripLeadingUniform(List<String> strings) {

        var minLeading = Integer.MAX_VALUE;
        for (var string : strings) {
            var leading = 0;
            while (leading < string.length() && Character.isWhitespace(string.charAt(leading))) {
                leading++;
            }
            minLeading = Math.min(minLeading, leading);
        }
        var result = new ArrayList<String>();
        for (var string : strings) {
            result.add(string.substring(minLeading));
        }
        return result;
    }
}
