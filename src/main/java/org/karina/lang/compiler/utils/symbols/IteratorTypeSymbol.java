package org.karina.lang.compiler.utils.symbols;

import org.karina.lang.compiler.utils.Variable;


public sealed interface IteratorTypeSymbol {
    Variable variable();

    record ForArray(Variable variable) implements IteratorTypeSymbol {}
    record ForIterable(Variable variable) implements IteratorTypeSymbol {}
    record ForRange(Variable variable) implements IteratorTypeSymbol {}

}
