package org.karina.lang.compiler.model;

import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

public interface FieldModel {
    int modifiers();
    String name();
    KType type();
    Region region();

    FieldPointer pointer();
    ClassPointer classPointer();
}
