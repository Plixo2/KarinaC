package org.karina.lang.compiler.stages.lower;

import karina.lang.Result;
import org.apache.commons.lang3.mutable.MutableInt;
import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public record LoweringContext(
        ClassLookup newClasses, // mutable
        MutableInt syntheticCounter,
        Model model,
        MethodModel owningMethod,
        List<ClassModel> outerClasses
) {

    public KType.ClassType getOrCreateInterface(KType.FunctionType functionType) {
        throw new NullPointerException("Not implemented");
    }

}
