package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;

import java.util.HashMap;

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

    /**
     * Returns true if the right type can be assigned to the left type.
     * Left is the expected type, and right can be a supertype of left.
     * {@code right <: left}
     * <p>
     * Example:
     * {@code let a: left = right}
     */
    public boolean canAssign(Span checkingRegion, KType left, KType right, boolean mutable) {
        if (left instanceof KType.AnyClass && !right.isPrimitiveNonString()) {
            //we check here, we dont want to resolve resolvable types with it
            return true;
        }

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
                    var leftItem = KTree.findAbsolutItem(this.root, classType.path().value());
                    var rightItem = KTree.findAbsolutItem(this.root, rightClassType.path().value());
                    if (leftItem == null || rightItem == null) {
                        //should not happen
                        Log.temp(checkingRegion, "Element is zero");
                        throw new Log.KarinaException();
                    }
                    if (leftItem instanceof KTree.KInterface kInterface) {
                        yield canAssignInterface(checkingRegion, kInterface, classType, rightItem, rightClassType, mutable);
                    } else {
                        yield classTypeStrictEquals(checkingRegion, classType, rightClassType, mutable);
                    }

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
            case KType.AnyClass anyClass -> false;
        };
    }


    private boolean canAssignInterface(
            Span checkingRegion,
            KTree.KInterface leftItem,
            KType.ClassType left,
            KTree.KItem rightItem,
            KType.ClassType right,
            boolean mutable
            ) {


        if (rightItem instanceof KTree.KInterface rightInterface) {
            //case to check if both are interfaces
            //TODO check if interface rightInterface is a supertype of leftItem, (interface extension)
            //TODO dont forget to replace generics
            return classTypeStrictEquals(checkingRegion, left, right, mutable);
        } else if (rightItem instanceof KTree.KStruct kStruct) {

            //replace generics:
            if (kStruct.generics().size() != right.generics().size()) {
                //should not happen
                Log.temp(checkingRegion, "Generics size mismatch");
                throw new Log.KarinaException();
            }
            HashMap<Generic, KType> mapped = new HashMap<>();
            for (var i = 0; i < right.generics().size(); i++) {
                var generic = rightItem.generics().get(i);
                var type = right.generics().get(i);
                mapped.put(generic, type);
            }

            for (var implBlock : kStruct.implBlocks()) {
                var replaced = AttributionExpr.replaceType(implBlock.type(), mapped);
                if (replaced instanceof KType.ClassType implClassType) {
                    //TODO also check interface extension
                    if (classTypeStrictEquals(checkingRegion, left, implClassType, mutable)) {
                        return true;
                    }
                } else {
                    //should not happen
                }
            }
            return false;
            //tests if implements
        } else {
            return false;
        }
    }

    private boolean classTypeStrictEquals(Span checkingRegion, KType.ClassType classType, KType.ClassType rightClassType, boolean mutable) {

        if (!classType.path().equals(rightClassType.path())) {
            return false;
        }
        if (rightClassType.generics().size() != classType.generics().size()) {
            //should not happen
            Log.temp(checkingRegion, "Generics size mismatch");
            throw new Log.KarinaException();
        }

        for (var i = 0; i < rightClassType.generics().size(); i++) {
            if (!canAssign(checkingRegion, classType.generics().get(i), rightClassType.generics().get(i), mutable)) {
                return false;
            }
        }
        return true;
    }


}
