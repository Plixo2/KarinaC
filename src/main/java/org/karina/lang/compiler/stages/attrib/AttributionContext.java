package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.Variable;
import org.karina.lang.compiler.VariableCollection;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.SymbolTable;

import java.util.Objects;

// Method scope is the enclosing method or function expression
record AttributionContext(
        KTree.KPackage root,
        @Nullable KType selfType,
        Span methodRegion,
        KType returnType,
        VariableCollection collection,
        SymbolTable table
) {

    AttributionContext addVariable(Variable variable) {
        if (this.collection.contains(variable.name())) {
            var existingVariable = Objects.requireNonNull(this.collection.get(variable.name()));
            Log.attribError(new AttribError.DuplicateVariable(
                    existingVariable.region(),
                    variable.region(),
                    variable.name()
            ));
            throw new Log.KarinaException();
        }
        return new AttributionContext(
                this.root,
                this.selfType,
                this.methodRegion,
                this.returnType,
                this.collection.add(variable),
                this.table
        );
    }

}
