package org.karina.lang.compiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.parser.KarinaErrorListener;
import org.karina.lang.compiler.parser.KarinaVisitor;
import org.karina.lang.compiler.parser.RegionConverter;
import org.karina.lang.compiler.parser.gen.KarinaLexer;
import org.karina.lang.compiler.parser.gen.KarinaParser;


/**
 * Helper for parsing source code into a {@link KTree.KUnit}.
 */
public class KarinaCParser {

    /**
     * Responsible for parsing Karina source code into an abstract syntax tree via ANTLR and
     * converting the tree into a Karina parse tree.
     * @see KarinaParser
     * @see KarinaVisitor
     * @see KTree
     */
    public static KTree.KUnit generateParseTree(TextSource source, String simpleName, ObjectPath path)
            throws Log.KarinaException {

        var errorListener = new KarinaErrorListener(source);
        var unitParser = getParserForUnit(errorListener, source);
        try {
            var regionConverter = new RegionConverter(source, unitParser.tokenStream);
            var visitor = new KarinaVisitor(regionConverter, path, simpleName);
            return visitor.visitUnit(unitParser.parser.unit());
        } catch(Log.KarinaException e) {
            if (errorListener.hadError()) {
                try {
                    Token t = unitParser.lexer().nextToken();
                    while (t.getType() != Token.EOF) {
                        t = unitParser.lexer().nextToken();
                    }
                } catch (Log.KarinaException ignored) {

                }
            }
            throw new Log.KarinaException();
        }

    }

    /**
     * Returns a parser for a unit of Karina source code.
     */
    public static ReadyUnitParser getParserForUnit(KarinaErrorListener errorListener, TextSource source) {

        var content = combineToString(source);
        var inputStream = CharStreams.fromString(content);
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        var karinaParser = new KarinaParser(tokenStream);
        karinaParser.removeErrorListeners();
        karinaParser.addErrorListener(errorListener);
        return new ReadyUnitParser(karinaParser, karinaLexer, tokenStream);

    }

    private static String combineToString(TextSource source) {

        var bobTheBuilder = new StringBuilder();
        for (var line : source.lines()) {
            bobTheBuilder.append(line).append("\n");
        }
        return bobTheBuilder.toString();

    }

    public record ReadyUnitParser(KarinaParser parser,KarinaLexer lexer ,CommonTokenStream tokenStream) {}

}