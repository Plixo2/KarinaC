package org.karina.lang.compiler.model;

import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;

public interface Model {

    ClassModel getClass(ClassPointer pointer);
    MethodModel getModel(MethodPointer model);
    FieldModel fieldPointer(FieldPointer pointer);

}
