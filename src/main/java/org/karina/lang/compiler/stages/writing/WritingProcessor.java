package org.karina.lang.compiler.stages.writing;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Config;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.utils.Context;

public class WritingProcessor {

   public void writeCompilation(Context c, JarCompilation compilation, @Nullable Config.OutputConfig outputConfig) throws Log.KarinaException {

       if (outputConfig == null) {
           return;
       }
       var outputPath = outputConfig.outputFile();
       Log.record("compiled to " + outputPath.toAbsolutePath());

       Log.begin("jar");
       compilation.writeJar(c, outputPath);
       Log.end("jar");

       if (outputConfig.emitClassFiles()) {
           Log.begin("classes");
           compilation.writeClasses(c, outputPath.getParent().resolve("classes"));
           Log.end("classes");
       }

   }
}
