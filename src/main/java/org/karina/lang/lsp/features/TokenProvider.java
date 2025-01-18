package org.karina.lang.lsp.features;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.stages.parser.TextProcessor;
import org.karina.lang.compiler.stages.parser.KarinaErrorListener;
import org.karina.lang.lsp.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a text source into an AST and then into a list of semantic tokens.
 * This list is then send as semantic tokens to the client.
 */
public class TokenProvider {

    public @Nullable List<Integer> getTokens(TextSource content) {
        var visitor = new SemanticTokenVisitor();
        var tokens = new ArrayList<Integer>();
//        var ignored = ErrorHandler.tryInternal(() -> {
//            var errorListener = new KarinaErrorListener(content, false);
//            var unitParser = TextProcessor.getParserForUnit(errorListener, content);
//            visitor.visit(unitParser.parser().unit());
//            tokens.addAll(getDeltaTokens(visitor.getTokens()));
//        });
        return tokens;

    }

    /**
     * Converts absolute token positions into relative positions.
     */
    private static List<Integer> getDeltaTokens(List<Integer> tokens) {
        assert tokens.size() % 4 == 0;
        var entries = tokens.size() / 4;
        var result = new ArrayList<Integer>(entries * 5);
        var groups = groupBy(tokens, 4);
        groups.sort((p1, p2) -> {
            var lineA = p1[0];
            var lineB = p2[0];
            var columnA = p1[1];
            var columnB = p2[1];
            if (lineA != lineB) {
                return Integer.compare(lineA, lineB);
            }
            return Integer.compare(columnA, columnB);
        });


        var lastLine = 0;
        var lastCharacter = 0;

        for (var group : groups) {
            var line = group[0];
            var character = group[1];
            var length = group[2];
            var type = group[3];

            var deltaLine = line - lastLine;
            var deltaCharacter = character - lastCharacter;
            if (deltaLine != 0) {
                deltaCharacter = character;
            }

            result.add(deltaLine);
            result.add(deltaCharacter);
            result.add(length);
            result.add(type);
            //modifier is always 0
            if (type == SemanticTokenType.VARIABLE.ordinal()) {
                var decl = 1 << SemanticTokenModifier.DECLARATION.ordinal();
                var def = 1 << SemanticTokenModifier.DEFINITION.ordinal();
                var read = 1 << SemanticTokenModifier.READONLY.ordinal();

                result.add(decl | def | read);
            } else {
                result.add(0);
            }

            lastLine = line;
            lastCharacter = character;
        }

        return result;
    }

    public static List<int[]> groupBy(List<Integer> inputList, int groupSize) {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i += groupSize) {
            var group = new int[groupSize];
            for (int j = 0; j < groupSize; j++) {
                group[j] = inputList.get(i + j);
            }
            result.add(group);
        }
        return result;
    }


    @RequiredArgsConstructor
    public enum SemanticTokenType {
        NAMESPACE("namespace"),
        TYPE("type"),
        STRUCT("struct"),
        CLASS("class"),
        INTERFACE("interface"),
        ENUM("enum"),
        FUNCTION("function"),
        METHOD("method"),
        MACRO("macro"),
        VARIABLE("variable"),
        PARAMETER("parameter"),
        PROPERTY("property"),
        ENUM_MEMBER("enumMember"),
        EVENT("event"),


        ;
        final String value;

        public static List<String> names() {
            var names = new ArrayList<String>();
            for (var type : values()) {
                names.add(type.value);
            }
            return names;
        }
    }

    @RequiredArgsConstructor
    public enum SemanticTokenModifier {
        DECLARATION("declaration"),
        DEFINITION("definition"),
        READONLY("readonly"),
        STATIC("static"),
        DEPRECATED("deprecated"),
        ABSTRACT("abstract"),
        ASYNC("async"),
        MODIFICATION("modification"),
        DOCUMENTATION("documentation"),
        DEFAULT_LIBRARY("defaultLibrary"),
        ;

        final String value;

        public static List<String> names() {
            var names = new ArrayList<String>();
            for (var modifier : values()) {
                names.add(modifier.value);
            }
            return names;
        }
    }
}
