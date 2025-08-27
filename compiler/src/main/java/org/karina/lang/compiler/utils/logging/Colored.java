package org.karina.lang.compiler.utils.logging;

import java.io.PrintStream;

/// Helper class to build colored strings for console output.
public class Colored {

    private StringBuilder sb;
    private boolean hasColor = false;
    private Colored() {
        this.sb = new StringBuilder();
    }

    private static Colored begin() {
        return new Colored();
    }

    public static Colored begin(ConsoleColor color) {
        return begin().color(color);
    }

    public Colored append(Object string) {
        this.sb.append(string);
        return this;
    }

    public Colored color(ConsoleColor color) {
        this.sb.append(color.getColorCode());
        if (color != ConsoleColor.RESET) {
            this.hasColor = true;
        }
        return this;
    }

    public Colored resetColor() {
        if (this.hasColor) {
            this.hasColor = false;
            this.sb.append(ConsoleColor.RESET.getColorCode());
        }
        return this;
    }


    public String toString() {
        if (this.hasColor) {
            return this.sb.toString() + ConsoleColor.RESET.getColorCode();
        }
        return this.sb.toString();
    }


    public void println(PrintStream out) {
        out.println(resetColor());
    }

    public void print(PrintStream out) {
        out.print(resetColor());
    }
}
