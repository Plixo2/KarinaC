package org.karina.lang.compiler.utils;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Accessors(fluent = true)
public class UntypedMethodCollection {
    String name;
    ClassPointer classPointer;

    public UntypedMethodCollection(String name, ClassPointer classPointer) {
        this.name = name;
        this.classPointer = classPointer;
    }

    @Override
    public String toString() {
        return "UntypedMethodCollection{" + "name='" + this.name + '\'' + ", class=" +
                this.classPointer + '}';
    }

    public MethodCollection toTypedStaticCollection(ClassPointer referenceSite, Model model) {
        var protection = new ProtectionChecking(model);
        var classModel = model.getClass(this.classPointer);
        var pointer = classModel.methods().stream().filter(ref ->
                ref.name().equals(this.name)
                && Modifier.isStatic(ref.modifiers())
                && protection.isMethodAccessible(model.getClass(referenceSite), ref)
        ).map(MethodModel::pointer).toList();

        return new MethodCollection(this.name, pointer);
    }
}
