package org.karina.lang.compiler.jvm.model;

import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model.ClassModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.Model;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JModel implements Model {
    private final Map<ObjectPath, ClassModel> classModels = new HashMap<>();

    public void addClass(ClassModel classModel) {
        this.classModels.put(classModel.path(), classModel);
    }


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

    public List<KClassModel> getUserClasses() {
        return this.classModels.values().stream().filter(c -> c instanceof KClassModel)
                               .map(c -> (KClassModel) c).toList();
    }

    public List<ClassModel> getClasses() {
        return this.classModels.values().stream().toList();
    }

    public List<JClassModel> getBytecodeClasses() {
        return this.classModels.values().stream().filter(c -> c instanceof JClassModel)
                               .map(c -> (JClassModel) c).toList();
    }
}
