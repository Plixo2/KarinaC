package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

public sealed interface LiteralSymbol {
    Region region();

    /*
     * Return void for static functions, they don't have a type directly
     */
    default KType type() {
        switch (this) {
            case StaticFunction staticFunction -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case VariableReference variableReference -> {
                return variableReference.variable().type();
            }
            case StructReference structReference -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case InterfaceReference interfaceReference -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
        }
    }

    record StaticFunction(Region region, ObjectPath path) implements LiteralSymbol { }

    record VariableReference(Region region, Variable variable) implements LiteralSymbol { }

    record StructReference(Region region, ObjectPath path) implements LiteralSymbol { }

    record InterfaceReference(Region region, ObjectPath path) implements LiteralSymbol { }

}
