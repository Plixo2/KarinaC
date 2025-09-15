/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

/*
 * Modified code from minimal-json
 * (https://github.com/ralfstx/minimal-json)
 * licensed under the MIT License.
 */
package karina.lang.json;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A streaming parser for JSON text. The parser reports all events to a given handler.
 */
class MinimalJsonParser<A, O> {

    private static final int MAX_NESTING_LEVEL = 1000;
    private static final int MIN_BUFFER_SIZE = 10;
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final JsonHandler<A, O> handler;
    private Reader reader;
    private char[] buffer;
    private int bufferOffset;
    private int index;
    private int fill;
    private int line;
    private int lineOffset;
    private int current;
    private StringBuilder captureBuffer;
    private int captureStart;
    private int nestingLevel;

    /*
     * |                      bufferOffset
     *                        v
     * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t]        < input
     *                       [l|m|n|o|p|q|r|s|t|?|?]    < buffer
     *                          ^               ^
     *                       |  index           fill
     */

    /**
     * Creates a new JsonParser with the given handler. The parser will report all parser events to
     * this handler.
     *
     * @param handler
     *          the handler to process parser events
     */
    public MinimalJsonParser(JsonHandler<A, O> handler) {
        this.handler = handler;
    }

    /**
     * Parses the given input string. The input must contain a valid JSON value, optionally padded
     * with whitespace.
     *
     * @param string
     *          the input string, must be valid JSON
     * @throws JsonParseException
     *           if the input is not valid JSON
     */
    public void parse(String string) throws JsonParseException {
        int bufferSize = Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, string.length()));
        try {
            parse(new StringReader(string), bufferSize);
        } catch (IOException exception) {
            // StringReader does not throw IOException
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Reads the entire input from the given reader and parses it as JSON. The input must contain a
     * valid JSON value, optionally padded with whitespace.
     * <p>
     * Characters are read in chunks into a default-sized input buffer. Hence, wrapping a reader in an
     * additional <code>BufferedReader</code> likely won't improve reading performance.
     * </p>
     *
     * @param reader
     *          the reader to read the input from
     * @throws IOException
     *           if an I/O error occurs in the reader
     * @throws JsonParseException
     *           if the input is not valid JSON
     */
    public void parse(Reader reader) throws IOException {
        parse(reader, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Reads the entire input from the given reader and parses it as JSON. The input must contain a
     * valid JSON value, optionally padded with whitespace.
     * <p>
     * Characters are read in chunks into an input buffer of the given size. Hence, wrapping a reader
     * in an additional <code>BufferedReader</code> likely won't improve reading performance.
     * </p>
     *
     * @param reader
     *          the reader to read the input from
     * @param buffersize
     *          the size of the input buffer in chars
     * @throws IOException
     *           if an I/O error occurs in the reader
     * @throws JsonParseException
     *           if the input is not valid JSON
     */
    public void parse(Reader reader, int buffersize) throws IOException {
        if (buffersize <= 0) {
            throw new IllegalArgumentException("buffersize is zero or negative");
        }
        this.reader = reader;
        this.buffer = new char[buffersize];
        this.bufferOffset = 0;
        this.index = 0;
        this.fill = 0;
        this.line = 1;
        this.lineOffset = 0;
        this.current = 0;
        this.captureStart = -1;
        read();
        skipWhiteSpace();
        readValue();
        skipWhiteSpace();
        if (!isEndOfText()) {
            throw error("Unexpected character");
        }
    }

    private void readValue() throws IOException {
        switch (this.current) {
            case 'n':
                readNull();
                break;
            case 't':
                readTrue();
                break;
            case 'f':
                readFalse();
                break;
            case '"':
                readString();
                break;
            case '[':
                readArray();
                break;
            case '{':
                readObject();
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                readNumber();
                break;
            default:
                throw expected("value");
        }
    }

    private void readArray() throws IOException {
        var array = this.handler.startArray();
        read();
        if (++this.nestingLevel > MAX_NESTING_LEVEL) {
            throw error("Nesting too deep");
        }
        skipWhiteSpace();
        if (readChar(']')) {
            this.nestingLevel--;
            this.handler.endArray(array);
            return;
        }
        do {
            skipWhiteSpace();
            this.handler.startArrayValue(array);
            readValue();
            this.handler.endArrayValue(array);
            skipWhiteSpace();
        } while (readChar(','));
        if (!readChar(']')) {
            throw expected("',' or ']'");
        }
        this.nestingLevel--;
        this.handler.endArray(array);
    }

    private void readObject() throws IOException {
        var object = this.handler.startObject();
        read();
        if (++this.nestingLevel > MAX_NESTING_LEVEL) {
            throw error("Nesting too deep");
        }
        skipWhiteSpace();
        if (readChar('}')) {
            this.nestingLevel--;
            this.handler.endObject(object);
            return;
        }
        do {
            skipWhiteSpace();
            this.handler.startObjectName(object);
            String name = readName();
            this.handler.endObjectName(object, name);
            skipWhiteSpace();
            if (!readChar(':')) {
                throw expected("':'");
            }
            skipWhiteSpace();
            this.handler.startObjectValue(object, name);
            readValue();
            this.handler.endObjectValue(object, name);
            skipWhiteSpace();
        } while (readChar(','));
        if (!readChar('}')) {
            throw expected("',' or '}'");
        }
        this.nestingLevel--;
        this.handler.endObject(object);
    }

    private String readName() throws IOException {
        if (this.current != '"') {
            throw expected("name");
        }
        return readStringInternal();
    }

    private void readNull() throws IOException {
        this.handler.startNull();
        read();
        readRequiredChar('u');
        readRequiredChar('l');
        readRequiredChar('l');
        this.handler.endNull();
    }

    private void readTrue() throws IOException {
        this.handler.startBoolean();
        read();
        readRequiredChar('r');
        readRequiredChar('u');
        readRequiredChar('e');
        this.handler.endBoolean(true);
    }

    private void readFalse() throws IOException {
        this.handler.startBoolean();
        read();
        readRequiredChar('a');
        readRequiredChar('l');
        readRequiredChar('s');
        readRequiredChar('e');
        this.handler.endBoolean(false);
    }

    private void readRequiredChar(char ch) throws IOException {
        if (!readChar(ch)) {
            throw expected("'" + ch + "'");
        }
    }

    private void readString() throws IOException {
        this.handler.startString();
        this.handler.endString(readStringInternal());
    }

    private String readStringInternal() throws IOException {
        read();
        startCapture();
        while (this.current != '"') {
            if (this.current == '\\') {
                pauseCapture();
                readEscape();
                startCapture();
            } else if (this.current < 0x20) {
                throw expected("valid string character");
            } else {
                read();
            }
        }
        String string = endCapture();
        read();
        return string;
    }

    private void readEscape() throws IOException {
        read();
        switch (this.current) {
            case '"':
            case '/':
            case '\\':
                this.captureBuffer.append((char) this.current);
                break;
            case 'b':
                this.captureBuffer.append('\b');
                break;
            case 'f':
                this.captureBuffer.append('\f');
                break;
            case 'n':
                this.captureBuffer.append('\n');
                break;
            case 'r':
                this.captureBuffer.append('\r');
                break;
            case 't':
                this.captureBuffer.append('\t');
                break;
            case 'u':
                char[] hexChars = new char[4];
                for (int i = 0; i < 4; i++) {
                    read();
                    if (!isHexDigit()) {
                        throw expected("hexadecimal digit");
                    }
                    hexChars[i] = (char) this.current;
                }
                this.captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected("valid escape sequence");
        }
        read();
    }

    private void readNumber() throws IOException {
        this.handler.startNumber();
        startCapture();
        readChar('-');
        int firstDigit = this.current;
        if (!readDigit()) {
            throw expected("digit");
        }
        if (firstDigit != '0') {
            while (readDigit()) {
            }
        }
        readFraction();
        readExponent();
        this.handler.endNumber(endCapture());
    }

    private void readFraction() throws IOException {
        if (!readChar('.')) {
            return;
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
    }

    private void readExponent() throws IOException {
        if (!readChar('e') && !readChar('E')) {
            return;
        }
        if (!readChar('+')) {
            readChar('-');
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
    }

    private boolean readChar(char ch) throws IOException {
        if (this.current != ch) {
            return false;
        }
        read();
        return true;
    }

    private boolean readDigit() throws IOException {
        if (!isDigit()) {
            return false;
        }
        read();
        return true;
    }

    private void skipWhiteSpace() throws IOException {
        while (isWhiteSpace()) {
            read();
        }
    }

    private void read() throws IOException {
        if (this.index == this.fill) {
            if (this.captureStart != -1) {
                this.captureBuffer.append(this.buffer, this.captureStart, this.fill -
                        this.captureStart
                );
                this.captureStart = 0;
            }
            this.bufferOffset += this.fill;
            this.fill = this.reader.read(this.buffer, 0, this.buffer.length);
            this.index = 0;
            if (this.fill == -1) {
                this.current = -1;
                this.index++;
                return;
            }
        }
        if (this.current == '\n') {
            this.line++;
            this.lineOffset = this.bufferOffset + this.index;
        }
        this.current = this.buffer[this.index++];
    }

    private void startCapture() {
        if (this.captureBuffer == null) {
            this.captureBuffer = new StringBuilder();
        }
        this.captureStart = this.index - 1;
    }

    private void pauseCapture() {
        int end = this.current == -1 ? this.index : this.index - 1;
        this.captureBuffer.append(this.buffer, this.captureStart, end - this.captureStart);
        this.captureStart = -1;
    }

    private String endCapture() {
        int start = this.captureStart;
        int end = this.index - 1;
        this.captureStart = -1;
        if (!this.captureBuffer.isEmpty()) {
            this.captureBuffer.append(this.buffer, start, end - start);
            String captured = this.captureBuffer.toString();
            this.captureBuffer.setLength(0);
            return captured;
        }
        return new String(this.buffer, start, end - start);
    }

    Location getLocation() {
        int offset = this.bufferOffset + this.index - 1;
        int column = offset - this.lineOffset + 1;
        return new Location(offset, this.line, column);
    }

    private JsonParseException expected(String expected) {
        if (isEndOfText()) {
            return error("Unexpected end of input");
        }
        return error("Expected " + expected);
    }

    private JsonParseException error(String message) {
        return new JsonParseException(message, getLocation());
    }

    private boolean isWhiteSpace() {
        return this.current == ' ' || this.current == '\t'
                || this.current == '\n' || this.current == '\r';
    }

    private boolean isDigit() {
        return this.current >= '0' && this.current <= '9';
    }

    private boolean isHexDigit() {
        return this.current >= '0' && this.current <= '9'
                || this.current >= 'a' && this.current <= 'f'
                || this.current >= 'A' && this.current <= 'F';
    }

    private boolean isEndOfText() {
        return this.current == -1;
    }

    /**
     * A handler for parser events. Instances of this class can be given to a {@link MinimalJsonParser}. The
     * parser will then call the methods of the given handler while reading the input.
     * <p>
     * The default implementations of these methods do nothing. Subclasses may override only those
     * methods they are interested in. They can use <code>getLocation()</code> to access the current
     * character position of the parser at any point. The <code>start*</code> methods will be called
     * while the location points to the first character of the parsed element. The <code>end*</code>
     * methods will be called while the location points to the character position that directly follows
     * the last character of the parsed element. Example:
     * </p>
     *
     * <pre>
     * ["lorem ipsum"]
     *  ^            ^
     *  startString  endString
     * </pre>
     * <p>
     * Subclasses that build an object representation of the parsed JSON can return arbitrary handler
     * objects for JSON arrays and JSON objects in {@link #startArray()} and {@link #startObject()}.
     * These handler objects will then be provided in all subsequent parser events for this particular
     * array or object. They can be used to keep track the elements of a JSON array or object.
     * </p>
     *
     * @param <A>
     *          The type of handlers used for JSON arrays
     * @param <O>
     *          The type of handlers used for JSON objects
     * @see MinimalJsonParser
     */
    interface JsonHandler<A, O> {


        /**
         * Indicates the beginning of a <code>null</code> literal in the JSON input. This method will be
         * called when reading the first character of the literal.
         */
        void startNull();

        /**
         * Indicates the end of a <code>null</code> literal in the JSON input. This method will be called
         * after reading the last character of the literal.
         */
        void endNull();

        /**
         * Indicates the beginning of a boolean literal (<code>true</code> or <code>false</code>) in the
         * JSON input. This method will be called when reading the first character of the literal.
         */
        void startBoolean();

        /**
         * Indicates the end of a boolean literal (<code>true</code> or <code>false</code>) in the JSON
         * input. This method will be called after reading the last character of the literal.
         *
         * @param value
         *          the parsed boolean value
         */
        void endBoolean(boolean value);

        /**
         * Indicates the beginning of a string in the JSON input. This method will be called when reading
         * the opening double quote character (<code>'&quot;'</code>).
         */
        void startString();

        /**
         * Indicates the end of a string in the JSON input. This method will be called after reading the
         * closing double quote character (<code>'&quot;'</code>).
         *
         * @param string
         *          the parsed string
         */
        void endString(String string);

        /**
         * Indicates the beginning of a number in the JSON input. This method will be called when reading
         * the first character of the number.
         */
        void startNumber();

        /**
         * Indicates the end of a number in the JSON input. This method will be called after reading the
         * last character of the number.
         *
         * @param string
         *          the parsed number string
         */
        void endNumber(String string);

        /**
         * Indicates the beginning of an array in the JSON input. This method will be called when reading
         * the opening square bracket character (<code>'['</code>).
         * <p>
         * This method may return an object to handle subsequent parser events for this array. This array
         * handler will then be provided in all calls to {@link #startArrayValue(Object)
         * startArrayValue()}, {@link #endArrayValue(Object) endArrayValue()}, and
         * {@link #endArray(Object) endArray()} for this array.
         * </p>
         *
         * @return a handler for this array, or <code>null</code> if not needed
         */
        A startArray();

        /**
         * Indicates the end of an array in the JSON input. This method will be called after reading the
         * closing square bracket character (<code>']'</code>).
         *
         * @param array
         *          the array handler returned from {@link #startArray()}, or <code>null</code> if not
         *          provided
         */
        void endArray(A array);

        /**
         * Indicates the beginning of an array element in the JSON input. This method will be called when
         * reading the first character of the element, just before the call to the <code>start</code>
         * method for the specific element type ({@link #startString()}, {@link #startNumber()}, etc.).
         *
         * @param array
         *          the array handler returned from {@link #startArray()}, or <code>null</code> if not
         *          provided
         */
        void startArrayValue(A array);

        /**
         * Indicates the end of an array element in the JSON input. This method will be called after
         * reading the last character of the element value, just after the <code>end</code> method for the
         * specific element type (like {@link #endString(String) endString()}, {@link #endNumber(String)
         * endNumber()}, etc.).
         *
         * @param array
         *          the array handler returned from {@link #startArray()}, or <code>null</code> if not
         *          provided
         */
        void endArrayValue(A array);

        /**
         * Indicates the beginning of an object in the JSON input. This method will be called when reading
         * the opening curly bracket character (<code>'{'</code>).
         * <p>
         * This method may return an object to handle subsequent parser events for this object. This
         * object handler will be provided in all calls to {@link #startObjectName(Object)
         * startObjectName()}, {@link #endObjectName(Object, String) endObjectName()},
         * {@link #startObjectValue(Object, String) startObjectValue()},
         * {@link #endObjectValue(Object, String) endObjectValue()}, and {@link #endObject(Object)
         * endObject()} for this object.
         * </p>
         *
         * @return a handler for this object, or <code>null</code> if not needed
         */
        O startObject();

        /**
         * Indicates the end of an object in the JSON input. This method will be called after reading the
         * closing curly bracket character (<code>'}'</code>).
         *
         * @param object
         *          the object handler returned from {@link #startObject()}, or null if not provided
         */
        void endObject(O object);

        /**
         * Indicates the beginning of the name of an object member in the JSON input. This method will be
         * called when reading the opening quote character ('&quot;') of the member name.
         *
         * @param object
         *          the object handler returned from {@link #startObject()}, or <code>null</code> if not
         *          provided
         */
        void startObjectName(O object);

        /**
         * Indicates the end of an object member name in the JSON input. This method will be called after
         * reading the closing quote character (<code>'"'</code>) of the member name.
         *
         * @param object
         *          the object handler returned from {@link #startObject()}, or null if not provided
         * @param name
         *          the parsed member name
         */
        void endObjectName(O object, String name);

        /**
         * Indicates the beginning of the name of an object member in the JSON input. This method will be
         * called when reading the opening quote character ('&quot;') of the member name.
         *
         * @param object
         *          the object handler returned from {@link #startObject()}, or <code>null</code> if not
         *          provided
         * @param name
         *          the member name
         */
        void startObjectValue(O object, String name);

        /**
         * Indicates the end of an object member value in the JSON input. This method will be called after
         * reading the last character of the member value, just after the <code>end</code> method for the
         * specific member type (like {@link #endString(String) endString()}, {@link #endNumber(String)
         * endNumber()}, etc.).
         *
         * @param object
         *          the object handler returned from {@link #startObject()}, or null if not provided
         * @param name
         *          the parsed member name
         */
        void endObjectValue(O object, String name);

    }

}