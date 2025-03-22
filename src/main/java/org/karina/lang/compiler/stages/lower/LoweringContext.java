package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;

public record LoweringContext(
        ClassLookup newClasses, // mutable
        Model model,
        MethodModel owningMethod,
        ClassModel owningClass
) {

}
