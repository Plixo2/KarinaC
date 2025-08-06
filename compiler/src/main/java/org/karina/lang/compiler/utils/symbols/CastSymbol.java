package org.karina.lang.compiler.utils.symbols;

import org.karina.lang.compiler.utils.KType;


public sealed interface CastSymbol {

    record PrimitiveCast(KType.KPrimitive fromNumeric, KType.KPrimitive toNumeric) implements CastSymbol { }
    record UpCast(KType fromType, KType toType) implements CastSymbol { }

    default KType type() {
        switch (this) {
            case UpCast upCast -> {
                return upCast.toType();
            }
            case PrimitiveCast primitiveCast -> {
                return new KType.PrimitiveType(primitiveCast.toNumeric());
            }
        }
    }
}
