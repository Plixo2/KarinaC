package org.karina.lang.compiler.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.objects.KType;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class CastSymbol {

    KType.PrimitiveType fromNumeric;
    KType.PrimitiveType toNumeric;

    public KType type() {
        return this.toNumeric;
    }
}
