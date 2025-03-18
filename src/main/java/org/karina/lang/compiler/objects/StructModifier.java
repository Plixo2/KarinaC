package org.karina.lang.compiler.objects;

import lombok.Getter;
import lombok.experimental.Accessors;

@Deprecated()
@Getter
@Accessors(fluent = true)
public class StructModifier {
    private boolean isPrivate = false;
    private boolean isThrowable = false;

    public StructModifier(boolean isPrivate, boolean isThrowable) {
        this.isPrivate = isPrivate;
        this.isThrowable = isThrowable;
    }

    public StructModifier() {
    }
}
