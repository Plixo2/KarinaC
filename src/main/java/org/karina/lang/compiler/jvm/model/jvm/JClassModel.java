package org.karina.lang.compiler.jvm.model.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.ClassModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.ObjectPath;

@AllArgsConstructor
public class JClassModel implements ClassModel {
    private String name;
    private ObjectPath path;
    private int modifiers;
    private ClassPointer superClass;
    private ImmutableList<ClassPointer> interfaces;
    private ImmutableList<ClassPointer> innerClasses;
    private ImmutableList<FieldModel> fields;
    private ImmutableList<MethodModel> methods;
    private TextSource resource;

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
    public ImmutableList<ClassPointer> interfaces() {
        return this.interfaces;
    }

    @Override
    public ImmutableList<ClassPointer> innerClasses() {
        return this.innerClasses;
    }

    @Override
    public ImmutableList<FieldModel> fields() {
        return this.fields;
    }

    @Override
    public ImmutableList<MethodModel> methods() {
        return this.methods;
    }

    @Override
    public TextSource resource() {
        return this.resource;
    }

    @Override
    public String toString() {
        return "ClassModelNode{" + "path=" + this.path.mkString("/") + '}';
    }
}
