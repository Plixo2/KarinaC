package karina.lang;


/**
 * String utility class for compiling interpolated strings.
 * Should be replaced with a more sophisticated implementation in the future.
 */
public class StringInterpolation {

    private final StringBuilder sb;

    private StringInterpolation() {
        this.sb = new StringBuilder();
    }

    public static StringInterpolation create() {
        return new StringInterpolation();
    }

    public StringInterpolation appendLiteral(String str) {
        this.sb.append(str);
        return this;
    }

    public StringInterpolation appendExpression(int i) {
        this.sb.append(Prelude.toString(i));
        return this;
    }

    public StringInterpolation appendExpression(long l) {
        this.sb.append(Prelude.toString(l));
        return this;
    }

    public StringInterpolation appendExpression(double d) {
        this.sb.append(Prelude.toString(d));
        return this;
    }

    public StringInterpolation appendExpression(float f) {
        this.sb.append(Prelude.toString(f));
        return this;
    }

    public StringInterpolation appendExpression(char c) {
        this.sb.append(Prelude.toString(c));
        return this;
    }

    public StringInterpolation appendExpression(byte b) {
        this.sb.append(Prelude.toString(b));
        return this;
    }

    public StringInterpolation appendExpression(short s) {
        this.sb.append(Prelude.toString(s));
        return this;
    }

    public StringInterpolation appendExpression(boolean b) {
        this.sb.append(Prelude.toString(b));
        return this;
    }

    public StringInterpolation appendExpression(Object obj) {
        this.sb.append(Prelude.toString(obj));
        return this;
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }
}
