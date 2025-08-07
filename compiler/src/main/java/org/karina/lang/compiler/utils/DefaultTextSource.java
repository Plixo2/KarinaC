package org.karina.lang.compiler.utils;

public record DefaultTextSource(Resource resource, String content) implements TextSource {
}
