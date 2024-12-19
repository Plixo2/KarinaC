package org.karina.lang.compiler;

import java.io.File;
import java.util.List;

public interface TextSource {
    List<String> lines();
    AbstractResource resource();

    record DefaultFile(AbstractResource resource, List<String> lines) implements TextSource {
        @Override
        public String toString() {
            return this.resource.errorString();
        }
    }

    record FileResource(File file) implements AbstractResource {
        @Override
        public String errorString() {
            var path = this.file.getAbsolutePath().replace("\\", "/");
            var prefix = "file:///";
            return prefix + path;
        }

    }

    interface AbstractResource {

        String errorString();
    }
}
