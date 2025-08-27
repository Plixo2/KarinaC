package org.karina.lang.compiler.stages.parser;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
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

    public Region toRegion(TerminalNode ctx) {
        var token = ctx.getSymbol();
        var fromLine = token.getLine() - 1;
        var fromColumn = token.getCharPositionInLine();
        var toLine = token.getLine() - 1;
        var toColumn = token.getCharPositionInLine() + token.getText().length();
        return new Region(
                this.source,
                new Region.Position(fromLine, fromColumn),
                new Region.Position(toLine, toColumn)
        );
    }

    public Region toRegion(ParserRuleContext ctx) {
        var fromLine = ctx.start.getLine() - 1;
        var fromColumn = ctx.start.getCharPositionInLine();
        var toLine = ctx.stop.getLine() - 1;
        var toColumn = ctx.stop.getCharPositionInLine() + ctx.stop.getText().length();
        return new Region(
                this.source,
                new Region.Position(fromLine, fromColumn),
                new Region.Position(toLine, toColumn)
        );
    }

    public RegionOf<String> region(KarinaParser.IdContext n) {
        var content = escapeID(n);
        var region = toRegion(n);
        return RegionOf.region(region, content);
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



}
