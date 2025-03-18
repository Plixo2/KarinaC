package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;


public sealed interface IteratorTypeSymbol {
    Variable variable();

    record ForArray(Variable variable, KType.ArrayType arrayType) implements IteratorTypeSymbol {}
    record ForIterable(Variable variable) implements IteratorTypeSymbol {}

}
