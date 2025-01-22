package org.karina.lang.compiler.jvm.model.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.ClassModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.Objects;

@AllArgsConstructor
public class JClassModel implements ClassModel {
    private String name;
    private ObjectPath path;
    private int version;
    private int modifiers;
    private @Nullable ClassPointer superClass;
    private @Nullable ClassPointer outerClass;
    private ImmutableList<ClassPointer> interfaces;
    private ImmutableList<ClassPointer> innerClasses;
    private ImmutableList<JFieldModel> fields;
    private ImmutableList<JMethodModel> methods;
    private ImmutableList<Generic> generics;
    private ImmutableList<ClassPointer> permittedSubclasses;
    private TextSource resource;
    private Region region;

    public int version() {
        return this.version;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int modifiers() {
        return this.modifiers;
    }

    @Override
    public ObjectPath path() {
        return this.path;
    }

    @Override
    public @Nullable ClassPointer superClass() {
        return this.superClass;
    }

    @Override
    public @Nullable ClassPointer outerClass() {
        return this.outerClass;
    }

    @Override
    public ImmutableList<ClassPointer> interfaces() {
        return this.interfaces;
    }

    @Override
    public ImmutableList<ClassPointer> innerClasses() {
        return this.innerClasses;
    }

    @Override
    public ImmutableList<? extends FieldModel> fields() {
        return this.fields;
    }

    @Override
    public ImmutableList<Generic> generics() {
        return this.generics;
    }

    @Override
    public ImmutableList<? extends MethodModel> methods() {
        return this.methods;
    }

    @Override
    public ImmutableList<ClassPointer> permittedSubclasses() {
        return this.permittedSubclasses;
    }

    @Override
    public TextSource resource() {
        return this.resource;
    }

    @Override
    public ClassPointer pointer() {
        //OK
        return ClassPointer.of(this.path);
    }

    @Override
    public Region region() {
        return this.region;
    }

    @Override
    public String toString() {
        return "ClassModelNode{" + "path=" + this.path.mkString("/") + '}';
    }



    public int hashCodeExpensive() {
        int expensiveHashField = 0;
        for (var field : this.fields) {
            expensiveHashField = Objects.hash(field.hashCodeExpensive(), expensiveHashField);
        }

        int expensiveHashMethod = 0;
        for (var method : this.methods) {
            expensiveHashMethod = Objects.hash(method.hashCodeExpensive(), expensiveHashMethod);
        }


        return Objects.hash(
                this.name, this.path, this.version, this.modifiers, this.superClass, this.outerClass, this.interfaces,
                this.innerClasses,
                expensiveHashField, expensiveHashMethod, this.generics, this.permittedSubclasses, this.resource, this.region
        );
    }
}
