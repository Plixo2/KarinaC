package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.*;

/**
 * Generates Bytecode that can be written to disk
 */
public class GenerationProcessor {
    //Java 17
    public static final int CLASS_VERSION = 65;

    public JarCompilation compileTree(Model model, String mainClass) {

        List<JarCompilation.JarOutput> files = new ArrayList<>();

        try (var collector = new ErrorCollector()) {
            for (var kClassModel : model.getUserClasses()) {
                Log.beginType(Log.LogTypes.GENERATION, kClassModel.name());
                if (Log.LogTypes.GENERATION.isVisible()) {
                    files.add(GenerateItem.compileClass(model, kClassModel, CLASS_VERSION));
                } else {
                    collector.collect(() -> {
                        files.add(GenerateItem.compileClass(model, kClassModel, CLASS_VERSION));
                    });
                }
                Log.endType(Log.LogTypes.GENERATION, kClassModel.name());
            }

            for (var jClassModel: model.getBinaryClasses()) {
              //  files.add(getJarOutput(model, jClassModel.region(), jClassModel.getClassNode()));
            }
        }




        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, "karina_base.jar");


        return new JarCompilation(
                files,
                manifest
        );
    }



}
