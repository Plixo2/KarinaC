package org.karina.lang.compiler.jvm_loading.loading;

import org.jetbrains.annotations.Contract;
import org.karina.lang.compiler.utils.logging.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BytecodeLoading {

    @Contract(mutates = "param2")
    public static OpenSet loadJarFile(JarInputStream jar, OpenSet openSet) throws IOException {

        JarEntry entry;

        while ((entry = jar.getNextJarEntry()) != null) {
            if (entry.getName().endsWith(".class")) {
                var reader = new ClassReader(jar);
                var classNode = new ClassNode();
                reader.accept(
                        classNode,
                        ClassReader.SKIP_FRAMES
                        //we cant skip code, as this skips local variables,
                        // which we need for recovering parameter names.
                        // Not skipping code is ~40% slower
                );
                if (classNode.name.equals("module-info") || classNode.name.equals("package-info")) {
                    continue;
                }
                openSet.add(classNode, entry.getRealName());
            }
        }


        return openSet;
    }


}
