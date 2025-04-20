package org.karina.lang.compiler.stages.lower;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

public record LoweringContext(
        ClassLookup newClasses, // mutable
        MutableInt syntheticCounter, // mutable
        Model model,
        MethodModel definitionMethod, //were the Expressions are defined
        MethodModel owningMethod, //might be the same as definitionMethod, but changed when inside a closure
        ClassModel definitionClass, //were the Expressions are defined
        ClassModel owningClass,
        List<ClosureReplacement> toReplace
) {

    public KType.ClassType getOrCreateInterface(KType.FunctionType functionType) {
        throw new NullPointerException("Not implemented");
    }


    public boolean shouldReplace(Variable variable) {
        return this.toReplace.stream().anyMatch(replacement -> replacement.toReplace.contains(variable));
    }

    public @Nullable KExpr lowerVariableReference(LiteralSymbol.VariableReference variableReference) {
        return lowerVariableReference(variableReference.region(), variableReference.variable());
    }

    public KExpr lowerSelf(KExpr.Self self) {
        if (self.symbol() == null) {
            Log.temp(self.region(), "Self reference is null");
            throw new Log.KarinaException();
        }
        var newRef = lowerVariableReference(self.region(), self.symbol());
        if (newRef != null) {
            return newRef;
        }
        return self;
    }

    private @Nullable KExpr lowerVariableReference(Region region, Variable reference) {
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


    @AllArgsConstructor
    public static class ClosureReplacement {
        KType.ClassType closure;
        Variable selfReference;
        List<Variable> toReplace;
    }
}
