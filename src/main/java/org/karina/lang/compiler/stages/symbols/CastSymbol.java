package org.karina.lang.compiler.stages.symbols;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.objects.KType;

@AllArgsConstructor
public class CastSymbol {

    KType.PrimitiveType fromNumeric;
    KType.PrimitiveType toNumeric;

    public KType type() {
        return this.toNumeric;
    }
}
