package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.stages.parser.error.KarinaErrorListener;
import org.karina.lang.compiler.stages.parser.error.KarinaRecoveringStrategy;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.KarinaExprVisitor;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;
import org.karina.lang.compiler.utils.ObjectPath;

public class TextUnitParser {
    private final KarinaParser karinaParser;
    private final KarinaUnitVisitor visitor;

    public TextUnitParser(TextSource source, String name, ObjectPath path) {
        var errorListener = new KarinaErrorListener(source, true);
        var content = combineToString(source);
        var inputStream = CharStreams.fromString(content);
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        this.karinaParser = new KarinaParser(tokenStream);
        this.karinaParser.setErrorHandler(new KarinaRecoveringStrategy(source));
        this.karinaParser.removeErrorListeners();
        this.karinaParser.addErrorListener(errorListener);

        KarinaExprVisitor.PARSER = this.karinaParser;

        var regionConverter = new TextContext(source, tokenStream);
        this.visitor = new KarinaUnitVisitor(regionConverter, name, path);
    }

    public void parse(JKModelBuilder builder) {
        this.visitor.visit(this.karinaParser.unit(), builder);
    }

    private String combineToString(TextSource source) {
        var bobTheBuilder = new StringBuilder();
        for (var line : source.lines()) {
            bobTheBuilder.append(line).append("\n");
        }
        return bobTheBuilder.toString();
    }

}
