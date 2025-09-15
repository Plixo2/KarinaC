package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;

/**
 * Rewrites the IR into different expressions that need extra processing before code generation
 */
public class LoweringProcessor {

    public Model lowerTree(Context c, Model model) throws Log.KarinaException {
        var build = new ModelBuilder();
        try (var fork = c.fork()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                fork.collect(subC -> {
                    LoweringItem.lowerClass(subC, model, null, kClassModel, build);
                    //return null, and mutate thread-safe ModelBuilder
                    return null;
                });
            }
            var _ = fork.dispatchParallel();
        }
        for (var bytecodeClass : model.getBinaryClasses()) {
            build.addClass(c, bytecodeClass);
        }


        return build.build(c);
    }
}
