package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.jar.*;

/**
 * Generates Bytecode that can be written to disk
 */
public class GenerationProcessor {
    //Java 21
    public static final int CLASS_VERSION = Opcodes.V21;



    public JarCompilation compileTree(Context c, Model model, String mainClass) {

        List<JarCompilation.JarOutput> files;

        try (var fork = c.<JarCompilation.JarOutput>fork()) {
            for (var kClassModel : model.getUserClasses()) {
                fork.collect(subC -> {
                    Log.beginType(Log.LogTypes.GENERATION_CLASS, "Class " + kClassModel.path());
                    var out = GenerateItem.compileClass(subC, model, kClassModel, CLASS_VERSION);
                    Log.recordType(Log.LogTypes.GENERATION_CLASS, "Generated", kClassModel.path(), "to", out.path());
                    Log.endType(Log.LogTypes.GENERATION_CLASS, "Class " + kClassModel.path());
                    return out;
                });
            }
            files = fork.dispatchParallel();
        }


        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Karina-Version"), KarinaCompiler.VERSION);

        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, "karina_base.jar");


        return new JarCompilation(
                files,
                manifest
        );
    }



}
