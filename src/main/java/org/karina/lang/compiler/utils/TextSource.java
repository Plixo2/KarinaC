package org.karina.lang.compiler.utils;

import java.util.List;

public record TextSource(Resource resource, List<String> lines) {

    public Region emptyRegion() {
        var start = new Region.Position(0, 0);
        var end = new Region.Position(0, 0);
        return new Region(this, start, end);
    }
}
