package org.karina.lang.compiler.utils;

import java.io.File;

public record FileResource(File file) implements Resource {

    @Override
    public String identifier() {
        var path = this.file.getAbsolutePath().replace("\\", "/");
        var prefix = "file:///";
        return prefix + path;
    }
}
