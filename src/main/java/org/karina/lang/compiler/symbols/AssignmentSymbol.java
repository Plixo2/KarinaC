package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Variable;

public sealed interface AssignmentSymbol {

    record LocalVariable(Variable variable) implements AssignmentSymbol { }

    record ArrayElement(KExpr array, KExpr index, KType elementType) implements AssignmentSymbol { }

    record Field(KExpr object, FieldPointer pointer, KType fieldType, KType fieldOwner) implements AssignmentSymbol { }
    record StaticField(FieldPointer pointer, KType fieldType) implements AssignmentSymbol { }


}
