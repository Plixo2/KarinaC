package org.karina.lang.compiler.json;

import org.karina.lang.compiler.utils.Span;

import java.util.List;

public record JsonObject(Span region, List<JsonMember> members) implements JsonElement {

    @Override
    public String toString() {

        var memberStrings = this.members.stream().map(JsonMember::toString).toList();
        var content = String.join(",", memberStrings);
        return "{" + content + "}";

    }

    public record JsonMember(Span region, String name, JsonElement value) {

        public String toString() {
            return this.name + ":" + this.value;
        }
    }


}
