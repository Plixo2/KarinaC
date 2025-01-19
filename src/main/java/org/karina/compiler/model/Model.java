package org.karina.compiler.model;

import org.karina.compiler.model.pointer.ClassPointer;
import org.karina.compiler.model.pointer.FieldPointer;
import org.karina.compiler.model.pointer.MethodPointer;

public interface Model {

    ClassModel getClass(ClassPointer pointer);
    MethodModel getModel(MethodPointer model);
    FieldModel fieldPointer(FieldPointer pointer);

}
