package org.karina.lang.compiler.jvm.model.karina;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.model.ClassModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;


@AllArgsConstructor
public class KClassModel implements ClassModel {
    private String name;
    private ObjectPath path;
    private int modifiers;
    private ClassPointer superClass;
    private @Nullable ClassPointer outerClass;
    private ImmutableList<ClassPointer> interfaces;
    private ImmutableList<ClassPointer> innerClasses;
    private ImmutableList<KFieldModel> fields;
    private ImmutableList<KMethodModel> methods;
    private ImmutableList<Generic> generics;
    private ImmutableList<KTree.KImport> imports;
    private ImmutableList<ClassPointer> permittedSubclasses;
    private TextSource resource;
    private Region region;

    @Override
    public Region region() {
        return this.region;
    }

    public ImmutableList<KTree.KImport> imports() {
        return this.imports;
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
    public ClassPointer superClass() {
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
    public String toString() {
        return "ClassModelNode{" + "path=" + this.path.mkString("/") + '}';
    }
}
