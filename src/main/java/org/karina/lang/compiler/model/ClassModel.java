package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

public interface ClassModel {
    String name();
    int modifiers();
    ObjectPath path();
    @Nullable ClassPointer superClass();
    @Nullable ClassPointer outerClass();
    ImmutableList<ClassPointer> interfaces();
    ImmutableList<ClassPointer> innerClasses();
    ImmutableList<? extends FieldModel> fields();
    ImmutableList<Generic> generics();
    ImmutableList<? extends MethodModel> methods();
    ImmutableList<ClassPointer> permittedSubclasses();
    TextSource resource();
    ClassPointer pointer();
    Region region();
}
