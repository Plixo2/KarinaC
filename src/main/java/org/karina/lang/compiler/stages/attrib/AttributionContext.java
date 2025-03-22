package org.karina.lang.compiler.stages.attrib;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.utils.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// Method scope is the enclosing method or function expression
public record AttributionContext(
        Model model,
        @Nullable Variable selfVariable,
        boolean isLoop,
        @Nullable MethodModel owningMethod,
        ClassPointer owningClass,
        KType returnType,
        VariableCollection variables,
        StaticImportTable table,
        TypeChecking checking,
        ProtectionChecking protection
) {
    /**
     * Unboxing and boxing of primitive types, see {@link #boxing} and {@link #unboxing}
     */

    private final static Map<KType.KPrimitive, PrimitiveUnboxing> BOX_MAPPING = Map.of(
            org.karina.lang.compiler.objects.KType.KPrimitive.INT, new PrimitiveUnboxing(
                    org.karina.lang.compiler.objects.KType.INTEGER.pointer(), "intValue", true),
            KType.KPrimitive.LONG, new PrimitiveUnboxing(KType.LONG.pointer(), "longValue", true),
            KType.KPrimitive.DOUBLE, new PrimitiveUnboxing(KType.DOUBLE.pointer(), "doubleValue", true),
            KType.KPrimitive.FLOAT, new PrimitiveUnboxing(KType.FLOAT.pointer(), "floatValue", true),
            KType.KPrimitive.BOOL, new PrimitiveUnboxing(KType.BOOLEAN.pointer(), "booleanValue", false)
    );

    public AttributionContext setInLoop(boolean isLoop) {
        return new AttributionContext(
                this.model, this.selfVariable, isLoop, this.owningMethod, this.owningClass, this.returnType, this.variables, this.table,
                this.checking, this.protection
        );
    }

    public AttributionContext addVariable(Variable variable) {
        if (variable.name().equals("_")) {
            Log.warn("Variable name '_' is reserved for ignored variables, this should not happen");
        }
        if (this.variables.contains(variable.name())) {
            var existingVariable = Objects.requireNonNull(this.variables.get(variable.name()));
            Log.attribError(
                    new AttribError.DuplicateVariable(
                            existingVariable.region(),
                            variable.region(),
                            variable.name()
                    ));
            throw new Log.KarinaException();
        }
        return new AttributionContext(
                this.model, this.selfVariable, this.isLoop,this.owningMethod, this.owningClass, this.returnType,
                this.variables.add(variable), this.table, this.checking, this.protection
        );
    }

    public AttributionContext markImmutable(Variable variable) {
        return new AttributionContext(
                this.model, this.selfVariable, this.isLoop ,this.owningMethod, this.owningClass, this.returnType,
                this.variables.markImmutable(variable), this.table, this.checking, this.protection
        );
    }

    public boolean isMutable(Variable variable) {
        return this.variables.isMutable(variable);
    }

//    public boolean canEscapeNonMutable(Variable variable) {
//        return this.variables.canEscapeNonMutable(variable);
//    }

    public @Nullable KExpr getConversion(Region checkingRegion, KType left, KExpr right, boolean mutate, boolean debug) {
        var rightType = right.type();
        left = left.unpack();

        var unwrapped = unboxing(checkingRegion, left, right, mutate, debug);
        if (unwrapped != null) {
            return unwrapped;
        }
        var boxed = boxing(checkingRegion, left, right, mutate, debug);
        if (boxed != null) {
            return boxed;
        }

        if (!this.checking.canAssign(checkingRegion, left, rightType, false)) {
            return null;
        }
        if (mutate) {
            //yes this is intentional this way
            this.checking.canAssign(checkingRegion, left, rightType, true);
        }
        return right;
    }

    /**
     * can convert from one type to another (implicit conversion)
     */
    public KExpr makeAssignment(Region checkingRegion, KType left, KExpr right) {
        var sample = Log.addSuperSample("ASSIGNMENT");
        var rightType = right.type();
        var conversion = getConversion(checkingRegion, left, right, true, true);
        if (conversion == null) {
            Log.attribError(new AttribError.TypeMismatch(checkingRegion, left, rightType));
            throw new Log.KarinaException();
        }
        sample.endSample();
        return conversion;
    }

    private @Nullable KExpr unboxing(Region checkingRegion, KType left, KExpr right, boolean mutate, boolean debug) {
        var rightType = right.type();

        if (!(left instanceof KType.PrimitiveType(var primitive))) {
            return null;
        }

        if (rightType instanceof KType.Resolvable resolvable && !resolvable.isResolved() && !resolvable.canUsePrimitives()) {
            var unboxFrom = BOX_MAPPING.get(primitive);
            if (unboxFrom != null) {
                var toType = new KType.ClassType(unboxFrom.pointer, List.of());
                //we just say, that the type is now a Boxed version, and let the makeAssignment handle the rest
                resolvable.tryResolve(checkingRegion, toType);

                return makeAssignment(checkingRegion, left, right);
            }
        }

        if (!(rightType instanceof KType.ClassType classType)) {
            return null;
        }

        var unboxing = BOX_MAPPING.get(primitive);
        if (unboxing == null) {
            return null;
        }

        var classPointer = unboxing.pointer;
        var isBoxed =
                classType.pointer().equals(classPointer) ||
                (classType.pointer().path().equals(ClassPointer.NUMBER_PATH) && unboxing.isNumber);

        if (isBoxed) {

            if (debug) {
                Log.recordType(Log.LogTypes.IMPLICIT_CONVERSION, "(ok) Implicit conversion from " + classPointer.path() + " to " + primitive);
            }

            var pointer = MethodPointer.of(
                    right.region(),
                    classPointer,
                    unboxing.unboxingMethod,
                    new Signature(
                            ImmutableList.of(),
                            new KType.PrimitiveType(primitive)
                    )
            );

            var call = new KExpr.Call(
                    right.region(),
                    right,
                    //should be 'right', not via GetMember, as CallAttrib does that to,
                    // so only the CallSymbol.CallVirtual should be used to the types
                    // from the expression and to compile this call
                    List.of(), //generics
                    List.of(), //arguments
                    new CallSymbol.CallVirtual(
                            pointer,
                            List.of(), //generics
                            new KType.PrimitiveType(primitive)
                    )
            );

            //check again
            return getConversion(checkingRegion, left, call, mutate, debug);
        }

        return null;
    }

    private @Nullable KExpr boxing(Region checkingRegion, KType left, KExpr right, boolean mutate, boolean debug) {
        var rightType = right.type();


        if (!(rightType instanceof KType.PrimitiveType(var primitive))) {
            return null;
        }

        //this should cover generics as well
        if (left instanceof KType.Resolvable resolvable && !resolvable.isResolved() && !resolvable.canUsePrimitives()) {
            var boxed = BOX_MAPPING.get(primitive);
            if (boxed == null) {
                return null;
            }

            var classTypeToConvert = new KType.ClassType(boxed.pointer, List.of());
            var call = createBoxingCall(right, classTypeToConvert, primitive, debug);

            //it should be resolvable, even tho currently, this should not happen
            if (!resolvable.canResolve(checkingRegion, classTypeToConvert)) {
                return null;
            }
            resolvable.tryResolve(checkingRegion, classTypeToConvert);
            //to prevent infinite loops, even tho currently, this should not happen
            if (!resolvable.isResolved()) {
                return null;
            }

            //check again
            return getConversion(checkingRegion, left, call, mutate, debug);
        }


        if (!(left instanceof KType.ClassType classType)) {
            return null;
        }

        if (shouldBoxTo(classType.pointer(), primitive)) {
            var boxing = BOX_MAPPING.get(primitive);
            //test for void
            if (boxing == null) {
                return null;
            }
            var type = new KType.ClassType(boxing.pointer, List.of());
            var call = createBoxingCall(right, type, primitive, debug);

            //check again
            return getConversion(checkingRegion, left, call, mutate, debug);
        }

        return null;
    }

    private boolean shouldBoxTo(ClassPointer expectedClassType, KType.KPrimitive givenPrimitive) {

        if (expectedClassType.path().equals(ClassPointer.NUMBER_PATH)) {
            return givenPrimitive.isNumeric();
        } else if (expectedClassType.isRoot()) {
            return true;
        }
        var unboxing = BOX_MAPPING.get(givenPrimitive);
        if (unboxing == null) {
            return false;
        }
        return expectedClassType.path().equals(unboxing.pointer.path());
    }

    /**
     * Construct a call to a boxing method
     */
    private static KExpr.Call createBoxingCall(
            KExpr right,
            KType.ClassType classType,
            KType.KPrimitive primitive,
            boolean debug
    ) {

        if (debug) {
            Log.recordType(Log.LogTypes.IMPLICIT_CONVERSION,"(ok) Implicit conversion from " + primitive + " to " + classType.pointer());
        }

        var pointer = MethodPointer.of(
                right.region(),
                classType.pointer(),
                "valueOf",
                new Signature(
                        ImmutableList.of(new KType.PrimitiveType(primitive)), classType
                )
        );

        //dummy, we dont need this object for checks or compilation
        var dummy = new KExpr.StringExpr(right.region(), "this should not be visible", false);


        return new KExpr.Call(
                right.region(),
                dummy, //should be ignored, when using CallStatic after attributing
                List.of(), //generics
                List.of(right),
                new CallSymbol.CallStatic(
                        pointer,
                        List.of(), //generics
                        classType
                )
        );
    }

    //helper records
    record PrimitiveUnboxing(ClassPointer pointer, String unboxingMethod, boolean isNumber) { }

}
