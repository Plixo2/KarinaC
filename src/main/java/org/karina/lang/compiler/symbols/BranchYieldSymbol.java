package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.objects.KType;

public sealed interface BranchYieldSymbol {
    default KType type() {
        return switch (this) {
            case YieldValue(var type) -> type;
            case Returns ignored -> KType.VOID;
            case None ignored -> KType.VOID;
        };
    }

    record YieldValue(KType type) implements BranchYieldSymbol {}
    record Returns() implements BranchYieldSymbol {}
    record None() implements BranchYieldSymbol {}
}
