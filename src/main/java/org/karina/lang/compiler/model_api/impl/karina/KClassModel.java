package org.karina.lang.compiler.model_api.impl.karina;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KAnnotation;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.stages.imports.ImportTable;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.KType;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class KClassModel implements ClassModel {
    private final String name;
    private final ObjectPath path;
    private final int modifiers;
    private final KType.ClassType superClass;
    private final @Nullable KClassModel outerClass;
    private final ImmutableList<KType.ClassType> interfaces;
    private final List<KClassModel> innerClasses;
    private final ImmutableList<KFieldModel> fields;
    private final List<KMethodModel> methods;
    private final ImmutableList<Generic> generics;
    private final ImmutableList<KImport> imports;
    private final List<ClassPointer> permittedSubclasses;
    private final ArrayList<ClassPointer> nestMembers;
    private final ImmutableList<KAnnotation> annotations;
    private final TextSource resource;

    //full region covering the whole unit/struct
    private final Region region;

    @Getter
    @Accessors(fluent = true)
    private final @Nullable @Symbol ImportTable symbolTable;


    @Override
    public Region region() {
        return this.region;
    }

    public ImmutableList<KImport> imports() {
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
    public KType.ClassType superClass() {
        return this.superClass;
    }

    @Override
    public @Nullable KClassModel outerClass() {
        return this.outerClass;
    }

    @Override
    public ImmutableList<KType.ClassType> interfaces() {
        return this.interfaces;
    }

    @Override
    public ImmutableList<KClassModel> innerClasses() {
        return ImmutableList.copyOf(this.innerClasses);
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
        return ImmutableList.copyOf(this.methods);
    }

    @Override
    public ImmutableList<ClassPointer> permittedSubclasses() {
        return ImmutableList.copyOf(this.permittedSubclasses);
    }

    @Override
    public ImmutableList<ClassPointer> nestMembers() {
        return ImmutableList.copyOf(this.nestMembers);
    }


    @Override
    public TextSource resource() {
        return this.resource;
    }

    private ClassPointer memPtr;
    @Override
    public ClassPointer pointer() {
        if (this.memPtr == null) {
            this.memPtr = ClassPointer.of(this.region, this.path);
        }
        return this.memPtr;
    }

    public ImmutableList<KAnnotation> annotations() {
        return this.annotations;
    }

    public void updateNestMembers(List<ClassPointer> pointers) {
//        this.nestMembers.clear();
        for (var pointer : pointers) {
            if (pointer.equals(this.pointer())) {
                continue;
            }
            this.nestMembers.add(pointer);
        }
    }


    public KType.ClassType getDefaultClassType() {
        var generics = this.generics.stream()
                .map(ref -> (KType) new KType.GenericLink(ref)).toList();

        return new KType.ClassType(this.pointer(), generics);
    }


    @Override
    public String toString() {
        return "KarinaClassModel{" + "path=" + this.path.mkString(".") + '}';
    }


}
