package org.karina.lang.compiler.stages;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;

public record TypeChecking(KTree.KPackage root) {

    //Returns a common type of two types
    public @Nullable KType superType(Span checkingRegion, KType a, KType b) {
        if (canAssign(checkingRegion, a, b, false)) {
            return a;
        } else if (canAssign(checkingRegion, b, a, false)) {
            return b;
        } else {
            return null;
        }
    }

    public boolean isVoid(KType type) {
        if (type instanceof KType.PrimitiveType primitiveType) {
            return primitiveType.primitive() == KType.KPrimitive.VOID;
        } else {
            return false;
        }
    }
    public void assign(Span checkingRegion, KType left, KType right) {
        if (!canAssign(checkingRegion, left, right, false)) {
            Log.attribError(new AttribError.TypeMismatch(checkingRegion, left, right));
            throw new Log.KarinaException();
        }
        canAssign(checkingRegion, left, right, true);
    }

    public boolean canAssign(Span checkingRegion, KType left, KType right, boolean mutable) {
        if (left instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                return canAssign(checkingRegion, resolvable.get(), right, mutable);
            } else {
                var canResolve = resolvable.canResolve(checkingRegion, right);
                if (mutable && canResolve) {
                    resolvable.tryResolve(right);
                }
                return canResolve;
            }
        } else if (right instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                return canAssign(checkingRegion, left, resolvable.get(), mutable);
            } else {
                var canResolve = resolvable.canResolve(checkingRegion, left);
                if (mutable && canResolve) {
                    resolvable.tryResolve(left);
                }
                return canResolve;
            }
        }

        return switch (left) {
            case KType.ArrayType arrayType -> {
                if (right instanceof KType.ArrayType rightArrayType) {
                    yield canAssign(checkingRegion, arrayType.elementType(), rightArrayType.elementType(), mutable);
                } else {
                    yield false;
                }
            }
            case KType.ClassType classType -> {
                if (right instanceof KType.ClassType rightClassType) {
                    if (!classType.path().equals(rightClassType.path())) {
                        yield false;
                    }
                    if (rightClassType.generics().size() != classType.generics().size()) {
                        //should not happen
                        yield false;
                    }

                    for (var i = 0; i < rightClassType.generics().size(); i++) {
                        if (!canAssign(checkingRegion, classType.generics().get(i), rightClassType.generics().get(i), mutable)) {
                            yield false;
                        }
                    }

                    yield true;

                } else {
                    yield false;
                }
            }
            case KType.FunctionType functionType -> {
                if (right instanceof KType.FunctionType rightFunctionType) {
                    if (functionType.arguments().size() != rightFunctionType.arguments().size()) {
                        yield false;
                    }
                    for (int i = 0; i < functionType.arguments().size(); i++) {
                        if (!canAssign(checkingRegion, functionType.arguments().get(i), rightFunctionType.arguments().get(i), mutable)) {
                            yield false;
                        }
                    }
                    var leftReturnType = functionType.returnType();
                    if (leftReturnType == null) {
                        leftReturnType = new KType.PrimitiveType.VoidType(functionType.region());
                    }
                    var rightReturnType = rightFunctionType.returnType();
                    if (rightReturnType == null) {
                        rightReturnType = new KType.PrimitiveType.VoidType(rightFunctionType.region());
                    }
                    yield canAssign(checkingRegion, leftReturnType, rightReturnType, mutable);
                } else {
                    yield false;
                }
            }
            case KType.GenericLink genericLink -> {
                yield right instanceof KType.GenericLink rightGenericLink
                    && genericLink.link().equals(rightGenericLink.link());
            }
            case KType.PrimitiveType primitiveType -> {
                yield right instanceof KType.PrimitiveType rightPrimitiveType
                     && primitiveType.primitive() == rightPrimitiveType.primitive();
            }
            case KType.UnprocessedType unprocessedType -> false;
            case KType.Resolvable resolvable -> false;
        };
    }


}
