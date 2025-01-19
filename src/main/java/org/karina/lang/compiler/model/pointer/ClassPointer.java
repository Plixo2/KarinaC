package org.karina.lang.compiler.model.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.ObjectPath;

@Getter
@Accessors(fluent = true)

public class ClassPointer {
    ObjectPath path;

    private ClassPointer(ObjectPath path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ClassPointer{" + "path=" + this.path.mkString("/") + '}';
    }

    public static ClassPointer of(ObjectPath path) {
        return new ClassPointer(path);
    }
}
