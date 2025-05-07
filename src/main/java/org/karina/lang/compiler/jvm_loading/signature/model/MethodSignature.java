package org.karina.lang.compiler.jvm_loading.signature.model;


import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MethodSignature(
        List<TypeParameter> generics,
        List<TypeSignature> parameters,
        @Nullable TypeSignature returnSignature //when null, its void,
) {}
