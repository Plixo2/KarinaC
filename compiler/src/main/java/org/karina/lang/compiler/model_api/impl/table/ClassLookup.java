package org.karina.lang.compiler.model_api.impl.table;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.List;

/**
 * Abstraction for a class lookup table.
 * A HashMap should be the fastest, Tries are slower
 * @see ObjectPath
 */
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
