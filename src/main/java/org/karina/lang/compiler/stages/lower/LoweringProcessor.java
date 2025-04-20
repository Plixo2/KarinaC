package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;

public class LoweringProcessor {

    public Model lowerTree(Model model) throws Log.KarinaException {
        var build = new ModelBuilder();
        try (var collector = new ErrorCollector()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                collector.collect(() -> {
                    LoweringItem.lowerClass(model, null, kClassModel, build);
                });
            }
            for (var bytecodeClass : model.getBinaryClasses()) {
                build.addClass(bytecodeClass);
            }
        }


        return build.build();
    }
}
