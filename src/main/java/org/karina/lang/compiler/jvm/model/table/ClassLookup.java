package org.karina.lang.compiler.jvm.model.table;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

public interface ClassLookup {

    /**
     * Returns the previous object at the path
     */
    @Nullable ClassModel insert(ObjectPath path, ClassModel object);

    boolean contains(ObjectPath path);

    @Nullable ClassModel get(ObjectPath path);

    ClassLookup lock();
    boolean locked();

    int count();

    List<KClassModel> userClasses();
    List<JClassModel> binaryClasses();

}
