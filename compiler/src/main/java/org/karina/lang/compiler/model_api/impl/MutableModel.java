package org.karina.lang.compiler.model_api.impl;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.table.ClassLookup;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.List;
import java.util.stream.Stream;

/**
 * TODO replace
 */
public record MutableModel(Model oldLookup, Context c, ClassLookup newLookup) implements Model {

    public MutableModel {
        if (newLookup.locked()) {
            Log.internal(c, new IllegalStateException("New classes are locked"));
            throw new Log.KarinaException();
        }
    }

    @Override
    public @Nullable ClassPointer getClassPointer(Region region, ObjectPath objectPath) {
        var oldPointer = this.oldLookup.getClassPointer(region, objectPath);
        if (oldPointer != null) {
            return ClassPointer.of(region, objectPath);
        }
        if (this.newLookup.contains(objectPath)) {
            return ClassPointer.of(region, objectPath);
        }
        return null;
    }

    @Override
    public ClassModel getClass(ClassPointer pointer) {
        var classModel = this.oldLookup.getClassNullable(pointer);
        if (classModel == null) {
            classModel = this.newLookup.get(pointer.path());
        }

        if (classModel == null) {
            Log.temp(this.c, pointer.region(), "Class not found, this should not happen: " + pointer.path());
            throw new Log.KarinaException();
        }
        return classModel;
    }

    @Override
    public @Nullable ClassModel getClassNullable(ClassPointer pointer) {
        var oldClassModel = this.oldLookup.getClassNullable(pointer);
        if (oldClassModel != null) {
            return oldClassModel;
        }
        return this.newLookup.get(pointer.path());
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
        Log.temp(this.c, pointer.region(), "Method not found, this should not happen: " + pointer);
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
        Log.temp(this.c, pointer.region(), "Field not found, this should not happen: " + pointer);
        throw new Log.KarinaException();
    }


    @Override
    public int getClassCount() {
        return this.oldLookup.getClassCount() + this.newLookup.count();
    }

    @Override
    public List<KClassModel> getUserClasses() {
        return Stream.concat(
                this.oldLookup.getUserClasses().stream(),
                this.newLookup.userClasses().stream()
        ).toList();
    }

    @Override
    public List<JClassModel> getBinaryClasses() {
        return Stream.concat(
                this.oldLookup.getBinaryClasses().stream(),
                this.newLookup.binaryClasses().stream()
        ).toList();
    }

}
