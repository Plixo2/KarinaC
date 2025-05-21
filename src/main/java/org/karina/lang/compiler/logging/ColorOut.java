package org.karina.lang.compiler.logging;

import java.io.PrintStream;

public class ColorOut {

    private StringBuilder sb;
    private boolean hasColor = false;
    private ColorOut() {
        this.sb = new StringBuilder();
    }

    public static ColorOut begin() {
        return new ColorOut();
    }

    public static ColorOut begin(String string) {
        return begin().append(string);
    }

    public static ColorOut begin(LogColor color) {
        return begin().color(color);
    }

    public ColorOut append(Object string) {
        this.sb.append(string);
        return this;
    }

    public ColorOut color(LogColor color) {
        this.sb.append(color.getColorCode());
        if (color != LogColor.NONE) {
            this.hasColor = true;
        }
        return this;
    }

    public ColorOut resetColor() {
        if (this.hasColor) {
            this.hasColor = false;
            this.sb.append(LogColor.NONE.getColorCode());
        }
        return this;
    }


    public String toString() {
        if (this.hasColor) {
            return this.sb.toString() + LogColor.NONE.getColorCode();
        }
        return this.sb.toString();
    }


    public void out(PrintStream out) {
        out.println(resetColor());
    }
}
