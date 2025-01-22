package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

public sealed interface MemberSymbol {
    KType type();

    record ArrayLength(Region region) implements MemberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.INT);
        }
    }
    record FieldSymbol(KType type, ObjectPath fieldPath, String name, KType owner) implements MemberSymbol {}
    record InterfaceFunctionSymbol(Region region, KType.ClassType classType, ObjectPath path) implements MemberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.VOID);
        }
    }
    record VirtualFunctionSymbol(Region region, KType.ClassType classType, ObjectPath path) implements MemberSymbol {

        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.VOID);
        }
    }

}
