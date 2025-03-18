package org.karina.lang.compiler.stages.attrib;

import org.karina.lang.compiler.jvm.model.PhaseDebug;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;

public class AttributionProcessor {

    public JKModel attribTree(JKModel model) throws Log.KarinaException {
        var build = new JKModelBuilder(PhaseDebug.TYPED);
        try (var collector = new ErrorCollector()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                collector.collect(() -> {
                    var newClassModel = AttributionItem.attribClass(model, null, kClassModel);
                    build.addClassWithChildren(newClassModel);
                });
            }
            for (var bytecodeClass : model.getBytecodeClasses()) {
                build.addClass(bytecodeClass);
            }
        }


        return build.build();
    }





}
