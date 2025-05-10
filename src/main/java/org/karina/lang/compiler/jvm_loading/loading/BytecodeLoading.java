package org.karina.lang.compiler.jvm_loading.loading;

import org.karina.lang.compiler.logging.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.jar.JarFile;

public class BytecodeLoading {

    public static OpenSet loadJarFile(JarFile jarFile, OpenSet openSet) throws IOException {

        var entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                try (var inputStream = jarFile.getInputStream(entry)) {
                    var reader = new ClassReader(inputStream);
                    var classNode = new ClassNode();
                    var sample = Log.addSuperSample("ASM_PARSE");
                    reader.accept(
                            classNode,
                            ClassReader.SKIP_FRAMES
                            //we cant skip code, as this skips local variables,
                            // which we need for recovering parameter names.
                            // Not skipping code is ~40% slower
                    );
                    sample.endSample();
                    if (!classNode.name.equals("module-info")) {
                        openSet.add(classNode, entry.getRealName());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        return openSet;
    }


}
