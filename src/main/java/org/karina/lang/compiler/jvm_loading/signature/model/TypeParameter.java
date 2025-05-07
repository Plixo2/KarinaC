package org.karina.lang.compiler.jvm_loading.signature.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TypeParameter(
        String name,
        @Nullable TypeSignature superClass,
        List<TypeSignature> interfaces
) {
}
