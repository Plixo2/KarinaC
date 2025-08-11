package karina.lang;


import java.util.Iterator;

//Build-in class for numbers in for loop
public record Range(int start, int end, int step) implements Iterable<Integer> {

    public Range reversed() {
        return new Range(this.end, this.start, -this.step);
    }

    public static Range range(int end) {
        return new Range(0, end, 1);
    }

    public static Range range(int start, int end) {
        return new Range(start, end, 1);
    }

    public static Range range(int start, int end, int step) {
        return new Range(start, end, step);
    }

    @Override
    public Iterator<Integer> iterator() {

        if (this.step > 0) {
            return new ForwardIterator(this.start, this.end, this.step);
        } else if (this.step < 0) {
            return new BackwardIterator(this.start, this.end, this.step);
        } else {
            throw new IllegalArgumentException("Step of Range cannot be zero.");
        }

    }

    private static class ForwardIterator implements Iterator<Integer> {
        private int current;
        private final int end;
        private final int step;

        public ForwardIterator(int start, int end, int step) {
            this.current = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return this.current < this.end;
        }

        @Override
        public Integer next() {
            int value = this.current;
            this.current += this.step;
            return value;
        }
    }

    private static class BackwardIterator implements Iterator<Integer> {
        private int current;
        private final int end;
        private final int step;

        public BackwardIterator(int start, int end, int step) {
            this.current = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public boolean hasNext() {
            return this.current > this.end;
        }

        @Override
        public Integer next() {
            int value = this.current;
            this.current += this.step;
            return value;
        }
    }
}
