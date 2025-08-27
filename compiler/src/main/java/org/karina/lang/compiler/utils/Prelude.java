package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;

import java.lang.reflect.Modifier;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Prelude {
    private ImmutableList<ClassPointer> classes;
    private ImmutableList<FieldPointer> staticFields;
    private ImmutableList<MethodPointer> staticMethods;

    public static Prelude fromModel(Context c, Model model) {

        var classes = ImmutableList.<ClassPointer>builder();
        for (var entry : model.getBinaryClasses()) {
            if (entry.outerClass() != null) {
                // only outermost classes
                continue;
            }
            var path = entry.path();
            if (path.size() != 3) {
                continue;
            }

            if (path.startsWith("java", "lang") || path.startsWith("karina", "lang")) {
                classes.add(entry.pointer());
            }
        }
        classes.add(ClassPointer.of(KType.KARINA_LIB, ClassPointer.OPTION_NONE_PATH));
        classes.add(ClassPointer.of(KType.KARINA_LIB, ClassPointer.OPTION_SOME_PATH));
        classes.add(ClassPointer.of(KType.KARINA_LIB, ClassPointer.RESULT_ERR_PATH));
        classes.add(ClassPointer.of(KType.KARINA_LIB, ClassPointer.RESULT_OK_PATH));

        var fields = ImmutableList.<FieldPointer>builder();

        var methods = ImmutableList.<MethodPointer>builder();

        var consolePath = new ObjectPath("karina", "lang", "Console");
        putAllMethodsFromKarinaClass(c, model, consolePath, methods);
        var rangePath = new ObjectPath("karina", "lang", "Range");
        putAllMethodsFromKarinaClass(c, model, rangePath, methods);

        

        return new Prelude(classes.build(), fields.build(), methods.build());
    }


    public void log() {
        Log.beginType(Log.LogTypes.IMPORT_PRELUDE, "prelude");
        if (Log.LogTypes.IMPORTS.isVisible()) {

            Log.begin("Classes");
            for (var entry : this.classes) {
                Log.record(entry);
            }
            Log.end("Classes");

            Log.begin("Fields");
            for (var entry : this.staticFields) {
                Log.record(entry);
            }
            Log.end("Fields");

            Log.begin("Methods");
            for (var entry : this.staticMethods) {
                Log.record(entry);
            }
            Log.end("Methods");

        }
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, "importing prelude with ");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, classes().size() + " classes");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, staticFields().size() + " static fields");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, staticMethods().size() + " static methods");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, (classes().size() + staticFields().size() + staticMethods().size()) + " items");

        Log.endType(Log.LogTypes.IMPORT_PRELUDE, "prelude");


    }

    /**
     * Import all public static methods from a given class
     */
    private static void putAllMethodsFromKarinaClass(Context c, Model model, ObjectPath path,  ImmutableList.Builder<MethodPointer> collection) {
        var classPointer = model.getClassPointer(KType.KARINA_LIB, path);

        if (classPointer == null) {
            Log.bytecode(c, KType.KARINA_LIB, path.mkString("/"), "Build-in Karina class not found");
            throw new Log.KarinaException();
        }

        var classModel = model.getClass(classPointer);
        for (var method : classModel.methods()) {
            if (Modifier.isStatic(method.modifiers()) && Modifier.isPublic(method.modifiers())) {
                collection.add(method.pointer());
            }
        }
    }

    private static void putStaticFieldFromKarinaClass(Context c, Model model, ObjectPath path, String name,  ImmutableList.Builder<FieldPointer> collection) {
        var classPointer = model.getClassPointer(KType.KARINA_LIB, path);

        if (classPointer == null) {
            Log.bytecode(c, KType.KARINA_LIB, path.mkString("/"), "Build-in Karina class not found");
            throw new Log.KarinaException();
        }

        var classModel = model.getClass(classPointer);
        var fieldPointer = classModel.getField(
                name, fieldModel -> Modifier.isStatic(fieldModel.modifiers()) &&
                        Modifier.isPublic(fieldModel.modifiers())
        );
        if (fieldPointer == null) {
            Log.bytecode(c, KType.KARINA_LIB, path.append(name).mkString("."), "Build-in Karina field not found");
            throw new Log.KarinaException();
        }

        collection.add(fieldPointer);

    }

}
