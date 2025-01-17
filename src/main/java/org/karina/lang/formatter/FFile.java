package org.karina.lang.formatter;

public class FFile {
    StringBuilder builder = new StringBuilder();
    int level;

    public void append(String string) {
        this.builder.append(string);
    }

    public void line() {
        this.builder.append("\n");
        this.builder.append("\t".repeat(Math.max(0, this.level)));
    }

    public void enter() {
        this.level++;
    }

    public void exit() {
        this.level--;
        if (this.level < 0) {
            throw new RuntimeException("Negative level");
        }
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
