package org.karina.lang.compiler.model_api.impl.karina;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.KAnnotation;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

public class KMethodModel implements MethodModel {
    private final String name;
    private final int modifiers;
    private final Signature signature;
    private final ImmutableList<String> parameters;
    private final ImmutableList<KType> erasedParameters;
    private final ImmutableList<Generic> generics;
    @Setter
    private @Nullable KExpr expression;
    private final ImmutableList<KAnnotation> annotations;
    private final Region region;
    private final ClassPointer classPointer;
    @Getter
    private final List<Variable> paramVariables;

//    @Getter
//    private @Nullable KType overrideOf;

    public KMethodModel(
            String name, int modifiers, Signature signature, ImmutableList<String> parameters,
            ImmutableList<Generic> generics, @Nullable KExpr expression,
            ImmutableList<KAnnotation> annotations, Region region, ClassPointer classPointer, List<Variable> paramVariables
    ) {
        this.name = name;
        this.modifiers = modifiers;
        this.signature = signature;
        this.parameters = parameters;
        this.generics = generics;
        this.expression = expression;
        this.annotations = annotations;
        this.region = region;
        this.classPointer = classPointer;
        this.erasedParameters = signature.parametersErased();
        this.paramVariables = paramVariables;
    }

    @Override
    public Region region() {
        return this.region;
    }

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
    public ImmutableList<KType> erasedParameters() {
        return this.erasedParameters;
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
    public MethodPointer pointer() {
        return MethodPointer.of(
                this.region,
                this.classPointer,
                this.name,
                this.erasedParameters,
                this.signature.returnType()
        );
    }

    public ImmutableList<KAnnotation> annotations() {
        return this.annotations;
    }

    @Override
    public ClassPointer classPointer() {
        return this.classPointer;
    }
}
