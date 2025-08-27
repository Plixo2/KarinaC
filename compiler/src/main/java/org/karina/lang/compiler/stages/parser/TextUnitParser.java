package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.stages.parser.error.KarinaErrorListener;
import org.karina.lang.compiler.stages.parser.error.KarinaRecoveringStrategy;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;
import org.karina.lang.compiler.utils.ObjectPath;

public class TextUnitParser {


    public static void parseItems(Context c, TextSource source, String name, ObjectPath path, ModelBuilder builder) {
        var errorListener = new KarinaErrorListener(c, source);
        var inputStream = CharStreams.fromString(source.content());
        var karinaLexer = new KarinaLexer(inputStream);
        karinaLexer.removeErrorListeners();
        karinaLexer.addErrorListener(errorListener);
        var tokenStream = new CommonTokenStream(karinaLexer);
        var karinaParser = new KarinaParser(tokenStream);
        karinaParser.setErrorHandler(new KarinaRecoveringStrategy(c, source));
        karinaParser.removeErrorListeners();
        karinaParser.addErrorListener(errorListener);

        var regionConverter = new RegionContext(source);
        var visitor = new KarinaUnitVisitor(c, regionConverter, name, path);
        visitor.visit(karinaParser.unit(), builder);
    }

}
