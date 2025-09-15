/**
 * Includes code from Apache Commons Text
 * (https://commons.apache.org/proper/commons-text/)
 * Copyright (c) The Apache Software Foundation
 * Licensed under the Apache License 2.0
 */

package karina.lang.string;


import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.*;

abstract class StringTranslator {

    protected static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    protected static String hex(int codePoint) {
        return Integer.toHexString(codePoint).toUpperCase(Locale.ENGLISH);
    }

    protected abstract int translate(CharSequence input, int index, Writer writer) throws IOException;

    protected final String translate(CharSequence input) {
        try {
            var writer = new StringWriter(input.length() * 2);
            translate(input, writer);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new UncheckedIOException(ioe);
        }
    }


    protected final void translate(CharSequence input, Writer writer) throws IOException {
        if (input == null) {
            return;
        }
        var pos = 0;
        var len = input.length();
        while (pos < len) {
            var consumed = translate(input, pos, writer);
            if (consumed == 0) {
                // inlined implementation of Character.toChars(Character.codePointAt(input, pos))
                // avoids allocating temp char arrays and duplicate checks
                var c1 = input.charAt(pos);
                writer.write(c1);
                pos++;
                if (Character.isHighSurrogate(c1) && pos < len) {
                    var c2 = input.charAt(pos);
                    if (Character.isLowSurrogate(c2)) {
                        writer.write(c2);
                        pos++;
                    }
                }
                continue;
            }
            // contract with translators is that they have to understand code points
            // and they just took care of a surrogate pair
            for (int pt = 0; pt < consumed; pt++) {
                pos += Character.charCount(Character.codePointAt(input, pos));
            }
        }
    }

    protected abstract static class CodePointTranslator extends StringTranslator {
        @Override
        protected final int translate(CharSequence input, int index, Writer writer) throws IOException {
            var codePoint = Character.codePointAt(input, index);
            var consumed = translate(codePoint, writer);
            return consumed ? 1 : 0;
        }
        protected abstract boolean translate(int codePoint, Writer writer) throws IOException;

    }

    protected static class UnicodeTranslatorEscape extends CodePointTranslator {
        private final int below;
        private final int above;
        private final boolean between;

        protected UnicodeTranslatorEscape(int below, int above, boolean between) {
            this.below = below;
            this.above = above;
            this.between = between;
        }

        protected String toUtf16Escape(int codePoint) {
            return "\\u" + hex(codePoint);
        }


        @Override
        protected boolean translate(int codePoint, Writer writer) throws IOException {
            if (this.between) {
                if (codePoint < this.below || codePoint > this.above) {
                    return false;
                }
            } else if (codePoint >= this.below && codePoint <= this.above) {
                return false;
            }

            if (codePoint > 0xffff) {
                writer.write(toUtf16Escape(codePoint));
            } else {
                writer.write("\\u");
                writer.write(HEX_DIGITS[codePoint >> 12 & 15]);
                writer.write(HEX_DIGITS[codePoint >> 8 & 15]);
                writer.write(HEX_DIGITS[codePoint >> 4 & 15]);
                writer.write(HEX_DIGITS[codePoint & 15]);
            }
            return true;
        }
    }

    protected static class JavaUnicodeTranslatorEscape extends UnicodeTranslatorEscape {
        protected JavaUnicodeTranslatorEscape(int below, int above, boolean between) {
            super(below, above, between);
        }

        @Override
        protected String toUtf16Escape(int codePoint) {
            var surrogatePair = Character.toChars(codePoint);
            return "\\u" + hex(surrogatePair[0]) + "\\u" + hex(surrogatePair[1]);
        }
    }

    protected static class UnicodeTranslatorUnescape extends StringTranslator {

