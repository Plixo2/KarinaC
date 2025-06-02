package org.karina.lang.compiler.parser_api;

import lombok.SneakyThrows;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.File;
import java.util.*;

public class Tokenizer {
    int keywordMinLength;
    int keywordMaxLength;
    KeywordRecord[][] packed;

    public Tokenizer(String[] keywords) {
        if (keywords.length == 0) {
            this.keywordMaxLength = 0;
            this.keywordMinLength = 0;
            this.packed = new KeywordRecord[0][];
            return;
        }

        this.keywordMaxLength = 0;
        this.keywordMinLength = Integer.MAX_VALUE;
        for (var key : keywords) {
            this.keywordMaxLength = Math.max(this.keywordMaxLength, key.length());
            this.keywordMinLength = Math.min(this.keywordMinLength, key.length());
        }

        var arrayOfArraySize = this.keywordMaxLength - this.keywordMinLength + 1;
        int[] counts = new int[arrayOfArraySize];
        for (var key : keywords) {
            counts[key.length() - this.keywordMinLength]++;
        }

        this.packed = new KeywordRecord[arrayOfArraySize][];
        for (int i = 0; i < counts.length; i++) {
            this.packed[i] = new KeywordRecord[counts[i]];
        }
        var indices = new int[arrayOfArraySize];
        for (var key : keywords) {
            var index = key.length() - this.keywordMinLength;
            var token = Token.tokenOfKeyword(key);
            if (token == null) {
                throw new IllegalArgumentException("Unknown keyword: " + key);
            }
            this.packed[index][indices[index]++] = new KeywordRecord(key.toCharArray(), token);
        }

    }



