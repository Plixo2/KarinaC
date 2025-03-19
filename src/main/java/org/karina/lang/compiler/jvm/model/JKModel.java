package org.karina.lang.compiler.jvm.model;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.FlightRecorder;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.ImportError;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java-Karina Model
 * Represents ALL class models of the program.
 * @param classModels
 */
public record JKModel(PhaseDebug phase, Map<ObjectPath, ClassModel> classModels) implements Model {

    public JKModel {
        //classModels = ImmutableMap.copyOf(classModels);
    }

    public JKModel(PhaseDebug phase) {
        this(phase, Map.of());
    }

    public static JKModel merge(JKModel... models) {
        var newClassModels = new HashMap<ObjectPath, ClassModel>();

        for (var other : models) {
            for (var entry : other.classModels.entrySet()) {
                if (newClassModels.containsKey(entry.getKey())) {
                    Log.importError(new ImportError.DuplicateItem(
                            newClassModels.get(entry.getKey()).region(),
                            entry.getValue().region(),
                            entry.getKey().toString()
                    ));
                    throw new Log.KarinaException();
                }
                newClassModels.put(entry.getKey(), entry.getValue());
            }
        }
        return new JKModel(PhaseDebug.LOADED, newClassModels);
    }

    @Override
    public @Nullable ClassPointer getClassPointer(Region region, ObjectPath objectPath) {
        if (this.classModels.containsKey(objectPath)) {
            return ClassPointer.of(region, objectPath);
        }
        return null;
    }

    @Override
    public ClassModel getClass(ClassPointer pointer) {
        var sample = Log.addSuperSample("GET_CLASS");
        var classModel = this.classModels.get(pointer.path());

        if (classModel == null) {
            Log.temp(pointer.region(), "Class not found, this should not happen: " + pointer.path());
            throw new Log.KarinaException();
        }
        sample.endSample();
        return classModel;
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

    public List<KClassModel> getUserClasses() {
        return this.classModels.values().stream()
                .filter(c -> c instanceof KClassModel)
                .map(c -> (KClassModel) c)
                .toList();
    }

    public Iterable<JClassModel> getBytecodeClasses() {
        return () -> this.classModels.values().stream()
                .filter(c -> c instanceof JClassModel)
                .map(c -> (JClassModel) c)
                .iterator();
    }

    public int getClassCount() {
        return this.classModels.size();
    }


}
