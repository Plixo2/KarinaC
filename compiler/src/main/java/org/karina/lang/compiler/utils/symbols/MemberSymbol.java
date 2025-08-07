package org.karina.lang.compiler.utils.symbols;

import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.utils.MethodCollection;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.KType;

public sealed interface MemberSymbol {
    KType type();

    record ArrayLength(Region region) implements MemberSymbol {
        @Override
        public KType type() {
            return KType.INT;
        }
    }
    record FieldSymbol(FieldPointer pointer, KType type, KType.ClassType classType) implements MemberSymbol { }
    record VirtualFunctionSymbol(Region region, KType.ClassType classType, MethodCollection collection) implements MemberSymbol {

        @Override
        public KType type() {
            return KType.NONE;
        }
    }

}
