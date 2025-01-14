package org.karina.lang.compiler.json;

import org.karina.lang.compiler.utils.Span;

import java.util.List;

public record JsonArray(Span region, List<JsonElement> elements) implements JsonElement {

    @Override
    public String toString() {

        var contentString = this.elements.stream().map(JsonElement::toString).toList();
        var content = String.join(", ", contentString);
        return "[" + content + "]";

    }


}
