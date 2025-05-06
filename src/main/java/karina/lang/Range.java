package karina.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

//Build-in class for numbers in for loop
public final class Range implements Iterable<Integer> {
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

    @Override
    public @NotNull Iterator<Integer> iterator() {
        if (this.step > 0) {
            return new ForwardIterator(this.start, this.end, this.step);
        } else {
            return new BackwardIterator(this.start, this.end, this.step);
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
