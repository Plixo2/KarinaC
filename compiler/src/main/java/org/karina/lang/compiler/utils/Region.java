package org.karina.lang.compiler.utils;


import java.util.List;

/**
 * Represents a region of a text in a given source file.
 * @param source The source file
 * @param start Starting line and column (0-based)
 * @param end Ending line and column (0-based)
 */
public record Region(TextSource source, Position start, Position end) {

    public record Position(int line, int column) {

        public boolean isBefore(Position other) {
            return this.line < other.line || (this.line == other.line && this.column < other.column);
        }
        public boolean isBeforeOrEqual(Position other) {
            return this.equals(other) || this.isBefore(other);
        }
    }

    public Region merge(Region other) {
        var start = this.start.isBefore(other.start) ? this.start : other.start;
        var end = this.end.isBefore(other.end) ? other.end : this.end;
        return new Region(this.source, start, end);
    }

    public Region reorder() {

        var isOnWrongLine = this.start.line() > this.end.line();
        var isOnWrongColumn = this.start.line() == this.end.line() && this.start.column() > this.end.column();
        if (isOnWrongLine || isOnWrongColumn) {
            return new Region(this.source, this.end, this.start);
        }
        return this;

    }

    public boolean doesContainPosition(Position position) {
        var reordered = this.reorder();
        var onEndLine = reordered.end.line() == position.line();
        var onStartLine = reordered.start.line() == position.line();
        if (onEndLine && onStartLine) {
            return reordered.start.column() <= position.column() && reordered.end.column() >= position.column();
        } else if (onEndLine) {
            return reordered.end.column() >= position.column();
        } else if (onStartLine) {
            return reordered.start.column() <= position.column();
        } else {
            return reordered.start.line() < position.line() && reordered.end.line() > position.line();
        }
    }

    public boolean intersects(Region other) {
        return this.start.isBeforeOrEqual(other.end)
                && other.start.isBeforeOrEqual(this.end);


    }

    @Override
    public String toString() {
        var region = this.reorder();
        var path = region.source().resource().identifier();
        var column = region.start().column() + 1;
        var line = region.start().line() + 1;
        return path + ":" + line + ":" + column;
    }
}

