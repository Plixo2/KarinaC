package org.karina.lang.compiler.stages.parser;


import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.utils.SpanOf;

/**
 * Converts an ANTLR {@link org.antlr.v4.runtime.misc.Interval} to a {@link Span}.
 */
@RequiredArgsConstructor
public class TextContext {
    private final TextSource source;
    private final TokenStream stream;

    public Span toRegion(ParseTree ctx) {
        return toRegion(ctx.getSourceInterval());
    }

    public <T> SpanOf<T> span(T t, Interval interval) {
        return SpanOf.span(toRegion(interval), t);
    }

    public SpanOf<String> span(TerminalNode n) {
        return span(n.getText(), n.getSourceInterval());
    }


    public Span toRegion(Interval interval) {
        var invalid = new Span(
                this.source,
                new Span.Position(-1, 0),
                new Span.Position(-1, 0)
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
            return new Span(
                    this.source,
                    new Span.Position(fromLine, fromColumn),
                    new Span.Position(toLine, toColumn)
            );
        } catch (IndexOutOfBoundsException e) {
            return invalid;
        }

    }

}
