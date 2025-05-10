package karina.lang;


/**
 * String interpolation utility class.
 * Should be replaced with a more sophisticated implementation in the future.
 */
public class StringInterpolation {

    private String str = "";

    public static StringInterpolation create() {
        return new StringInterpolation();
    }

    public StringInterpolation appendLiteral(String str) {
        this.str += str;
        return this;
    }

    public StringInterpolation appendExpression(int i) {
        this.str += Console.toString(i);
        return this;
    }

    public StringInterpolation appendExpression(long l) {
        this.str += Console.toString(l);
        return this;
    }

    public StringInterpolation appendExpression(double d) {
        this.str += Console.toString(d);
        return this;
    }

    public StringInterpolation appendExpression(float f) {
        this.str += Console.toString(f);
        return this;
    }

    public StringInterpolation appendExpression(char c) {
        this.str += Console.toString(c);
        return this;
    }

    public StringInterpolation appendExpression(byte b) {
        this.str += Console.toString(b);
        return this;
    }

    public StringInterpolation appendExpression(short s) {
        this.str += Console.toString(s);
        return this;
    }


    public StringInterpolation appendExpression(Object obj) {
        this.str += Console.toString(obj);
        return this;
    }

    @Override
    public String toString() {
        return this.str;
    }
}
