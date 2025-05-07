package org.karina.lang.compiler.stages.attrib;

import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

/**
 * Responsible for type checking and gathering information about the program for lowering and code generation.
 */
public class AttributionProcessor {

    public Model attribTree(Model model) throws Log.KarinaException {
        var build = new ModelBuilder();
        try (var collector = new ErrorCollector()) {
            for (var kClassModel : model.getUserClasses()) {

                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                collector.collect(() -> {
                    AttributionItem.attribClass(model, null, kClassModel , build);
                });
            }
            for (var bytecodeClass : model.getBinaryClasses()) {
                build.addClass(bytecodeClass);
            }
        }


        return build.build();
    }





}
