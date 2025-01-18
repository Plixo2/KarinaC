package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.KarinaVisitor;
import org.karina.lang.compiler.utils.ObjectPath;

public class TextUnitParser {
    private final KarinaParser karinaParser;
    private final KarinaVisitor visitor;

    public TextUnitParser(TextSource source, String simpleName, ObjectPath path) {

        var errorListener = new KarinaErrorListener(source, true);
        var content = combineToString(source);
        var inputStream = CharStreams.fromString(content);
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        this.karinaParser = new KarinaParser(tokenStream);
        this.karinaParser.removeErrorListeners();
        this.karinaParser.addErrorListener(errorListener);

        var regionConverter = new TextContext(source, tokenStream);
        this.visitor = new KarinaVisitor(regionConverter, path, simpleName);
    }

    public KTree.KUnit parse() {
        return this.visitor.visitUnit(this.karinaParser.unit());
    }

    private String combineToString(TextSource source) {
        var bobTheBuilder = new StringBuilder();
        for (var line : source.lines()) {
            bobTheBuilder.append(line).append("\n");
        }
        return bobTheBuilder.toString();
    }

}
