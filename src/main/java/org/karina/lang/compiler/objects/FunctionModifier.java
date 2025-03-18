package org.karina.lang.compiler.objects;

import lombok.Getter;
import lombok.experimental.Accessors;

@Deprecated()
@Getter
@Accessors(fluent = true)
public class FunctionModifier {
    private boolean isPrivate = false;
    private boolean isStatic = false;

    public FunctionModifier(boolean isPrivate, boolean isStatic) {
        this.isPrivate = isPrivate;
        this.isStatic = isStatic;
    }

    public FunctionModifier() {
    }
}
