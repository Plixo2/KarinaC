package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.objects.KType;

public sealed interface UnwrapSymbol {
    default KType unpackedType() {
        return switch (this) {
            case UnwrapOptional unwrapOptional -> unwrapOptional.inner;
            case UnwrapResult unwrapResult -> unwrapResult.okType;
        };
    }

    record UnwrapOptional(KType inner) implements UnwrapSymbol {}
    record UnwrapResult(KType okType, KType errorType) implements UnwrapSymbol {}

}