        @Override
        protected int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
            if (input.charAt(index) == '\\' && index + 1 < input.length() && input.charAt(index + 1) == 'u') {
                // consume optional additional 'u' chars
                int i = 2;
                while (index + i < input.length() && input.charAt(index + i) == 'u') {
                    i++;
                }

                if (index + i < input.length() && input.charAt(index + i) == '+') {
                    i++;
                }

                if (index + i + 4 <= input.length()) {
                    // Get 4 hex digits
                    final CharSequence unicode = input.subSequence(index + i, index + i + 4);

                    try {
                        final int value = Integer.parseInt(unicode.toString(), 16);
                        writer.write((char) value);
                    } catch (final NumberFormatException nfe) {
                        throw new IllegalArgumentException("Unable to parse unicode value: " + unicode, nfe);
                    }
                    return i + 4;
                }
                throw new IllegalArgumentException("Less than 4 hex digits in unicode value: '"
                        + input.subSequence(index, input.length())
                        + "' due to end of CharSequence");
            }
            return 0;
        }
    }


    protected static class OctalTranslatorUnescape extends StringTranslator {

        private boolean isOctalDigit(final char ch) {
            return ch >= '0' && ch <= '7';
        }

        private boolean isZeroToThree(final char ch) {
            return ch >= '0' && ch <= '3';
        }

        @Override
        protected int translate(final CharSequence input, final int index, final Writer writer) throws IOException {
            final int remaining = input.length() - index - 1; // how many characters left, ignoring the first \
            final StringBuilder builder = new StringBuilder();
            if (input.charAt(index) == '\\' && remaining > 0 && isOctalDigit(input.charAt(index + 1))) {
                final int next = index + 1;
                final int next2 = index + 2;
                final int next3 = index + 3;

                // we know this is good as we checked it in the if block above
                builder.append(input.charAt(next));

                if (remaining > 1 && isOctalDigit(input.charAt(next2))) {
                    builder.append(input.charAt(next2));
                    if (remaining > 2 && isZeroToThree(input.charAt(next)) && isOctalDigit(input.charAt(next3))) {
                        builder.append(input.charAt(next3));
                    }
                }

                writer.write(Integer.parseInt(builder.toString(), 8));
                return 1 + builder.length();
            }
            return 0;
        }
    }

    protected static class AggregateTranslator extends StringTranslator {

        private final List<StringTranslator> translators;

        protected AggregateTranslator(StringTranslator... translators) {
            this.translators = Arrays.asList(translators);
        }

        @Override
        protected int translate(CharSequence input, int index, Writer writer) throws IOException {
            for (var translator : this.translators) {
                var consumed = translator.translate(input, index, writer);
                if (consumed != 0) {
                    return consumed;
                }
            }
            return 0;
        }

    }


    protected static class LookupTranslator extends StringTranslator {
        private final Map<String, String> lookupMap;
        private final BitSet prefixSet;
        private final int shortest;
        private final int longest;

        protected LookupTranslator(Map<CharSequence, CharSequence> lookupMap) {
            this.lookupMap = new HashMap<>();
            this.prefixSet = new BitSet();
            int currentShortest = Integer.MAX_VALUE;
            int currentLongest = 0;

            for (Map.Entry<CharSequence, CharSequence> pair : lookupMap.entrySet()) {
                var key = pair.getKey();
                var value = pair.getValue();

                this.lookupMap.put(key.toString(), value.toString());
                this.prefixSet.set(key.charAt(0));
                var sz = key.length();
                if (sz < currentShortest) {
                    currentShortest = sz;
                }
                if (sz > currentLongest) {
                    currentLongest = sz;
                }
            }
            this.shortest = currentShortest;
            this.longest = currentLongest;
        }

        @Override
        protected int translate(CharSequence input, int index, Writer writer) throws IOException {
            // check if translation exists for the input at position index
            if (this.prefixSet.get(input.charAt(index))) {
                int max = this.longest;
                if (index + this.longest > input.length()) {
                    max = input.length() - index;
                }
                // implement greedy algorithm by trying maximum match first
                for (int i = max; i >= this.shortest; i--) {
                    var subSeq = input.subSequence(index, index + i);
                    var result = this.lookupMap.get(subSeq.toString());

                    if (result != null) {
                        writer.write(result);
                        return Character.codePointCount(subSeq, 0, subSeq.length());
                    }
                }
            }
            return 0;
        }
    }

}
