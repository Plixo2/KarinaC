package org.karina.lang.compiler.model_api;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

public interface Model {

    //tests if a class exists
    @Nullable ClassPointer getClassPointer(Region region, ObjectPath objectPath);
    ClassModel getClass(ClassPointer pointer);
    MethodModel getMethod(MethodPointer model);
    FieldModel getField(FieldPointer pointer);

}
