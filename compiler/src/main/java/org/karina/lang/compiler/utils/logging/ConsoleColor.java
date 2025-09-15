package org.karina.lang.compiler.utils.logging;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

@RequiredArgsConstructor
public enum ConsoleColor {

    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    GRAY("\u001B[37m"),
    WHITE("\u001B[0m"),
    RESET("\u001B[0m"),
    NONE(""),

    ;
    private final String colorCode;

    public String getColorCode() {
        //TODO test this code for different platforms
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
