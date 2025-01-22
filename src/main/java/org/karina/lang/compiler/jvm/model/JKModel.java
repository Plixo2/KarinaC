package org.karina.lang.compiler.jvm.model;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
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
import org.karina.lang.compiler.utils.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java-Karina Model
 * Represents ALL class models of the program.
 * @param classModels
 */
public record JKModel(Map<ObjectPath, ClassModel> classModels) implements Model {

    public JKModel {
        classModels = new HashMap<>(classModels);
    }

    public JKModel() {
        this(Map.of());
    }

    public JKModel merge(JKModel other) {
        var newClassModels = new HashMap<>(this.classModels);
        for (var entry : other.classModels.entrySet()) {
            if (newClassModels.containsKey(entry.getKey())) {
                var msg = "Class already exists: " + entry.getKey();
                Log.bytecode(entry.getValue().resource().resource(), entry.getValue().name(), msg);
                throw new Log.KarinaException();
            }
            newClassModels.put(entry.getKey(), entry.getValue());
        }
        return new JKModel(newClassModels);
    }

    @Override
    public @Nullable ClassPointer getClassPointer(ObjectPath objectPath) {
        if (this.classModels.containsKey(objectPath)) {
            //OK
            return ClassPointer.of(objectPath);
        }
        return null;
    }

    @Override
    public ClassModel getClass(ClassPointer pointer) {
        var classModel = this.classModels.get(pointer.path());

        if (classModel == null) {
            //TODO: Log
            //This should not happen, every ClassPointer should be already valid
            throw new NullPointerException("Class not found " + pointer.path());
        }

        return classModel;
    }

    @Override
    public MethodModel getMethod(MethodPointer model) {
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

    public List<JClassModel> getBytecodeClasses() {
        return this.classModels.values().stream().filter(c -> c instanceof JClassModel)
                               .map(c -> (JClassModel) c).toList();
    }

    public int hashCodeExpensive() {

        int expensiveHashMethod = 0;
        for (var classModel : this.classModels.values()) {
            if (classModel instanceof JClassModel jClassModel) {
                expensiveHashMethod = Objects.hash(expensiveHashMethod, jClassModel.hashCodeExpensive());
            }
        }

        return expensiveHashMethod;
    }
}
