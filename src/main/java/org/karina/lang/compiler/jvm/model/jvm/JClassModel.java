package org.karina.lang.compiler.jvm.model.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@AllArgsConstructor
public class JClassModel implements ClassModel {
    private final String name;
    private final ObjectPath path;
    private final int version;
    private final int modifiers;
    private final @Nullable KType.ClassType superClass;
    private final @Nullable JClassModel outerClass;
    private final ImmutableList<KType.ClassType> interfaces;
    private final List<JClassModel> innerClasses;
    private final List<JFieldModel> fields;
    private final List<JMethodModel> methods;
    private final ImmutableList<Generic> generics;
    private final ImmutableList<ClassPointer> permittedSubclasses;
    private final ImmutableList<ClassPointer> nestMembers;
    private final TextSource resource;
    private final Region region;
    @Getter
    private final ClassNode classNode;

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
    public @Nullable KType.ClassType superClass() {
        return this.superClass;
    }

    @Override
    public @Nullable JClassModel outerClass() {
        return this.outerClass;
    }

    @Override
    public ImmutableList<KType.ClassType> interfaces() {
        return this.interfaces;
    }

    @Override
    public ImmutableList<? extends ClassModel> innerClasses() {
        return ImmutableList.copyOf(this.innerClasses);
    }

    @Override
    public ImmutableList<? extends FieldModel> fields() {
        return ImmutableList.copyOf(this.fields);
    }

    @Override
    public ImmutableList<Generic> generics() {
        return this.generics;
    }

    @Override
    public ImmutableList<? extends MethodModel> methods() {
        return ImmutableList.copyOf(this.methods);
    }

    @Override
    public ImmutableList<ClassPointer> permittedSubclasses() {
        return this.permittedSubclasses;
    }

    @Override
    public ImmutableList<ClassPointer> nestMembers() {
        return this.nestMembers;
    }

    @Override
    public TextSource resource() {
        return this.resource;
    }

    @Override
    public ClassPointer pointer() {
        //OK
        return ClassPointer.of(this.region, this.path);
    }

    @Override
    public Region region() {
        return this.region;
    }

    @Override
    public String toString() {
        return "JavaClassModel{" + "path=" + this.path.mkString(".") + '}';
    }



}
