package org.karina.lang.compiler.parser_api;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;


@RequiredArgsConstructor
public enum Token {
    EXPR("expr"),
    TYPE("type"),
    FN("fn"),
    IS("is"),
    IN("in"),
    AS("as"),
    NULL("null"),
    IMPORT("import"),
    EXTENDS("extends"),
    EXTEND("extend"),
    MATCH("match"),
    OVERRIDE("override"),
    NATIVE("native"),
    TRUE("true"),
    FALSE("false"),
    VIRTUAL("virtual"),
    BREAK("break"),
    RETURN("return"),
    YIELD("yield"),
    STRUCT("struct"),
    STATIC("static"),
    THROW("throw"),
    TRAIT("trait"),
    IMPL("impl"),
    ENUM("enum"),
    CLASS("class"),
    LET("let"),
    IF("if"),
    CONST("const"),
    ELSE("else"),
    WHILE("while"),
    FOR("for"),
    SUPER("super"),
    WHERE("where"),
    INTERFACE("interface"),
    SELF("self"),
    INT("int"),
    MUT("mut"),
    LONG("long"),
    BYTE("byte"),
    CHAR("char"),
    DOUBLE("double"),
    SHORT("short"),
    STRING("string"),
    FLOAT("float"),
    BOOL("bool"),
    VOID("void"),
    JSON("json"),
    CONTINUE("continue"),

    ARROW_RIGHT("->"),
    ARROW_RIGHT_BOLD("=>"),
    GREATER_EQUALS(">="),
    SMALLER_EQUALS("<="),
    EQUALS("=="),
    STRICT_EQUALS("==="),
    STRICT_NOT_EQUALS("!=="),
    NOT_EQUALS("!="),
    AND_AND("&&"),
    OR_OR("||"),


    CHAR_PLUS("+"),
    CHAR_MINUS("-"),
    CHAR_STAR("*"),
    CHAR_R_SLASH("/"),
    CHAR_PERCENT("%"),
    CHAR_OR("|"),
    CHAR_XOR("^"),
    CHAR_TILDE("~"),
    CHAR_GREATER(">"),
    CHAR_SMALLER("<"),
    CHAR_EXCLAMATION("!"),
    CHAR_COLON(":"),
    CHAR_COLON_COLON("::"),
    CHAR_EQUAL("="),
    CHAR_L_PAREN("("),
    CHAR_R_PAREN(")"),
    CHAR_L_BRACE("{"),
    CHAR_R_BRACE("}"),
    CHAR_L_BRACKET("["),
    CHAR_R_BRACKET("]"),
    CHAR_AT("@"),
    CHAR_COMMA(","),
    CHAR_UNDER("_"),
    CHAR_AND("&"),
    CHAR_DOT("."),
    CHAR_QUESTION("?"),
    CHAR_SEMICOLON(";"),
    CHAR_ESCAPE("\\\\"),


    ID("id"),

    NUMBER("number"),
    STRING_LITERAL("string"),
    CHAR_LITERAL("char"),

    EOF("eof"),

    INVALID_CHARACTER(""),
    INVALID_COMMENT(""),
    INVALID_STRING_LITERAL(""),
    INVALID_CHAR_LITERAL(""),

    ;

    private final String name;


    public static @Nullable Token tokenOfKeyword(String keyword) {
        try {
            return Token.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static @Nullable Token getByLiteral(String literalName) {
        var token = tokenOfKeyword(literalName);

        if (token != null) {
            return token;
        }

        Token[] values = Token.values();
        for (var value : values) {
            if (value.name.equals(literalName)) {
                return value;
            }
        }
        return null;

    }
}
