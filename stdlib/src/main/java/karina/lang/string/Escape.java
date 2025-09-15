/**
 * Includes code from Apache Commons Text
 * (https://commons.apache.org/proper/commons-text/)
 * Copyright (c) The Apache Software Foundation
 * Licensed under the Apache License 2.0
 */

package karina.lang.string;

import java.util.Map;
import java.util.stream.Collectors;

public class Escape {



    private static final StringTranslator ESCAPE_JAVA;
    private static final StringTranslator ESCAPE_JSON;

    private static final StringTranslator UNESCAPE_JAVA;
    private static final StringTranslator UNESCAPE_JSON;

    static {
        var JAVA_CTRL_CHARS_ESCAPE = Map.<CharSequence, CharSequence>of(
                "\b", "\\b",
                "\n", "\\n",
                "\t", "\\t",
                "\f", "\\f",
                "\r", "\\r"
        );

        var escapeJavaMap = Map.<CharSequence, CharSequence>of(
                "\"", "\\\"",
                "\\", "\\\\"
        );

        ESCAPE_JAVA = new StringTranslator.AggregateTranslator(
                new StringTranslator.LookupTranslator(escapeJavaMap),
                new StringTranslator.LookupTranslator(JAVA_CTRL_CHARS_ESCAPE),
                new StringTranslator.JavaUnicodeTranslatorEscape(32, 0x7e, false)
        );

        var escapeJsonMap = Map.<CharSequence, CharSequence>of(
                "\"", "\\\"",
                "\\", "\\\\",
                "/", "\\/"
        );

        ESCAPE_JSON = new StringTranslator.AggregateTranslator(
                new StringTranslator.LookupTranslator(escapeJsonMap),
                new StringTranslator.LookupTranslator(JAVA_CTRL_CHARS_ESCAPE),
                new StringTranslator.JavaUnicodeTranslatorEscape(32, 0x7e, false)
        );

        var unescapeJavaMap = Map.<CharSequence, CharSequence>of(
                "\\\\", "\\",
                "\\\"", "\"",
                "\\'", "'",
                "\\", ""
        );
        UNESCAPE_JAVA = new StringTranslator.AggregateTranslator(
                new StringTranslator.OctalTranslatorUnescape(),     // .between('\1', '\377'),
                new StringTranslator.UnicodeTranslatorUnescape(),
                new StringTranslator.LookupTranslator(invert(JAVA_CTRL_CHARS_ESCAPE)),
                new StringTranslator.LookupTranslator(unescapeJavaMap)
        );


        UNESCAPE_JSON = UNESCAPE_JAVA;
    }

    private static Map<CharSequence, CharSequence> invert(final Map<CharSequence, CharSequence> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    /**
     * Escapes the characters in a {@code String} using Java String rules.
     *
     * <p>Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     *
     * <p>So a tab becomes the characters {@code '\\'} and
     * {@code 't'}.</p>
     *
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote and forward-slash (/) are escaped.</p>
     *
     * <p>Example:</p>
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     *
     * @param input  String to escape values in, may be null
     * @return String with escaped values, {@code null} if null string input
     */
    public static String escapeJava(final String input) {
        return ESCAPE_JAVA.translate(input);
    }

    /**
     * Unescapes any Java literals found in the {@code String}.
     * For example, it will turn a sequence of {@code '\'} and
     * {@code 'n'} into a newline character, unless the {@code '\'}
     * is preceded by another {@code '\'}.
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     */
    public static String unescapeJava(final String input) {
        return UNESCAPE_JAVA.translate(input);
    }

    /**
     * Escapes the characters in a {@code String} using Json String rules.
     *
     * <p>Escapes any values it finds into their Json String form.
     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     *
     * <p>So a tab becomes the characters {@code '\\'} and
     * {@code 't'}.</p>
     *
     * <p>The only difference between Java strings and Json strings
     * is that in Json, forward-slash (/) is escaped.</p>
     *
     * <p>See <a href="http://www.ietf.org/rfc/rfc4627.txt">http://www.ietf.org/rfc/rfc4627.txt</a> for further details.</p>
     *
     * <p>Example:</p>
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn't say, \"Stop!\"
     * </pre>
     *
     * @param input  String to escape values in, may be null
     * @return String with escaped values, {@code null} if null string input
     */
    public static String escapeJson(final String input) {
        return ESCAPE_JSON.translate(input);
    }

    /**
     * Unescapes any Json literals found in the {@code String}.
     *
     * <p>For example, it will turn a sequence of {@code '\'} and {@code 'n'}
     * into a newline character, unless the {@code '\'} is preceded by another
     * {@code '\'}.</p>
     *
     * @see #unescapeJava(String)
     * @param input  the {@code String} to unescape, may be null
     * @return A new unescaped {@code String}, {@code null} if null string input
     */
    public static String unescapeJson(final String input) {
        return UNESCAPE_JSON.translate(input);
    }


}
