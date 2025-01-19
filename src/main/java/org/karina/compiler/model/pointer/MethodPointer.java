package org.karina.compiler.model.pointer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.compiler.Signature;

@Getter
@Accessors(fluent = true)
public class MethodPointer {
    ClassPointer classPointer;
    String name;
    Signature signature;

    private MethodPointer(ClassPointer classPointer, String name, Signature signature) {
        this.classPointer = classPointer;
        this.name = name;
        this.signature = signature;
    }

    public static MethodPointer of(ClassPointer classPointer, String name, Signature signature) {
        return new MethodPointer(classPointer, name, signature);
    }
}
