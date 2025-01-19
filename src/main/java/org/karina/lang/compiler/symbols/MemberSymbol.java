package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KType;

public sealed interface MemberSymbol {
    KType type();

    record ArrayLength(Span region) implements MemberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.INT);
        }
    }
    record FieldSymbol(KType type, ObjectPath fieldPath, String name, KType owner) implements MemberSymbol {}
    record InterfaceFunctionSymbol(Span region, KType.ClassType classType, ObjectPath path) implements MemberSymbol {
        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.VOID);
        }
    }
    record VirtualFunctionSymbol(Span region, KType.ClassType classType, ObjectPath path) implements MemberSymbol {

        @Override
        public KType type() {
            return new KType.PrimitiveType(KType.KPrimitive.VOID);
        }
    }

}
