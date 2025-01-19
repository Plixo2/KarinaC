package org.karina.compiler.model.pointer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.ObjectPath;

@Getter
@Accessors(fluent = true)
public class FieldPointer {
    ClassPointer classPointer;
    String fieldName;

    private FieldPointer(ClassPointer classPointer, String fieldName) {
        this.classPointer = classPointer;
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "FieldPointer{" + "classPointer=" + this.classPointer + ", fieldName='" +
                this.fieldName +
                '\'' + '}';
    }

    public static FieldPointer of(ClassPointer classPointer, String fieldName) {
        return new FieldPointer(classPointer, fieldName);
    }
}
