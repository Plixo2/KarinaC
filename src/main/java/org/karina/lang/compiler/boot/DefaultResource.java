package org.karina.lang.compiler.boot;

import org.karina.lang.compiler.api.Resource;

import java.io.File;

public record DefaultResource(File file) implements Resource {

    @Override
    public String identifier() {
        var path = this.file.getAbsolutePath().replace("\\", "/");
        var prefix = "file:///";
        return prefix + path;
    }
}
