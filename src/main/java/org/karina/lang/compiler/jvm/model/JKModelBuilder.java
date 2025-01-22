package org.karina.lang.compiler.jvm.model;

import org.karina.lang.compiler.errors.Log;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Thread Safe builder for a JKModel
 */
public class JKModelBuilder {

    Map<ObjectPath, ClassModel> classModels = new ConcurrentHashMap<>();

    public JKModelBuilder() {

    }

    //Thread safe
    public void addClass(ClassModel classModel) {
        if (this.classModels.containsKey(classModel.path())) {
            var msg = "Class already exists: " + classModel.path();
            Log.bytecode(classModel.resource().resource(), classModel.name(), msg);
            throw new Log.KarinaException();
        }
        this.classModels.put(classModel.path(), classModel);
    }

    public JKModel build() {
        return new JKModel(this.classModels);
    }
}
