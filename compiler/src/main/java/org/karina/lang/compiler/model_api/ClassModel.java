package org.karina.lang.compiler.model_api;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.MethodCollection;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.function.Predicate;

public interface ClassModel {
    String name();
    int modifiers();
    ObjectPath path();
    @Nullable KType.ClassType superClass();
    @Nullable ClassModel outerClass();
    @Nullable ClassPointer nestHost();
    ImmutableList<KType.ClassType> interfaces();
    ImmutableList<? extends ClassModel> innerClasses();
    ImmutableList<? extends FieldModel> fields();
    ImmutableList<Generic> generics();
    ImmutableList<? extends MethodModel> methods();
    ImmutableList<ClassPointer> permittedSubclasses();
    ImmutableList<ClassPointer> nestMembers();
    TextSource resource();
    ClassPointer pointer();

    /**
     * Derived from the TextSource, but without a valid line number. (0:0 - 0:0)
     * Should only be used when no other source for a ClassPointer Region is available.
     * Only use this for the importing phase to point to the class declaration
     */
    Region region();


    default boolean isTopLevel() {
        return this.outerClass() == null;
    }

    default KType.ClassType getDefaultClassType() {
        var generics = this.generics().stream()
                                    .map(ref -> (KType) new KType.GenericLink(ref)).toList();

        return new KType.ClassType(this.pointer(), generics);
    }


    default MethodCollection getMethodCollectionShallow(String name) {
        var methods = new ArrayList<MethodPointer>();
        for (var method : methods()) {
            if (method.name().equals(name)) {
                methods.add(method.pointer());
            }
        }
        return new MethodCollection(name, methods);
    }

    default @Nullable FieldPointer getField(String name, Predicate<FieldModel> predicate) {
        for (var field : fields()) {
            if (field.name().equals(name) && predicate.test(field)) {
                return field.pointer();
            }
        }
        return null;
    }
}
