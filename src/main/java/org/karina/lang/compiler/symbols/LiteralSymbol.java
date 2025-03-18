package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.MethodCollection;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

public sealed interface LiteralSymbol {
    Region region();

    /*
     * Return void for static functions, they don't have a fieldType directly
     */
    default KType type() {
        switch (this) {
            case StaticMethodReference staticMethodReference -> {
                return KType.VOID;
            }
            case VariableReference variableReference -> {
                return variableReference.variable().type();
            }
            case StaticClassReference staticClassReference -> {
                //todo class return
                return KType.VOID;
            }
            case StaticFieldReference staticFieldReference -> {
                return staticFieldReference.fieldType();
            }
        }
    }

    record StaticMethodReference(Region region, MethodCollection collection) implements LiteralSymbol { }
    record StaticFieldReference(Region region, FieldPointer fieldPointer, KType fieldType) implements LiteralSymbol { }

    record VariableReference(Region region, Variable variable) implements LiteralSymbol { }

    record StaticClassReference(Region region, ClassPointer classPointer) implements LiteralSymbol { }

}
