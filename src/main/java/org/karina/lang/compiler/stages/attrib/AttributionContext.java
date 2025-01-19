package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.SymbolTable;
import org.karina.lang.compiler.utils.TypeChecking;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.utils.VariableCollection;
import org.karina.lang.compiler.utils.Span;

import java.util.Objects;

// Method scope is the enclosing method or function expression
public record AttributionContext(
        KTree.KPackage root,
        @Nullable Variable selfType,
        boolean isLoop,
        Span methodRegion,
        @NotNull KType returnType,
        VariableCollection variables,
        SymbolTable table,
        TypeChecking checking
) {

    boolean isVoid(KType type) {
        return this.checking.isVoid(type);
    }

    @Nullable
    public KType getSuperType(Span checkingRegion, KType a, KType b) {
        return this.checking.superType(checkingRegion, a, b);
    }

    public boolean isPrimitive(@Nullable KType type, KType.KPrimitive primitiveType) {
        if (type instanceof KType.PrimitiveType primitive) {
            return primitive.primitive() == primitiveType;
        }
        return false;
    }

//    public boolean canAssign(KType left, KType right, boolean mutable) {
//        return this.checking.canAssign(left, right, mutable);
//    }

    public void assign(Span region, KType left, KType right) {
        this.checking.assign(region, left, right);
    }

    public boolean canAssign(Span region, KType left, KType right, boolean mutable) {
        return this.checking.canAssign(region, left, right, mutable);
    }

    public AttributionContext setInLoop(boolean isLoop) {
        return new AttributionContext(
                this.root,
                this.selfType,
                isLoop,
                this.methodRegion,
                this.returnType,
                this.variables,
                this.table,
                this.checking
        );
    }

    public AttributionContext addVariable(Variable variable) {
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
                this.isLoop,
                this.methodRegion,
                this.returnType,
                this.variables.add(variable),
                this.table,
                this.checking
        );
    }

    public AttributionContext markImmutable(Variable variable) {
        return new AttributionContext(
                this.root,
                this.selfType,
                this.isLoop,
                this.methodRegion,
                this.returnType,
                this.variables.markImmutable(variable),
                this.table,
                this.checking
        );
    }

    public KTree.KStruct getStruct(Span region, KType type) {

        if (type instanceof KType.ClassType classType) {
            var item = KTree.findAbsolutItem(this.root, classType.path());
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
