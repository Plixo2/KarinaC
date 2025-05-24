package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.parser.error.KarinaErrorListener;
import org.karina.lang.compiler.stages.parser.error.KarinaRecoveringStrategy;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;
import org.karina.lang.compiler.utils.ObjectPath;

public class TextUnitParser {
    private final KarinaParser karinaParser;
    private final KarinaUnitVisitor visitor;

    public TextUnitParser(TextSource source, String name, ObjectPath path) {
        var errorListener = new KarinaErrorListener(source, true);
        var inputStream = CharStreams.fromString(source.content());
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        this.karinaParser = new KarinaParser(tokenStream);
        this.karinaParser.setErrorHandler(new KarinaRecoveringStrategy(source));
        this.karinaParser.removeErrorListeners();
        this.karinaParser.addErrorListener(errorListener);


        var regionConverter = new RegionContext(source, tokenStream);
        this.visitor = new KarinaUnitVisitor(regionConverter, name, path);
    }

    public void visit(ModelBuilder builder) {
        this.visitor.visit(this.karinaParser.unit(), builder);
    }

}
