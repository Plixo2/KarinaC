package org.karina.lang.compiler.api;

import java.util.List;

public record TextSource(Resource resource, List<String> lines) {

}
