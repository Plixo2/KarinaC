package org.karina.lang.compiler.logging;

import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.TextSource;

import java.util.ArrayList;
import java.util.List;

public interface ErrorInformation {

    @Contract(mutates = "this")
    void setTitle(String title);

    @Contract(mutates = "this")
    void setPrimarySource(Region region);

    @Contract(mutates = "this")
    void addSecondarySource(Region region, String... message);

    @Contract(mutates = "this")
    StringBuilder append(String s);

    static List<String> getCodeOfRegion(Region region, boolean annotate) {
        region = region.reorder();
        var builder = new ArrayList<StringBuilder>();
        if (region.start().line() == region.end().line()) {
            addSingleLineImpl(region.source(), annotate, region.start().line(), region.start().column(), region.end().column(), builder);
        } else {
            addMultiLineImpl(region, annotate, builder);
        }
        return stripLeadingUniform(builder.stream().map(StringBuilder::toString).toList());
    }

    private static void addMultiLineImpl(Region region, boolean annotate, List<StringBuilder> builder) {

        var lines = region.source().content().lines().toList();
        if (lines.isEmpty()) {
            return;
        }
        var startLine = region.start().line();
        var endLine = region.end().line();

        if (endLine - startLine > 40) {
            endLine = startLine + 35;
        }

        startLine = Math.min(Math.max(startLine, 0), lines.size() - 1);
        endLine = Math.min(Math.max(endLine, 0), lines.size() - 1);
        var startPad = " ".repeat(region.start().column());
        var startAnnotationPosition = "v";
        if (annotate) {
            builder.add(new StringBuilder(startPad).append(startAnnotationPosition).append(" "));
        }
        for (var i = startLine; i <= endLine; i++) {
            builder.add(new StringBuilder(lines.get(i)));
        }
        if (annotate) {
            var endPad = " ".repeat(Math.max(0, region.end().column() - 1));
            var endAnnotationPosition = "^";
            builder.add(new StringBuilder(endPad).append(endAnnotationPosition));
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

        var lines = source.content().lines().toList();
        if (lines.isEmpty()) {
            return;
        }
        line = Math.min(Math.max(line, 0), lines.size() - 1);
        var lineContent = lines.get(line);
        var padLeft = " ".repeat(from);
        var annotationPosition = "v".repeat(Math.max(to - from, 1));
        if (annotate) {
            builder.add(new StringBuilder(padLeft).append(annotationPosition).append(" "));
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
            if (leading == string.length()) {
                continue;
            }
            minLeading = Math.min(minLeading, leading);
        }
        var result = new ArrayList<String>();
        for (var string : strings) {
            if (minLeading >= string.length()) {
                result.add("");
            } else {
                result.add(string.substring(minLeading));
            }
        }
        return result;
    }

}
