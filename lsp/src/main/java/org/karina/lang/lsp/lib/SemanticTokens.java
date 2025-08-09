package org.karina.lang.lsp.lib;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public final class SemanticTokens {
    /// Calculates deltas for semantic tokens.
    /// @param tokens the tokens in multiple of 4, where each group of 4 represents: line, character, length, type
    /// @return a list of integers representing the deltas, where each group of 5 represents: deltaLine, deltaCharacter, length, type, modifier
    private static List<Integer> getDeltaTokens(IntList tokens) {
        assert tokens.size() % 4 == 0;
        var entries = tokens.size() / 4;
        var result = new ArrayList<Integer>(entries * 5);
        var groups = groupBy(tokens, 4);
        groups.sort((p1, p2) -> {
            var lineA = p1[0];
            var lineB = p2[0];
            if (lineA != lineB) {
                return Integer.compare(lineA, lineB);
            }
            var columnA = p1[1];
            var columnB = p2[1];
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

    public static List<int[]> groupBy(IntList inputList, int groupSize) {
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
