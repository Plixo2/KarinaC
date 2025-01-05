package org.karina.lang.compiler.stages;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;

public record TypeChecking(KTree.KPackage root) {

    //Returns a common type of two types
    public @Nullable KType superType(KType a, KType b) {
        if (canAssign(a, b, false)) {
            return a;
        } else if (canAssign(b, a, false)) {
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
    public void assign(Span region, KType left, KType right) {
        if (!canAssign(left, right, false)) {
            Log.attribError(new AttribError.TypeMismatch(region, left, right));
            throw new Log.KarinaException();
        }
        canAssign(left, right, true);
    }

    public boolean canAssign(KType left, KType right, boolean mutable) {
        if (left instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                return canAssign(resolvable.get(), right, mutable);
            } else {
                if (mutable) {
                    resolvable.resolve(right);
                }
                return true;
            }
        } else if (right instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                return canAssign(left, resolvable.get(), mutable);
            } else {
                if (mutable) {
                    resolvable.resolve(left);
                }
                return true;
            }
        }

        return switch (left) {
            case KType.ArrayType arrayType -> {
                if (right instanceof KType.ArrayType rightArrayType) {
                    yield canAssign(arrayType.elementType(), rightArrayType.elementType(), mutable);
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
                        if (!canAssign(classType.generics().get(i), rightClassType.generics().get(i), mutable)) {
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
                        if (!canAssign(functionType.arguments().get(i), rightFunctionType.arguments().get(i), mutable)) {
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
                    yield canAssign(leftReturnType, rightReturnType, mutable);
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
