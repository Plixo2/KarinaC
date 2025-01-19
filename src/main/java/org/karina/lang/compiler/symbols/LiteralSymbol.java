package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

public sealed interface LiteralSymbol {
    Span region();

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

    record StaticFunction(Span region, ObjectPath path) implements LiteralSymbol { }

    record VariableReference(Span region, Variable variable) implements LiteralSymbol { }

    record StructReference(Span region, ObjectPath path) implements LiteralSymbol { }

    record InterfaceReference(Span region, ObjectPath path) implements LiteralSymbol { }

}
