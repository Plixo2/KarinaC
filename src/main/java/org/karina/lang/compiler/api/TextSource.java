package org.karina.lang.compiler.api;

import java.util.List;

public interface TextSource {
    List<String> lines();
    Resource resource();
}
