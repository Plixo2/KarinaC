package org.karina.compiler.jvm;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.compiler.Signature;
import org.karina.compiler.model.MethodModel;
import org.karina.compiler.model.ModelParameter;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;

@AllArgsConstructor
public class MethodModelNode implements MethodModel {
    private String name;
    private int modifiers;
    private Signature signature;
    private ImmutableList<String> parameters;
    private ImmutableList<Generic> generics;
    private @Nullable KExpr expression;


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
}
