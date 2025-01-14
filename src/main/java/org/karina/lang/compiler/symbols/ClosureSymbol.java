package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

public record ClosureSymbol(KType.FunctionType functionType, List<Variable> captures, boolean captureSelf) {

    public KType type() {
        return this.functionType;
    }
}
