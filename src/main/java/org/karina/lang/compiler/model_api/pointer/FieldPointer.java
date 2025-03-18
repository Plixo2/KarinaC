package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class FieldPointer {
    ClassPointer classPointer;
    String name;
    Region region;

    private FieldPointer(Region region, ClassPointer classPointer, String name) {
        this.region = region;
        this.classPointer = classPointer;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FieldPointer{" + "classPointer=" + this.classPointer + ", fieldName='" +
                this.name +
                '\'' + '}';
    }

    public static FieldPointer of(Region region, ClassPointer classPointer, String fieldName) {
        return new FieldPointer(region, classPointer, fieldName);
    }
}
