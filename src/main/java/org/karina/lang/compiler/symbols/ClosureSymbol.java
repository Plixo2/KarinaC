package org.karina.lang.compiler.symbols;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

public record ClosureSymbol(KType.FunctionType functionType, List<Variable> captures, boolean captureSelf, @Nullable Variable self, List<Variable> argVariables) {

    public KType type() {
        return this.functionType;
    }
}
