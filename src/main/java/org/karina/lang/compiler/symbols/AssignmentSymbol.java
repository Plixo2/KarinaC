package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Variable;

public sealed interface AssignmentSymbol {

    record LocalVariable(Variable variable) implements AssignmentSymbol { }

    record ArrayElement(KExpr array, KExpr index) implements AssignmentSymbol { }

    record Field(KExpr object, ObjectPath fieldPath, String name) implements AssignmentSymbol { }

}
