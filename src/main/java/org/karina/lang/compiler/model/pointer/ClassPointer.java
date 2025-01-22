package org.karina.lang.compiler.model.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class ClassPointer {
    public static final ClassPointer ROOT = new ClassPointer(new ObjectPath("java", "lang", "Object"));


    ObjectPath path;

    private ClassPointer(ObjectPath path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ClassPointer{" + "path=" + this.path.mkString("/") + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassPointer that)) {
            return false;
        }
        return Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.path);
    }

    public static ClassPointer of(ObjectPath path) {
        return new ClassPointer(path);
    }
}
