package org.karina.lang.compiler.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.objects.KType;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class CastSymbol {

    KType.KPrimitive fromNumeric;
    KType.KPrimitive toNumeric;

    public KType type() {
        return new KType.PrimitiveType(this.toNumeric);
    }
}
