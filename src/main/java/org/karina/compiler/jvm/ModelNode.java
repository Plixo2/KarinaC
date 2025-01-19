package org.karina.compiler.jvm;

import org.karina.compiler.model.ClassModel;
import org.karina.compiler.model.FieldModel;
import org.karina.compiler.model.MethodModel;
import org.karina.compiler.model.Model;
import org.karina.compiler.model.pointer.ClassPointer;
import org.karina.compiler.model.pointer.FieldPointer;
import org.karina.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.HashMap;
import java.util.Map;

public class ModelNode implements Model {
    public Map<ObjectPath, ClassModel> classModels = new HashMap<>();


    @Override
    public ClassModel getClass(ClassPointer pointer) {
        var classModel = this.classModels.get(pointer.path());

        if (classModel == null) {
            throw new NullPointerException("Class not found: " + pointer.path());
        }

        return classModel;
    }

    @Override
    public MethodModel getModel(MethodPointer model) {
        throw new NullPointerException("Not implemented");
    }

    @Override
    public FieldModel fieldPointer(FieldPointer pointer) {
        var classModel = getClass(pointer.classPointer());
        for (var field : classModel.fields()) {
            if (field.name().equals(pointer.fieldName())) {
                return field;
            }
        }
        throw new NullPointerException("Field not found");
    }
}
