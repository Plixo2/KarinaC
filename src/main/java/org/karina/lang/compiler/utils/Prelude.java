package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;

import java.lang.reflect.Modifier;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Prelude {
    private ImmutableList<ClassPointer> classes;
    private ImmutableList<FieldPointer> staticFields;
    private ImmutableList<MethodPointer> staticMethods;

    public static Prelude fromModel(Model model) {

        var classes = ImmutableList.<ClassPointer>builder();
        for (var entry : model.getBinaryClasses()) {
            if (ClassPointer.shouldIncludeInPrelude(entry.path())) {
                classes.add(entry.pointer());
            }
        }

        var fields = ImmutableList.<FieldPointer>builder();
//        var valuesPath = new ObjectPath("karina", "lang", "Values");
//        putStaticFieldFromKarinaClass(model, valuesPath, "NULL_VALUE", fields);

        var methods = ImmutableList.<MethodPointer>builder();

        var consolePath = new ObjectPath("karina", "lang", "Console");
        putAllMethodsFromKarinaClass(model, consolePath, methods);


        return new Prelude(classes.build(), fields.build(), methods.build());
    }


    public void log() {
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
    }

    private static void putAllMethodsFromKarinaClass(Model model, ObjectPath path,  ImmutableList.Builder<MethodPointer> collection) {
        var classPointer = model.getClassPointer(KType.KARINA_LIB, path);

        if (classPointer == null) {
            Log.bytecode(KType.KARINA_LIB, path.mkString("/"), "Build-in Karina class not found");
            throw new Log.KarinaException();
        }

        var classModel = model.getClass(classPointer);
        for (var method : classModel.methods()) {
            if (Modifier.isStatic(method.modifiers()) && Modifier.isPublic(method.modifiers())) {
                collection.add(method.pointer());
            }
        }
    }

    private static void putStaticFieldFromKarinaClass(Model model, ObjectPath path, String name,  ImmutableList.Builder<FieldPointer> collection) {
        var classPointer = model.getClassPointer(KType.KARINA_LIB, path);

        if (classPointer == null) {
            Log.bytecode(KType.KARINA_LIB, path.mkString("/"), "Build-in Karina class not found");
            throw new Log.KarinaException();
        }

        var classModel = model.getClass(classPointer);
        var fieldPointer = classModel.getField(
                name, fieldModel -> Modifier.isStatic(fieldModel.modifiers()) &&
                        Modifier.isPublic(fieldModel.modifiers())
        );
        if (fieldPointer == null) {
            Log.bytecode(KType.KARINA_LIB, path.append(name).mkString("."), "Build-in Karina field not found");
            throw new Log.KarinaException();
        }

        collection.add(fieldPointer);

    }

}
