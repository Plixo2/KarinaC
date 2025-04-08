package karina.lang;

//Build-in class for numbers in for loop
public final class Range {
    public final int start;
    public final int end;
    public final int step;

    public Range(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public Range reversed() {
        return new Range(this.end, this.start, -this.step);
    }

    public static Range range(int start, int end) {
        return new Range(start, end, 1);
    }
    public static Range range(int start, int end, int step) {
        return new Range(start, end, step);
    }
}
