package org.karina.lang.compiler.objects;

import lombok.Getter;

@Getter

public class StructModifier {
    private boolean isPrivate = false;
    private boolean isNative = false;
    private boolean isThrowable = false;

    public StructModifier(boolean isPrivate, boolean isNative, boolean isThrowable) {
        this.isPrivate = isPrivate;
        this.isNative = isNative;
        this.isThrowable = isThrowable;
    }

    public StructModifier() {
    }
}
