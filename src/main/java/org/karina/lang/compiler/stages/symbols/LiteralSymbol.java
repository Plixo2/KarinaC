package org.karina.lang.compiler.stages.symbols;

import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;

public sealed interface LiteralSymbol {
    Span region();

    /*
     * Return void for static functions, they don't have a type directly
     */
    default KType type() {
        switch (this) {
            case StaticFunction staticFunction -> {
                return new KType.PrimitiveType.VoidType(this.region());
            }
            case VariableReference variableReference -> {
                return variableReference.variable().type();
            }
            case StructReference structReference -> {
                return new KType.PrimitiveType.VoidType(this.region());
            }
            case InterfaceReference interfaceReference -> {
                return new KType.PrimitiveType.VoidType(this.region());
            }
        }
    }

    record StaticFunction(Span region, ObjectPath path) implements LiteralSymbol { }

    record VariableReference(Span region, Variable variable) implements LiteralSymbol { }

    record StructReference(Span region, ObjectPath path) implements LiteralSymbol { }

    record InterfaceReference(Span region, ObjectPath path) implements LiteralSymbol { }

}
