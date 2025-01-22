package org.karina.lang.compiler.model;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

public interface Model {

    //tests if a class exists
    @Nullable ClassPointer getClassPointer(ObjectPath objectPath);
    ClassModel getClass(ClassPointer pointer);
    MethodModel getMethod(MethodPointer model);
    FieldModel fieldPointer(FieldPointer pointer);

}
