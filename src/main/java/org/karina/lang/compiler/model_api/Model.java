package org.karina.lang.compiler.model_api;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.List;

/**
 * A Model is a collection of classes.
 * This interface is used to get the current underlying representation of classes, fields, and methods.
 *
 * <p>
 * A {@link ClassPointer}/{@link MethodPointer}/{@link FieldPointer} is used to identify a class, method, or field across different stages of the compiler.
 * <p>
 * In every stage of the compiler, a new {@link Model} is created along with new classes, methods, and fields.
 *
 * <p>
 * A Pointer persists across different stages of the compiler and should be always valid.
 */
public interface Model {


    @Nullable ClassPointer getClassPointer(Region region, ObjectPath objectPath);

    ClassModel getClass(ClassPointer pointer);
    @Nullable ClassModel getClassNullable(ClassPointer pointer);
    MethodModel getMethod(MethodPointer model);
    FieldModel getField(FieldPointer pointer);
    int getClassCount();
    List<KClassModel> getUserClasses();
    List<JClassModel> getBinaryClasses();


}
