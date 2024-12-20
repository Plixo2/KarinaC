package org.karina.lang.compiler;

import org.karina.lang.compiler.api.Resource;
import org.karina.lang.compiler.api.TextSource;

import java.io.File;
import java.util.List;

public record DefaultFile(Resource resource, List<String> lines)
        implements TextSource {
    @Override
    public String toString() {
        return this.resource.identifier();
    }

    public record FileResource(File file) implements Resource {
        @Override
        public String identifier() {
            var path = this.file.getAbsolutePath().replace("\\", "/");
            var prefix = "file:///";
            return prefix + path;
        }
    }
}
