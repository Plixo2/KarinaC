package org.karina.lang.compiler.model_api;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;

public interface FieldModel {
    int modifiers();
    String name();
    KType type();
    Region region();

    /**
     * the field's initial value. This parameter, which may be {@literal null} if the
     * field does not have an initial value, must be an
     * {@link Integer}, a {@link Float}, a {@link Long}, a {@link Double} or a {@link String}.
     */
    @Nullable Object defaultValue();

    FieldPointer pointer();
    ClassPointer classPointer();
}
