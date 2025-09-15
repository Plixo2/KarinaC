package karina.lang.json;

public record JsonFormatter(String newline, String indentation, String fieldValueSeparator) {

    public static JsonFormatter DEFAULT = new JsonFormatter("\n", "    ", " ");
    public static JsonFormatter COMPACT = new JsonFormatter("", "", "");
}
