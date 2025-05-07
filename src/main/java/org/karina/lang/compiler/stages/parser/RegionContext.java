package org.karina.lang.compiler.stages.parser;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;

/**
 * Converts an ANTLR {@link org.antlr.v4.runtime.misc.Interval} to a {@link Region}.
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class RegionContext {
    private final TextSource source;
    private final TokenStream stream;

    public Region toRegion(ParseTree ctx) {
        return toRegion(ctx.getSourceInterval());
    }

    public <T> RegionOf<T> region(T t, Interval interval) {
        return RegionOf.region(toRegion(interval), t);
    }


    public RegionOf<String> region(KarinaParser.IdContext n) {
        return region(escapeID(n), n.getSourceInterval());
    }

    public String escapeID(KarinaParser.IdContext context) {
        var str = context.getText();
        return escapeID(str);
    }

    public String escapeID(String str) {
        if (str.startsWith("\\")) {
            return str.substring(1);
        }
        return str;
    }



    public Region toRegion(Interval interval) {
        var invalid = new Region(
                this.source,
                new Region.Position(-1, 0),
                new Region.Position(-1, 0)
        );
        if (interval.a < 0 || interval.b < 0 || interval.a > interval.b) {
            return invalid;
        }
        try {
            var fromToken = this.stream.get(interval.a);
            var fromLine = fromToken.getLine() - 1;
            var fromColumn = fromToken.getCharPositionInLine();
            var toToken = this.stream.get(interval.b);
            var toLine = toToken.getLine() - 1;
            var toColumn = toToken.getCharPositionInLine() + toToken.getText().length();
            return new Region(
                    this.source,
                    new Region.Position(fromLine, fromColumn),
                    new Region.Position(toLine, toColumn)
            );
        } catch (IndexOutOfBoundsException e) {
            return invalid;
        }

    }

}
