package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class MethodPointer {
    Region region;
    ClassPointer classPointer;
    String name;
    Signature signature;

    private MethodPointer(Region region, ClassPointer classPointer, String name, Signature signature) {
        this.region = region;
        this.classPointer = classPointer;
        this.name = name;
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "MethodPointer{" + "signature=" + this.signature + ", name='" + this.name + '\'' +
                ", class=" + this.classPointer + '}';
    }

    public static MethodPointer of(Region region, ClassPointer classPointer, String name, Signature signature) {
        return new MethodPointer(region, classPointer, name, signature);
    }
}
