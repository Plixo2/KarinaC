package org.karina.lang.compiler.logging;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

@RequiredArgsConstructor
public enum LogColor {

    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    GRAY("\u001B[37m"),
    WHITE(""),
    NONE("\u001B[0m"),

    ;
    private final String colorCode;

    public String getColorCode() {
        //TODO test for different systems
        return this.colorCode;
    }

    @Override
    public String toString() {
        return getColorCode();
    }

    public void out(PrintStream out) {
        out.print(getColorCode());
    }
}
