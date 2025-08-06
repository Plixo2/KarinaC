package org.karina.lang.compiler.jvm_loading.signature.model;

import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;

import java.util.List;

public sealed interface TypeSignature
        permits TypeSignature.ArraySignature, TypeSignature.BaseSignature,
        TypeSignature.ClassTypeSignature, TypeSignature.TypeVarSignature {


    record ClassTypeSignature(
            ClassPointer classPointer,
            List<TypeArgument> typeArguments,
            List<SubClassSignature> subClassSignatures
    ) implements TypeSignature {
        public record SubClassSignature(String name, List<TypeArgument> typeArguments) { }
    }

    record ArraySignature(TypeSignature componentType) implements TypeSignature {
    }

    record BaseSignature(KType.KPrimitive primitive) implements TypeSignature {
    }

    record TypeVarSignature(String name) implements TypeSignature {
    }
}
