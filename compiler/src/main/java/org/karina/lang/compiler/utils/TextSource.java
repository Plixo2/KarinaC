package org.karina.lang.compiler.utils;


public record TextSource(Resource resource, String content) {

    public Region emptyRegion() {
        var start = new Region.Position(0, 0);
        var end = new Region.Position(0, 0);
        return new Region(this, start, end);
    }
}
