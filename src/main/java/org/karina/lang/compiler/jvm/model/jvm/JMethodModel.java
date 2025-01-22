package org.karina.lang.compiler.jvm.model.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

import java.util.Objects;

@AllArgsConstructor
public class JMethodModel implements MethodModel {
    private String name;
    private int modifiers;
    private Signature signature;
    private ImmutableList<String> parameters;
    private ImmutableList<Generic> generics;
    private @Nullable KExpr expression;
    private Region region;
    private ClassPointer classPointer;

    @Override
    public int modifiers() {
        return this.modifiers;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ImmutableList<String> parameters() {
        return this.parameters;
    }

    @Override
    public Signature signature() {
        return this.signature;
    }

    @Override
    public ImmutableList<Generic> generics() {
        return this.generics;
    }

    @Override
    public @Nullable KExpr expression() {
        return this.expression;
    }

    @Override
    public Region region() {
        return this.region;
    }

    @Override
    public MethodPointer pointer() {
        return MethodPointer.of(this.classPointer, this.name, this.signature);
    }

    @Override
    public ClassPointer classPointer() {
        return this.classPointer;
    }



    public int hashCodeExpensive() {
        return Objects.hash(
                this.name, this.modifiers, this.signature, this.parameters, this.generics, this.expression, this.region,
                this.classPointer
        );
    }
}
