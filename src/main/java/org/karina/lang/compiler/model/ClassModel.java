package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.ObjectPath;

public interface ClassModel {
    String name();
    int modifiers();
    ObjectPath path();
    @Nullable ClassPointer superClass();
    ImmutableList<ClassPointer> interfaces();
    ImmutableList<ClassPointer> innerClasses();
    ImmutableList<FieldModel> fields();
    ImmutableList<MethodModel> methods();
    TextSource resource();
}
