package org.karina.lang.compiler.jvm.model.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

import java.util.Objects;

@AllArgsConstructor
public class JMethodModel implements MethodModel {
    private final String name;
    private final int modifiers;
    private final Signature signature;
    private final ImmutableList<String> parameters;
    private final ImmutableList<Generic> generics;
    private final @Nullable KExpr expression;
    private final Region region;
    private final ClassPointer classPointer;

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
        return MethodPointer.of(this.region, this.classPointer, this.name, this.signature);
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
