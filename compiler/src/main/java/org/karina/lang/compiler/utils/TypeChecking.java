package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public record TypeChecking(Model model) {

    /**
     * Returns the most common super type of the two types.
     */

    public @Nullable KType superType(IntoContext c, Region checkingRegion, KType a, KType b) {
        return superTypeInner(c.intoContext(), checkingRegion, a, b);
    }

    private @Nullable KType superTypeInner(Context c, Region checkingRegion, KType a, KType b) {
        var mutable = true;
        if (canAssign(c, checkingRegion, a, b, mutable)) {
            Log.recordType(Log.LogTypes.BRANCH, "Most common super type found for " + a + " and " + b + " is " + a);
            return a;
        } else if (canAssign(c, checkingRegion, b, a, mutable)) {
            Log.recordType(Log.LogTypes.BRANCH, "Most common super type found for " + a + " and " + b + " is " + b);
            return b;
        }

        //TODO implement functions
        a = a.unpack();
        b = b.unpack();

        //TODO check for common interfaces
        if (a instanceof KType.FunctionType && b instanceof KType.FunctionType) {
            return KType.ROOT;
        }
        //when we have arrays, check for comment inner type, when not primitive
        if (a instanceof KType.ArrayType(var innerA) && b instanceof KType.ArrayType(var innerB)) {
            if (innerA.isPrimitive() || innerB.isPrimitive()) {
                return KType.ROOT;
            }

            var inner = superType(c, checkingRegion, a, b);
            if (inner == null) {
                return KType.ROOT;
            }
            return new KType.ArrayType(inner);

        }

        //TODO when generics bounds implemented, return the most common super type, from left to right


        if (!(a instanceof KType.ClassType aClass) || !(b instanceof KType.ClassType bClass)) {
            return null;
        }
        var interfacesA = new ArrayList<KType.ClassType>();
        var interfacesB = new ArrayList<KType.ClassType>();

        var aSuperTypes = getSuperTypes(c, aClass, interfacesA);
        var bSuperTypes = getSuperTypes(c, bClass, interfacesB);

        //can be replaced with a while loop with an early return,
        // without having to test the rest of the super types, but kept for debugging
        for (var current : bSuperTypes) {
            if (current.isRoot()) {
                break; //skip, we might find a common interface. Object is the fallback anyway
            }
            for (KType.ClassType currentSuper : aSuperTypes) {
                if (classStrictEquals(c, checkingRegion, currentSuper, current, mutable)) {
                    Log.recordType(Log.LogTypes.BRANCH, "Most common super type found for " + a + " and " + b + " is " + current);
                    return current;
                }
            }
        }

        //add interfaces, of the base class
        // superclass interfaces are already added by the 'getSuperTypes' call
        putInterfaces(c, aClass, interfacesA);
        putInterfaces(c, bClass, interfacesB);
        //TODO Not breadth first, so not most common, fix this

        for (var interfaceA : interfacesA) {
            for (var interfaceB : interfacesB) {
                if (classStrictEquals(c, checkingRegion, interfaceA, interfaceB, mutable)) {
                    Log.recordType(Log.LogTypes.BRANCH, "Common super type found for " + a + " and " + b + " is " + interfaceA);
                    return interfaceA;
                }
            }
        }

        Log.recordType(Log.LogTypes.BRANCH,"No common super type found for " + a + " and " + b);
        Log.recordType(Log.LogTypes.BRANCH, "Interfaces of A: ", interfacesA);
        Log.recordType(Log.LogTypes.BRANCH, "Interfaces of B: ", interfacesB);
        return KType.ROOT;

    }

    //TODO ensure in the import stage, that no common interface are present with different generics
    // in the whole tree (supertype interfaces included)
    /**
     * Returns the super types of the class
     * Can fill the collection with the interfaces of the superclass, when not null
     */
    private List<KType.ClassType> getSuperTypes(Context c, KType.ClassType cls, @Nullable List<KType.ClassType> collection) {
        var superTypes = new ArrayList<KType.ClassType>();

        var superType = Types.getSuperType(c, this.model, cls);
        while (superType != null) {
            if (collection != null) {
                putInterfaces(c, superType, collection);
            }
            superTypes.add(superType);
            superType = Types.getSuperType(c, this.model, superType);
        }

        return superTypes;
    }

    /**
     * (breadth first) recursive search for the interfaces of the class
     */
    private void putInterfaces(Context c, KType.ClassType cls, List<KType.ClassType> collection) {

        var interfaces = Types.getInterfaces(c, this.model, cls);
        for (var anInterface : interfaces) {
            if (collection.contains(anInterface)) {
                continue;
            }
            collection.add(anInterface);
        }
        for (var anInterface : interfaces) {
            if (collection.contains(anInterface)) {
                continue;
            }
            putInterfaces(c, anInterface, collection);
        }
    }

    /**
     * Returns true if the right fieldType can be assigned to the left fieldType.
     * Left is the expected fieldType, and right can be a supertype of left.
     * {@code right <: left}
     * <p>
     * Example:
     * {@code let a: left = right}
     */
    public boolean canAssign(IntoContext c, Region checkingRegion, KType left, KType right, boolean mutable) {
        var logName = "type-checking (" + left + " from " + right + ")" + (mutable ? " mutable" : "");
        Log.beginType(Log.LogTypes.CHECK_TYPE, logName);
        var resultInner = canAssignInner(c.intoContext(), checkingRegion, left, right, mutable);
        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "result: " + resultInner, checkingRegion, "left: " + left, "right: " + right);
        return resultInner;
    }

    private boolean canAssignInner(Context c, Region checkingRegion, KType left, KType right, boolean mutable) {
        var logName = left + " from " + right;
        Log.beginType(Log.LogTypes.CHECK_TYPE, logName);

        if (left instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                var resultInner = canAssignInner(c, checkingRegion, resolvable.get(), right, mutable);
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Left Resolvable resolved", resultInner);
                return resultInner;
            } else {
                var canResolve = resolvable.canResolve(c, checkingRegion, right);
                if (mutable && canResolve) {
                    resolvable.tryResolve(c, checkingRegion, right);
                    //we dont test again if it was resolved, since the resolvable might reference itself
                }

                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Trying to resolve left", canResolve);
                return canResolve;
            }
        } else if (right instanceof KType.Resolvable resolvable) {
            if (resolvable.isResolved()) {
                var resultInner = canAssignInner(c, checkingRegion, left, resolvable.get(), mutable);
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Right Resolvable resolved", resultInner);
                return resultInner;
            } else {
                var canResolve = resolvable.canResolve(c, checkingRegion, left);
                if (mutable && canResolve) {
                    resolvable.tryResolve(c, checkingRegion, left);
                    //we dont test again if it was resolved, since the resolvable might reference itself
                }
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Trying to resolve right", canResolve);
                return canResolve;
            }
        }

        //TODO Array with java.lang.Object
        // functions with java.lang.Object
        // generics with java.lang.Object

        return switch (left) {
            case KType.ArrayType arrayType -> {
                if (right instanceof KType.ArrayType(KType elementType)) {
                    var resultInner = canAssignInner(
                            c,
                            checkingRegion,
                            arrayType.elementType(),
                            elementType,
                            mutable
                    );
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Array match", resultInner);
                    yield resultInner;
                } else {
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Array mismatch", false);
                    yield false;
                }
            }
            case KType.ClassType classType -> {
                var isObject = classType.pointer().isRoot();

                if (right instanceof KType.ClassType rightClassType) {
                    var classCheck = canAssignClass(c, checkingRegion, classType, rightClassType, mutable);
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Class match", classCheck);
                    yield classCheck;
                } else if (right instanceof KType.GenericLink) {
                    if (isObject) {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Object match with generic", true);
                        yield true;
                    } else {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Class mismatch with generics", false);
                        yield false;
                    }
                } else if (right instanceof KType.FunctionType functionType) {
                    if (isObject) {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Object match with function", true);
                        yield true;
                    }
                    var resultInner = canAssignFunctionToClass(c, checkingRegion, classType, functionType, mutable);
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Class match via Function interface", resultInner);
                    yield resultInner;
                } else if (right instanceof KType.ArrayType) {
                    if (isObject) {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Object match with Array", true);
                    } else {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Class mismatch with Array", false);
                    }
                    yield isObject;
                }else {
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Class mismatch, right side is not a class, generic, function or array", false);
                    yield false;
                }
            }
            case KType.FunctionType functionType -> {
                if (right instanceof KType.FunctionType rightFunctionType) {
                    if (functionType.arguments().size() != rightFunctionType.arguments().size()) {
                        Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Function arguments size mismatch", false);
                        yield false;
                    }
                    for (int i = 0; i < functionType.arguments().size(); i++) {
                        if (!canAssignInner(c, checkingRegion, functionType.arguments().get(i), rightFunctionType.arguments().get(i), mutable)) {
                            Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Function arguments mismatch", false);
                            yield false;
                        }
                    }
                    //test all interfaces, direct and indirect
                    var leftReturnType = functionType.returnType();
                    var rightReturnType = rightFunctionType.returnType();
                    var returnResult = canAssignInner(c, checkingRegion, leftReturnType, rightReturnType, mutable);
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Function return match",  returnResult);
                    yield returnResult;
                } else {
                    Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Function mismatch", false);
                    yield false;
                }
            }
            case KType.GenericLink genericLink -> {
                var resultInner = right instanceof KType.GenericLink(Generic link) &&
                        genericLink.link().equals(link);
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "GenericLink match ", resultInner);
                yield resultInner;
            }
            case KType.PrimitiveType primitiveType -> {
                var resultInner = right instanceof KType.PrimitiveType(KType.KPrimitive primitive) &&
                        primitiveType.primitive() == primitive;
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "Primitive match ", resultInner);
                yield resultInner;
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.endType(Log.LogTypes.CHECK_TYPE, logName, "UnprocessedType match", false);
                Log.temp(c, unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
            case KType.VoidType voidType -> right.isVoid();
            //should not happen
            case KType.Resolvable resolvable -> false;
        };

    }

    private boolean canAssignFunctionToClass(Context c, Region checkingRegion, KType.ClassType left, KType.FunctionType right, boolean mutable) {
        var classModelLeft = this.model.getClass(left.pointer());

        if (Modifier.isInterface(classModelLeft.modifiers())) {
            for (var anInterface : right.interfaces()) {
                if (anInterface instanceof KType.ClassType rightInterfaceClassType) {
                  //  Log.recordType(Log.LogTypes.CHECK_TYPE, "test if interface " + left + " implements " + rightInterfaceClassType);
                   // var replacedInterface = Types.projectGenerics(this.model, left, rightInterfaceClassType);

                    if (doesImplementInterface(c, checkingRegion, left, rightInterfaceClassType, mutable)) {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    private boolean canAssignClass(Context c, Region checkingRegion, KType.ClassType left, KType.ClassType right, boolean mutable) {

        if (classStrictEquals(c, checkingRegion, left, right, mutable)) {
            return true;
        }

        var classModelLeft = this.model.getClass(left.pointer());

        if (Modifier.isInterface(classModelLeft.modifiers())) {
            return doesImplementInterface(c, checkingRegion, left, right, mutable);
        } else {
            return isSuperClassOf(c, checkingRegion, left, right, mutable);
        }

    }

    private boolean doesImplementInterface(Context c, Region checkingRegion, KType.ClassType interfaceModel, KType.ClassType right, boolean mutable) {
        if (classStrictEquals(c, checkingRegion, interfaceModel, right, mutable)) {
            return true;
        }

        var rightModel = this.model.getClass(right.pointer());
        Log.recordType(Log.LogTypes.CHECK_TYPE,"test if interface " + right + " implements " + interfaceModel + " with " + rightModel.interfaces().size() + " interface(s)");
        for (var interfaceOfRight : rightModel.interfaces()) {

            var replacedInterface = Types.projectGenerics(c, this.model, right, interfaceOfRight);

            if (doesImplementInterface(c, checkingRegion, interfaceModel, replacedInterface, mutable)) {
                return true;
            }
        }
        return false;
    }


    private boolean isSuperClassOf(Context c, Region checkingRegion, KType.ClassType left, KType.ClassType right, boolean mutable) {

        if (classStrictEquals(c, checkingRegion, left, right, mutable)) {
            return true;
        }

        var superClass = Types.getSuperType(c, this.model, right);
        if (superClass == null) {
            return false;
        }

        return isSuperClassOf(c, checkingRegion, left, superClass, mutable);
    }



    /**
     * Strict equals for classes, checks if the classes are the same, and if the generics are the same
     */
    private boolean classStrictEquals(Context c, Region checkingRegion, KType.ClassType left, KType.ClassType right, boolean mutable) {
        Log.beginType(Log.LogTypes.CHECK_TYPE, "strict equals check");
        Log.recordType(Log.LogTypes.CHECK_TYPE, "Class strict equals " + left + " and " + right);
        if (left.pointer().equals(right.pointer())) {
            if (left.generics().size() != right.generics().size()) {
                Log.record("(warn) Generic mismatch, probably a Java side issue");
                Log.temp(c, checkingRegion, "Generics size mismatch, should not happen");
                Log.temp(c, left.pointer().region(), "Left of mismatch, with " + left.generics().size() + " generics");
                Log.temp(c, right.pointer().region(), "Right of mismatch, with " + right.generics().size() + " generics");
                throw new Log.KarinaException();
            }
            for (var i = 0; i < left.generics().size(); i++) {
                var leftGeneric = left.generics().get(i);
                var rightGeneric = right.generics().get(i);
                if (!canAssignInner(c, checkingRegion, leftGeneric, rightGeneric, mutable)) {
                    Log.recordType(Log.LogTypes.CHECK_TYPE, "cannot assign generics", leftGeneric, "from", rightGeneric);
                    Log.endType(Log.LogTypes.CHECK_TYPE, "strict equals check", false);
                    return false;
                }
            }

            Log.endType(Log.LogTypes.CHECK_TYPE, "strict equals check", true);
            return true;
        }
        Log.endType(Log.LogTypes.CHECK_TYPE, "strict equals check", false, "Classes are not the same");
        return false;
    }


}
