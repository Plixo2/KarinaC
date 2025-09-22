package org.karina.lang.compiler.test;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.karina.lang.compiler.test.errors.TestErrorListener;
import org.karina.lang.compiler.test.gen.TestLexer;
import org.karina.lang.compiler.test.gen.TestParser;

public class Main {
    public static void main(String[] args) {

        var content = """
                {
                let a: int = 10
                let b = 20
                a + b
                }
                """;
        var inputStream = CharStreams.fromString(content);
        var lexer = new TestLexer(inputStream);
        var parser = new TestParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        lexer.addErrorListener(new TestErrorListener("Lexer"));
        parser.removeErrorListeners();
        parser.addErrorListener(new TestErrorListener("Parser"));

        printTree(parser.entry(), "", "");
    }


    public static void printTree(ParseTree tree, String indent, String childIndent) {
        String nodeText = tree.getClass().getSimpleName().replaceAll("Context$", "");
        System.out.println(indent + nodeText + " -> \"" + tree.getText() + "\"");

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            boolean last = (i == tree.getChildCount() - 1);
            printTree(child, childIndent + (last ? "\\-- " : "|-- "),
                    childIndent + (last ? "    " : "|   "));
        }
    }
}
