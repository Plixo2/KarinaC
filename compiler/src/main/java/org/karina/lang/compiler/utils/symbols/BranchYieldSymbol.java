package org.karina.lang.compiler.utils.symbols;

import org.karina.lang.compiler.utils.KType;

public sealed interface BranchYieldSymbol {
    default KType type() {
        return switch (this) {
            case YieldValue(var type) -> type;
            case Returns ignored -> KType.NONE;
            case None ignored -> KType.NONE;
        };
    }

    record YieldValue(KType type) implements BranchYieldSymbol {}
    record Returns() implements BranchYieldSymbol {}
    record None() implements BranchYieldSymbol {}
}
