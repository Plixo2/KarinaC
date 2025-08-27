package org.karina.lang.compiler.stages.lower;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.table.ClassLookup;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.MemberSymbol;

import java.util.List;

public record LoweringContext(
        ClassLookup newClasses, // mutable
        MutableInt syntheticCounter, // mutable
        Model model,
        Context c,
        MethodModel definitionMethod, //were the Expressions are defined
        MethodModel owningMethod, //might be the same as definitionMethod, but changed when inside a closure
        ClassModel definitionClass, //were the Expressions are defined
        ClassModel owningClass,
        List<ClosureReplacement> toReplace
) implements IntoContext {

    public KType.ClassType getOrCreateInterface(Region region, KType.FunctionType functionType) {
        if (functionType.interfaces().isEmpty()) {
            Log.temp(this, region, "Function type has no interfaces");
            throw new Log.KarinaException();
        }

        if (functionType.interfaces().getFirst() instanceof KType.ClassType classType) {
            return classType;
        }
        Log.temp(this, region, "Function interface is not a class type");
        throw new Log.KarinaException();

    }

    public @Nullable KExpr lowerVariableReference(Region region, Variable reference) {
        for (var closureReplacement : this.toReplace) {
            if (closureReplacement.toReplace.contains(reference)) {
                return makeReference(region, closureReplacement, reference);
            }
        }
        return null;
    }

    private static KExpr makeReference(Region region, ClosureReplacement replacement, Variable reference) {
        var selfExpr = new KExpr.Self(region, replacement.selfReference);
        var varName = reference.name();

        var fieldPointer = FieldPointer.of(
                region, replacement.closure.pointer(), varName
        );
        var memberSymbol = new MemberSymbol.FieldSymbol(
                fieldPointer,
                reference.type(),
                replacement.closure
        );

        return new KExpr.GetMember(region, selfExpr, RegionOf.region(region, varName), false, memberSymbol);
    }

    @Override
    public Context intoContext() {
        return this.c;
    }


    @AllArgsConstructor
    @ToString
    public static class ClosureReplacement {
        public final KType.ClassType closure;
        public final Variable selfReference;
        public final List<Variable> toReplace;
    }
}
