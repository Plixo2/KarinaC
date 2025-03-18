package org.karina.lang.compiler.jvm.model;

import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.HashMap;
import java.util.Map;

//TODO replace, to slow
/**
 * A Thread Safe builder for a JKModel
 */
public class JKModelBuilder {

    PhaseDebug phase;
    Map<ObjectPath, ClassModel> classModels = new HashMap<>();

    public JKModelBuilder(PhaseDebug phase) {
        this.phase = phase;
    }

    //TODO replace, to slow
    public void addClass(ClassModel classModel) {
        if (classModel.phase() != PhaseDebug.JVM && this.phase != PhaseDebug.JVM) {
            if (this.phase != classModel.phase()) {
                var msg = "Class added to JKModel with wrong phase: " + classModel.path();
                Log.temp(classModel.region(), msg);
                throw new Log.KarinaException();
            }
        }

        if (this.classModels.containsKey(classModel.path())) {
            var msg = "Class already exists: " + classModel.path();
            Log.bytecode(classModel.resource(), classModel.name(), msg);
            throw new Log.KarinaException();
        }
        this.classModels.put(classModel.path(), classModel);
    }

    public void addClassWithChildren(ClassModel classModel) {
        if (this.classModels.containsKey(classModel.path())) {
            var msg = "Class already exists: " + classModel.path();
            Log.bytecode(classModel.resource(), classModel.name(), msg);
            throw new Log.KarinaException();
        }
        this.classModels.put(classModel.path(), classModel);

        classModel.innerClasses().forEach(this::addClassWithChildren);

    }

    public JKModel build() {
        if (this.phase != PhaseDebug.JVM) {
            for (var classModel : this.classModels.values()) {
                if (classModel.phase() == PhaseDebug.JVM) {
                    continue;
                }
                if (this.phase != classModel.phase()) {
                    var msg = "Class added to JKModel with wrong phase: " + classModel.path();
                    Log.temp(classModel.region(), msg);
                    throw new Log.KarinaException();
                }
                if (classModel.phase() == PhaseDebug.IMPORTED && classModel instanceof KClassModel kClassModel) {
                    checkIfImported(kClassModel);
                }
            }
        }

        return new JKModel(this.phase, this.classModels);
    }

    private void checkIfImported(KClassModel model) {
        for (var method : model.methods()) {
            for (var parameter : method.signature().parameters()) {
                if (parameter instanceof KType.UnprocessedType unprocessedType) {
                    Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                    throw new Log.KarinaException();
                }
            }
        }

    }

}
