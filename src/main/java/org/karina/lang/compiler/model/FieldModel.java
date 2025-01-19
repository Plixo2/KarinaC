package org.karina.lang.compiler.model;

import org.karina.lang.compiler.objects.KType;

public interface FieldModel {
    int modifiers();
    String name();
    KType type();
}
