package org.karina.lang.compiler.jvm.signature.model;

import java.util.List;

public record ClassSignature(
        List<TypeParameter> generics,
        TypeSignature.ClassTypeSignature superClass,
        List<TypeSignature.ClassTypeSignature> interfaces
) { }