    @SneakyThrows
    public static void main(String[] args) {

        var string = FileLoader.loadUTF8FiletoString(new File("resources/src_local/main.krna"));

        var keys = new String[]{
                "expr",
                "type",
                "fn",
                "is",
                "in",
                "as",
                "null",
                "import",
                "extends",
                "extend",
                "match",
                "override",
                "native",
                "true",
                "false",
                "virtual",
                "break",
                "return",
                "yield",
                "struct",
                "static",
                "throw",
                "trait",
                "impl",
                "enum",
                "class",
                "let",
                "if",
                "const",
                "else",
                "while",
                "for",
                "super",
                "where",
                "interface",
                "self",
                "int",
                "mut",
                "long",
                "byte",
                "char",
                "double",
                "short",
                "string",
                "float",
                "bool",
                "void",
                "json",
                "continue"
        };

        var tokenizerF = new Tokenizer(keys);

//       string = "1000 1e 1.0e10 40.0 0x444_333 0b1001 -10";

        for (var i = 0; i < 100000; i++) {
            var _ = tokenizerF.tokenize(string);
        }

        var repeatCount = 2000;
        var runs = 100;

        /*
        var repeatCount = 200;
        var runs = 200;
        7015781

        var repeatCount = 2000;
        var runs = 100;
        5304262

        var repeatCount = 10;
        var runs = 100;
        12225000
        */

        var repeated = string.repeat(repeatCount);

        var records = tokenizerF.tokenize(string);
        for (var i = 0; i < records.size(); i+=1) {
            var token = records.getToken(i);
            var column = records.getIndex(i);
            var length = records.getLength(i);
            var region = string.substring(column, column + length);
            System.out.println(token + ": " + region);
        }

        var current = System.currentTimeMillis();
        for (var i = 0; i < runs; i++) {
            var _ = tokenizerF.tokenize(repeated);
        }
        var end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - current) + "ms");
        var perRun = (end - current)/((double)runs);
        System.out.println("Time taken per run: " + perRun + "ms");
        var perLineMS = ((repeated.lines().count()) / perRun);
        var perLine = perLineMS * 1000d;
        System.out.println("Lines per second: " + ((int)perLine) + " lines/s");

        var ms_perMillion = 1_000_000 / perLineMS;
        System.out.println("MS per million lines: " + ms_perMillion + " ms");


    }

    public TokenList tokenize(String source) {
        var list = new TokenList();

        var index = 0;
        var chars = source.toCharArray();
        var byteLength = chars.length;


        loop: while (index < byteLength) {
            var c = chars[index];

            if (Character.isWhitespace(c)) {
                while (index < byteLength && (Character.isWhitespace(chars[index]))) {
                    index++;
                }
            } else if (Character.isJavaIdentifierStart(c)) {

                var length = 1;
                while (index + length < byteLength) {
                    var current = chars[index + length];
                    if (!Character.isJavaIdentifierPart(current)) {
                        break;
                    }
                    length++;
                }
                if (length >= this.keywordMinLength && length <= this.keywordMaxLength) {
                    var offset = length - this.keywordMinLength;
                    var keywords = this.packed[offset];

                    inner:
                    for (var keyword : keywords) {
                        for (var i = 0; i < length; i++) {
                            var inSource = chars[index + i];
                            var inKeyword = keyword.chars[i];
                            if (inSource != inKeyword) {
                                continue inner;
                            }
                        }
                        index += length;
                        list.add(keyword.token, index - length, length);
                        continue loop;
                    }
                }
                list.add(Token.ID, index, length);
                index += length;
            } else if (c >= '0' && c <= '9') {
                //Numbers
                var next = parseNumber(chars, index, byteLength);
                list.add(Token.NUMBER, index, next - index);
                index = next;
            } else if (c == '/') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_R_SLASH, index, 1);
                    index++;
                    continue;
                }
                var next = chars[index + 1];
                if (next == '/') {
                    // single line comment
                    index += 2;
                    while (index < byteLength && chars[index] != '\n') {
                        index++;
                    }
                } else if (next == '*') {
                    // multi line comment
                    var start = index;
                    index += 2;
                    while (index + 1 < byteLength && !(chars[index] == '*' && chars[index + 1] == '/')) {
                        index++;
                    }
                    if (index + 1 >= byteLength) {
                        list.add(Token.INVALID_COMMENT, start, (byteLength - 1) - start);
                    }
                    index += 2; // skip */
                } else {
                    list.add(Token.CHAR_R_SLASH, index, 1);
                    index++;
                }

            } else if (c == '-') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_MINUS, index, 1);
                    index++;
                    continue;
                }
                var next = chars[index + 1];
                if (next == '>') {
                    // arrow
                    list.add(Token.ARROW_RIGHT, index, 2);
                    index += 2;
                } else if (next >= '0' && next <= '9') {
                    var nextIndex = parseUnsigned(chars, index + 1, byteLength);
                    // bold arrow
                    list.add(Token.NUMBER, index, nextIndex - index);
                    index = nextIndex;
                } else {
                    list.add(Token.CHAR_MINUS, index, 1);
                    index++;
                }
            } else if (c == '>') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_GREATER, index, 1);
                    index++;
                    continue;
                }
                if (chars[index + 1] == '=') {
                    // greater equals
                    list.add(Token.GREATER_EQUALS, index, 2);
                    index += 2;
                } else {
                    list.add(Token.CHAR_GREATER, index, 1);
                    index++;
                }

            } else if (c == '<') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_SMALLER, index, 1);
                    index++;
                    continue;
                }
                if (chars[index + 1] == '=') {
                    // greater equals
                    list.add(Token.SMALLER_EQUALS, index, 2);
                    index += 2;
                } else {
                    list.add(Token.CHAR_SMALLER, index, 1);
                    index++;
                }

            } else if (c == '=') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_EQUAL, index, 1);
                    index++;
                    continue;
                }
                var next = chars[index + 1];
                if (next == '=') {
                    if (index + 2 < byteLength && chars[index + 2] == '=') {
                        // strict equals
                        list.add(Token.STRICT_EQUALS, index, 3);
                        index += 3;
                    } else {
                        // equals
                        list.add(Token.EQUALS, index, 2);
                        index += 2;
                    }
                } else {
                    list.add(Token.CHAR_EQUAL, index, 1);
                    index++;
                }

            } else if (c == '!') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_EXCLAMATION, index, 1);
                    index++;
                    continue;
                }
                var next = chars[index + 1];
                if (next == '=') {
                    if (index + 2 < byteLength && chars[index + 2] == '=') {
                        // strict not equals
                        list.add(Token.STRICT_NOT_EQUALS, index, 3);
                        index += 3;
                    } else {
                        // not equals
                        list.add(Token.NOT_EQUALS, index, 2);
                        index += 2;
                    }
                } else {
                    list.add(Token.CHAR_EXCLAMATION, index, 1);
                    index++;
                }

            } else if (c == '&') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_AND, index, 1);
                    index++;
                    continue;
                }
                if (chars[index + 1] == '&') {
                    // and and
                    list.add(Token.AND_AND, index, 2);
                    index += 2;
                } else {
                    list.add(Token.CHAR_AND, index, 1);
                    index++;
                }

            } else if (c == '|') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_OR, index, 1);
                    index++;
                    continue;
                }
                if (chars[index + 1] == '|') {
                    // and and
                    list.add(Token.OR_OR, index, 2);
                    index += 2;
                } else {
                    list.add(Token.CHAR_OR, index, 1);
                    index++;
                }
            } else if (c == ':') {
                var hasNext = index + 1 < byteLength;
                if (!hasNext) {
                    list.add(Token.CHAR_COLON, index, 1);
                    index++;
                    continue;
                }
                if (chars[index + 1] == ':') {
                    // and and
                    list.add(Token.CHAR_COLON_COLON, index, 2);
                    index += 2;
                } else {
                    list.add(Token.CHAR_COLON, index, 1);
                    index++;
                }

            } else if (c == '"') {
                index = string(chars, index, byteLength, list);
            } else if (c == '\'') {
                index = interpolation(chars, index, byteLength, list);
            }else {
                var token = switch (c) {
                    case '+' -> Token.CHAR_PLUS;
                    case '*' -> Token.CHAR_STAR;
                    case '%' -> Token.CHAR_PERCENT;
                    case '^' -> Token.CHAR_XOR;
                    case '~' -> Token.CHAR_TILDE;
                    case '(' -> Token.CHAR_L_PAREN;
                    case ')' -> Token.CHAR_R_PAREN;
                    case '{' -> Token.CHAR_L_BRACE;
                    case '}' -> Token.CHAR_R_BRACE;
                    case '[' -> Token.CHAR_L_BRACKET;
                    case ']' -> Token.CHAR_R_BRACKET;
                    case '@' -> Token.CHAR_AT;
                    case ',' -> Token.CHAR_COMMA;
                    case '_' -> Token.CHAR_UNDER;
                    case '.' -> Token.CHAR_DOT;
                    case '?' -> Token.CHAR_QUESTION;
                    case ';' -> Token.CHAR_SEMICOLON;
                    case '\\' -> Token.CHAR_ESCAPE;
                    default -> null;
                };
                if (token == null) {
                    var length = Character.isHighSurrogate(c) ? 2 : 1;
                    list.add(Token.INVALID_CHARACTER, index, length);
                    index += length;
                } else {
                    list.add(token, index, 1);
                    index++;
                }
            }

        }

        return list;
    }

    private static int string(char[] chars, int index, int end, TokenList list) {
        index += 1;

        var start = index - 1;
        while (index < end) {
            var c = chars[index];
            if (c == '"') {
                index++;
                list.add(Token.STRING_LITERAL, start, index - start);
                return index;
            } else if (c == '\\') {
                index++;
            }
            index++;
        }
        list.add(Token.INVALID_STRING_LITERAL, start, (end - 1) - start);
        return end;

    }

    private static int interpolation(char[] chars, int index, int end, TokenList list) {
        index += 1;

        var start = index - 1;
        while (index < end) {
            var c = chars[index];
            if (c == '\'') {
                index++;
                list.add(Token.CHAR_LITERAL, start, index - start);
                return index;
            } else if (c == '\\') {
                index++;
            }
            index++;
        }
        list.add(Token.INVALID_CHAR_LITERAL, start, (end - 1) - start);
        return end;

    }

    private static int parseNumber(char[] chars, int index, int end) {
        var first = chars[index];
        if (index + 1 >= end) {
            return index + 1;
        }
        var next = chars[index + 1];
        if (first == '0') {
            if (next == 'x') {
                return parseHex(chars, index + 2, end);
            } else if (next == 'b') {
                return parseBinary(chars, index + 2, end);
            }
        }
        return parseUnsigned(chars, index, end);

    }

    private static int parseUnsigned(char[] chars, int index, int end) {
        index = digits(chars, index, end);
        if (index >= end) {
            return index;
        }
        if (chars[index] == '.') {
            index++;
        }
        index = digits(chars, index, end);
        index = exponent(chars, index, end);
        return index;
    }

    private static int exponent(char[] chars, int index, int end) {
        if (index >= end) return index;

        var c = chars[index];
        if (c != 'e' && c != 'E') {
            return index;
        }
        var next = index + 1;
        if (next >= end) return index;

        c = chars[next];
        if (c == '+' || c == '-') {
            next++;
        }
        if (next >= end) return index;

        if ((c < '0' || c > '9') && c != '_') {
            return index;
        }
        return digits(chars, next, end);

    }

    private static int digits(char[] chars, int index, int end) {
        if (index >= end) {
            return index;
        }
        var c = chars[index];
        while (
                c >= '0' && c <= '9'
             || c == '_'
        ) {
            index++; if (index >= end) return index;
            c = chars[index];
        }
        return index;
    }

    private static int parseBinary(char[] chars, int index, int end) {
        if (index >= end) {
            return index;
        }
        var c = chars[index];
        while (
                c == '0' || c == '1' || c == '_'
        ) {
            index++; if (index >= end) return index;
            c = chars[index];
        }
        return index;
    }

    private static int parseHex(char[] chars, int index, int end) {
        if (index >= end) {
            return index;
        }
        var c = chars[index];
        while (
               c >= '0' && c <= '9'
            || c >= 'a' && c <= 'f'
            || c >= 'A' && c <= 'F'
            || c == '_'
        ) {
            index++; if (index >= end) return index;
            c = chars[index];
        }
        return index;
    }

    private record KeywordRecord(
            char[] chars,
            Token token
    ) { }

    public record TokenRecord(
            Token token,
            int column,
            int length
    ) { }

    public static class TokenList {
        private int[] data;
        private int size;
        private int capacity;

        public TokenList() {
            this.data = new int[32];
            this.size = 0;
            this.capacity = 32;
        }

        public void add(Token token, int index, int length) {
            if ((this.size + 3) >= this.capacity) {
                if (this.capacity == 0) {
                    this.capacity = 8;
                } else {
                    this.capacity *= 2;
                }
                this.data = Arrays.copyOf(this.data, this.capacity);
            }
            this.data[this.size++] = token.ordinal();
            this.data[this.size++] = index;
            this.data[this.size++] = length;
        }

        public Token getToken(int index) {
            if (index < 0 || index >= this.size / 3) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (this.size / 3));
            }
            return Token.values()[this.data[index * 3]];
        }

        public int getIndex(int index) {
            if (index < 0 || index >= this.size / 3) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (this.size / 3));
            }
            return this.data[index * 3 + 1];
        }
        public int getLength(int index) {
            if (index < 0 || index >= this.size / 3) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (this.size / 3));
            }
            return this.data[index * 3 + 2];
        }
        public String getText(String content, int index) {
            if (index < 0 || index >= this.size / 3) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (this.size / 3));
            }
            var start = this.data[index * 3 + 1];
            var length = this.data[index * 3 + 2];
            return content.substring(start, start + length);
        }

        public int size() {
            return this.size / 3;
        }

        public boolean isEmpty() {
            return this.size == 0;
        }

    }

   /* private static void test(String[] keys, Consumer<String> consumer) {
        var startTotal = System.currentTimeMillis();
        for (var runs = 0; runs < 2; runs++) {
            System.out.print(": ");
            for (var i = 0f; i <= 1.01; i += 0.1f) {
                var start = System.currentTimeMillis();
                var builder = new StringBuilder();
                for (var j = 0; j < 4000000; j++) {
                    String word;
                    if (Math.random() < i) {
                        word = getRandomWord(1 + (int) (Math.random() * 13));
                    } else {
                        word = keys[(int) (Math.random() * keys.length)];
                    }
                    builder.append(word).append(" ".repeat((int) (Math.random() * 10)));
                }
                var toTest = builder.toString();
                consumer.accept(toTest);
                var end = System.currentTimeMillis();
                System.out.print((end - start) + ",");
            }
            System.out.println(" ms ");
        }
        var endTotal = System.currentTimeMillis();
        System.out.println("Total time: " + (endTotal - startTotal) + "ms");
    }

    private static String getRandomWord(int length) {
        var random = new Random();
        var sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
            if (Math.random() < 0.4) {
                sb.append((char) (random.nextInt(26) + 'A'));
            }
            if (Math.random() < 0.1) {
                sb.append((char) (random.nextInt(10) + '0'));
            }
        }
        return sb.toString();
    }*/
}
