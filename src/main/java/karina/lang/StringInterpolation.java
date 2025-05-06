package karina.lang;


public class StringInterpolation {

    private String str = "";

    public static StringInterpolation create() {
        return new StringInterpolation();
    }

    public StringInterpolation appendLiteral(String str) {
        this.str += str;
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
