package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.karina.lang.compiler.api.FileTreeNode;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.stages.KarinaStage;
import org.karina.lang.compiler.stages.parser.visitor.KarinaVisitor;
import org.karina.lang.compiler.utils.RegionConverter;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;

import java.util.ArrayList;
import java.util.List;


/**
 * Helper for parsing source code into a {@link KTree.KUnit}.
 */
public class KarinaTextParser {

    public static KTree.KPackage parseFiles(FileTreeNode fileTree, ErrorCollector collector) {

        List<KTree.KUnit> units = new ArrayList<>();
        for (var files : fileTree.leafs()) {
            collector.collect(() -> {
                units.add(KarinaStage.parseUnit(
                        files.text(),
                        files.name(),
                        files.path()
                ));
            });
        }
        var subPackages = fileTree.children().stream().map(ref -> parseFiles(ref, collector)).toList();
        return new KTree.KPackage(fileTree.path(), fileTree.name(), subPackages, units);

    }
    /**
     * Responsible for parsing Karina source code into an abstract syntax tree via ANTLR and
     * converting the tree into a Karina parse tree.
     * @see KarinaParser
     * @see KarinaVisitor
     * @see KTree
     */
    public static KTree.KUnit generateParseTree(TextSource source, String simpleName, ObjectPath path) throws Log.KarinaException {

        var errorListener = new KarinaErrorListener(source, true);
        var unitParser = getParserForUnit(errorListener, source);

        var regionConverter = new RegionConverter(source, unitParser.tokenStream);
        var visitor = new KarinaVisitor(regionConverter, path, simpleName);
        return visitor.visitUnit(unitParser.parser.unit());

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

    public record ReadyUnitParser(KarinaParser parser, KarinaLexer lexer, CommonTokenStream tokenStream) {}

}
