package org.karina.lang.compiler.jvm.model;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.List;

/**
 * Java-Karina Model
 * Represents all class models of the program.
 * @param data
 */
public record JKModel(ClassLookup data) implements Model {

    public JKModel {
        if (!data.locked()) {
            Log.internal(new IllegalStateException("Model not locked"));
            throw new Log.KarinaException();
        }
    }

    @Override
    public @Nullable ClassPointer getClassPointer(Region region, ObjectPath objectPath) {
        if (this.data.contains(objectPath)) {
            return ClassPointer.of(region, objectPath);
        }
        return null;
    }

    @Override
    public ClassModel getClass(ClassPointer pointer) {
        var sample = Log.addSuperSample("GET_CLASS");
        var classModel = this.data.get(pointer.path());

        if (classModel == null) {
            Log.temp(pointer.region(), "Class not found, this should not happen: " + pointer.path());
            throw new Log.KarinaException();
        }
        sample.endSample();
        return classModel;
    }

    @Override
    public @Nullable ClassModel getClassNullable(ClassPointer pointer) {
        return this.data.get(pointer.path());
    }


    @Override
    public MethodModel getMethod(MethodPointer pointer) {

        var erased = pointer.erasedParameters();

        var classModel = getClass(pointer.classPointer());
        for (var method : classModel.methods()) {
            if (!method.name().equals(pointer.name())) {
                continue;
            }
            var interErased = method.erasedParameters();
            if (Types.signatureEquals(erased, interErased)) {
                return method;
            }
        }
        Log.temp(pointer.region(), "Method not found, this should not happen: " + pointer);
        throw new Log.KarinaException();
    }

    @Override
    public FieldModel getField(FieldPointer pointer) {
        var classModel = getClass(pointer.classPointer());
        for (var field : classModel.fields()) {
            if (field.name().equals(pointer.name())) {
                return field;
            }
        }
        Log.temp(pointer.region(), "Field not found, this should not happen: " + pointer);
        throw new Log.KarinaException();
    }


    @Override
    public int getClassCount() {
        return this.data.count();
    }

    @Override
    public List<KClassModel> getUserClasses() {
        return this.data.userClasses();
    }

    @Override
    public List<JClassModel> getBinaryClasses() {
        return this.data.binaryClasses();
    }


}
