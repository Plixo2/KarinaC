package org.karina.lang.compiler.jvm;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.api.Resource;

public record JavaResource(String identifier) implements Resource {
    @Override
    public String identifier() {
        return "";
    }
}
