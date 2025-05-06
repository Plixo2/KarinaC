package org.karina.lang.compiler.model_api;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;

public record Signature(ImmutableList<KType> parameters, KType returnType) {

    @Override
    public String toString() {
        var paramString = String.join(", ", this.parameters.stream().map(KType::toString).toList());
        return "(" + paramString + ") -> " + this.returnType;
    }

    public ImmutableList<KType> parametersErased() {
        return ImmutableList.copyOf(parameters().stream().map(Types::erase).toList());
    }

    public static Signature emptyArgs(KType returnType) {
        return new Signature(ImmutableList.of(), returnType);
    }
}
