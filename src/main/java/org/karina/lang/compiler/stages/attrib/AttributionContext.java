package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.SymbolTable;
import org.karina.lang.compiler.stages.TypeChecking;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.VariableCollection;

import java.util.Objects;

// Method scope is the enclosing method or function expression
record AttributionContext(
        KTree.KPackage root,
        @Nullable KType selfType,
        Span methodRegion,
        KType returnType,
        VariableCollection variables,
        SymbolTable table,
        TypeChecking checking
) {

    boolean isVoid(KType type) {
        return this.checking.isVoid(type);
    }

    @Nullable KType getSuperType(KType a, KType b) {
        return this.checking.superType(a, b);
    }

    boolean isPrimitive(@Nullable KType type, KType.KPrimitive primitiveType) {
        if (type instanceof KType.PrimitiveType primitive) {
            return primitive.primitive() == primitiveType;
        }
        return false;
    }

    boolean canAssign(KType left, KType right, boolean mutable) {
        return this.checking.canAssign(left, right, mutable);
    }

    void assign(Span region, KType left, KType right) {
        this.checking.assign(region, left, right);
    }


    AttributionContext addVariable(Variable variable) {
        if (this.variables.contains(variable.name())) {
            var existingVariable = Objects.requireNonNull(this.variables.get(variable.name()));
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
                this.variables.add(variable),
                this.table,
                this.checking
        );
    }

    public KTree.KStruct getStruct(Span region, KType type) {

        if (type instanceof KType.ClassType classType) {
            var item = KTree.findAbsolutItem(this.root, classType.path().value());
            if (!(item instanceof KTree.KStruct struct)) {
                Log.attribError(new AttribError.NotAStruct(
                        region,
                        type
                ));
                throw new Log.KarinaException();
            }
            return struct;
        } else {
            Log.attribError(new AttribError.NotAStruct(
                    region,
                    type
            ));
            throw new Log.KarinaException();
        }

    }

}
